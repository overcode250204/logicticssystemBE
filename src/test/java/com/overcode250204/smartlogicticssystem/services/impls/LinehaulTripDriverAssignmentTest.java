package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripDriverCreateRequest;
import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import com.overcode250204.smartlogicticssystem.enums.DriverStatus;
import com.overcode250204.smartlogicticssystem.enums.DriverType;
import com.overcode250204.smartlogicticssystem.exception.AppException;
import com.overcode250204.smartlogicticssystem.exception.DriverErrorCode;
import com.overcode250204.smartlogicticssystem.mapper.LinehaulTripMapper;
import com.overcode250204.smartlogicticssystem.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Kiểm thử các ràng buộc phân công tài xế cho chuyến linehaul:
 *  - Không trùng tài xế trong cùng chuyến (chính kiêm phụ / phụ trùng).
 *  - Tối đa một tài xế chính.
 *  - Không chồng lấn chuyến active khác.
 */
@ExtendWith(MockitoExtension.class)
class LinehaulTripDriverAssignmentTest {

    @Mock private LinehaulTripRepository linehaulTripRepository;
    @Mock private LinehaulTripDriverRepository linehaulTripDriverRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private RouteConfigRepository routeConfigRepository;
    @Mock private LinehaulTripMapper linehaulTripMapper;
    @Mock private PalletRepository palletRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private com.overcode250204.smartlogicticssystem.services.LiveTrackingCache liveTrackingCache;
    @Mock private com.overcode250204.smartlogicticssystem.vrp.OsrmRoutingService osrmRoutingService;

    @InjectMocks private LinehaulTripServiceImpl service;

    private static final long WAREHOUSE_ID = 100L;

    private Driver driver(long id) {
        Warehouse wh = new Warehouse();
        wh.setWarehouseId(WAREHOUSE_ID);
        Driver d = new Driver();
        d.setDriverId(id);
        d.setName("Driver " + id);
        d.setDriverType(DriverType.LINEHAUL);
        d.setStatus(DriverStatus.AVAILABLE);
        d.setCurrentWarehouse(wh);
        return d;
    }

    private LinehaulTripCreateRequest requestWith(List<LinehaulTripDriverCreateRequest> drivers) {
        LinehaulTripCreateRequest req = new LinehaulTripCreateRequest();
        req.setRouteId(1L);
        req.setVehicleId(null);
        req.setLinehaulTripDriverCreateRequest(drivers);
        return req;
    }

    private LinehaulTripDriverCreateRequest dr(long id, DriverRole role) {
        LinehaulTripDriverCreateRequest r = new LinehaulTripDriverCreateRequest();
        r.setDriverId(id);
        r.setRole(role);
        return r;
    }

    @BeforeEach
    void setUp() {
        RouteConfig route = new RouteConfig();
        Warehouse from = new Warehouse();
        from.setWarehouseId(WAREHOUSE_ID);
        route.setFromWarehouse(from);
        lenient().when(routeConfigRepository.findById(1L)).thenReturn(Optional.of(route));
        lenient().when(driverRepository.findById(anyLong()))
                .thenAnswer(inv -> Optional.of(driver(inv.getArgument(0))));
        lenient().when(driverRepository.findByIdForUpdate(anyLong()))
                .thenAnswer(inv -> Optional.of(driver(inv.getArgument(0))));
        // Mặc định: không dính chuyến active nào khác.
        lenient().when(linehaulTripDriverRepository.findActiveAssignmentsForDriver(anyLong(), anyList(), any()))
                .thenReturn(List.of());
    }

    @Test
    void sameDriverAsMainAndAssistant_throwsDuplicate() {
        AppException ex = assertThrows(AppException.class, () -> service.create(
                requestWith(List.of(dr(5L, DriverRole.MAIN), dr(5L, DriverRole.ASSISTANT))),
                1, 1));
        assertEquals(DriverErrorCode.DRIVER_DUPLICATE_IN_TRIP, ex.getErrorCode());
    }

    @Test
    void duplicateAssistant_throwsDuplicate() {
        AppException ex = assertThrows(AppException.class, () -> service.create(
                requestWith(List.of(dr(7L, DriverRole.ASSISTANT), dr(7L, DriverRole.ASSISTANT))),
                1, 1));
        assertEquals(DriverErrorCode.DRIVER_DUPLICATE_IN_TRIP, ex.getErrorCode());
    }

    @Test
    void twoMainDrivers_throwsMultipleMain() {
        AppException ex = assertThrows(AppException.class, () -> service.create(
                requestWith(List.of(dr(1L, DriverRole.MAIN), dr(2L, DriverRole.MAIN))),
                1, 1));
        assertEquals(DriverErrorCode.MULTIPLE_MAIN_DRIVERS, ex.getErrorCode());
    }

    @Test
    void driverOnAnotherActiveTrip_throwsAlreadyAssigned() {
        when(linehaulTripDriverRepository.findActiveAssignmentsForDriver(eq(9L), anyList(), any()))
                .thenReturn(List.of(new LinehaulTripDriver()));

        AppException ex = assertThrows(AppException.class, () -> service.create(
                requestWith(List.of(dr(9L, DriverRole.MAIN))),
                1, 1));
        assertEquals(DriverErrorCode.DRIVER_ALREADY_ON_ANOTHER_TRIP, ex.getErrorCode());
    }

    @Test
    void validMainAndAssistant_passesValidation() {
        when(linehaulTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> service.create(
                requestWith(List.of(dr(1L, DriverRole.MAIN), dr(2L, DriverRole.ASSISTANT))),
                1, 1));
    }

    // TC-DRIVER-032 (unit proxy): khoá ghi Driver được lấy TRƯỚC khi kiểm tra
    // active assignment — điều kiện cần để hai request đồng thời bị tuần tự hoá.
    @Test
    void lockAcquiredBeforeActiveAssignmentCheck() {
        when(linehaulTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service.create(requestWith(List.of(dr(3L, DriverRole.MAIN))), 1, 1);

        org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(
                driverRepository, linehaulTripDriverRepository);
        inOrder.verify(driverRepository).findByIdForUpdate(3L);
        inOrder.verify(linehaulTripDriverRepository)
                .findActiveAssignmentsForDriver(eq(3L), anyList(), any());
    }

    // ---- Departure revalidation (updateStatusToCanStart = cổng "xuất bến") ----

    private com.overcode250204.smartlogicticssystem.entities.LinehaulTrip preparingTrip(
            long tripId, List<com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver> tds) {
        var trip = new com.overcode250204.smartlogicticssystem.entities.LinehaulTrip();
        trip.setLinehaulId(tripId);
        trip.setStatus(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus.PREPARING);
        trip.setVehicle(new com.overcode250204.smartlogicticssystem.entities.Vehicle());
        trip.setTripDrivers(tds);
        return trip;
    }

    private com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver td(long driverId, DriverRole role) {
        var t = new com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver();
        t.setDriver(driver(driverId));
        t.setRole(role);
        return t;
    }

    // TC-DRIVER-034
    @Test
    void departure_validTrip_succeeds() {
        var pallet = new com.overcode250204.smartlogicticssystem.entities.Pallet();
        pallet.setStatus(com.overcode250204.smartlogicticssystem.enums.PalletStatus.CAN_SEAL);
        var palletItem = new com.overcode250204.smartlogicticssystem.entities.PalletItem();
        pallet.setPalletItems(new java.util.ArrayList<>(List.of(palletItem)));
        var trip = preparingTrip(10L, new java.util.ArrayList<>(List.of(td(1L, DriverRole.MAIN))));
        trip.setPallets(List.of(pallet));
        // can-start yêu cầu routeConfig có fromWarehouse.
        var route = new com.overcode250204.smartlogicticssystem.entities.RouteConfig();
        var fromWh = new com.overcode250204.smartlogicticssystem.entities.Warehouse();
        fromWh.setWarehouseId(WAREHOUSE_ID);
        route.setFromWarehouse(fromWh);
        trip.setRouteConfig(route);
        when(linehaulTripRepository.findById(10L)).thenReturn(Optional.of(trip));
        when(linehaulTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.updateStatusToCanStart(10L, 1, 1));
        assertEquals(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus.CAN_START, trip.getStatus());
    }

    // TC-DRIVER-035
    @Test
    void departure_duplicateAssignment_blocked() {
        var trip = preparingTrip(11L, new java.util.ArrayList<>(List.of(
                td(1L, DriverRole.MAIN), td(1L, DriverRole.ASSISTANT))));
        when(linehaulTripRepository.findById(11L)).thenReturn(Optional.of(trip));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateStatusToCanStart(11L, 1, 1));
        assertEquals(DriverErrorCode.DRIVER_DUPLICATE_IN_TRIP, ex.getErrorCode());
        assertEquals(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus.PREPARING, trip.getStatus());
    }

    // TC-DRIVER-036
    @Test
    void departure_driverOnAnotherTrip_blocked() {
        var trip = preparingTrip(12L, new java.util.ArrayList<>(List.of(td(9L, DriverRole.MAIN))));
        when(linehaulTripRepository.findById(12L)).thenReturn(Optional.of(trip));
        when(linehaulTripDriverRepository.findActiveAssignmentsForDriver(eq(9L), anyList(), eq(12L)))
                .thenReturn(List.of(new com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver()));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateStatusToCanStart(12L, 1, 1));
        assertEquals(DriverErrorCode.DRIVER_ALREADY_ON_ANOTHER_TRIP, ex.getErrorCode());
    }

    // TC-DRIVER-037
    @Test
    void departure_noMain_blockedAndStatusUnchanged() {
        var trip = preparingTrip(13L, new java.util.ArrayList<>(List.of(td(1L, DriverRole.ASSISTANT))));
        when(linehaulTripRepository.findById(13L)).thenReturn(Optional.of(trip));

        AppException ex = assertThrows(AppException.class,
                () -> service.updateStatusToCanStart(13L, 1, 1));
        assertEquals(DriverErrorCode.MAIN_DRIVER_NOT_FOUND, ex.getErrorCode());
        assertEquals(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus.PREPARING, trip.getStatus());
        org.mockito.Mockito.verify(linehaulTripRepository, org.mockito.Mockito.never()).save(any());
    }

    // ---- Driver status lifecycle ----

    // TC-DRIVER-038: huỷ (delete) chuyến giải phóng đúng driver về AVAILABLE.
    @Test
    void cancelTrip_releasesDriver() {
        var mainTd = td(1L, DriverRole.MAIN);
        mainTd.getDriver().setStatus(DriverStatus.BUSY);
        var trip = preparingTrip(20L, new java.util.ArrayList<>(List.of(mainTd)));
        when(linehaulTripRepository.findById(20L)).thenReturn(Optional.of(trip));
        when(palletRepository.findPalletByLinehaulTrip(trip)).thenReturn(List.of());

        service.delete(20L, 1, 1);

        assertEquals(DriverStatus.AVAILABLE, mainTd.getDriver().getStatus());
    }

    // TC-DRIVER-040: update bỏ driver cũ khỏi danh sách -> driver cũ về AVAILABLE.
    @Test
    void updateRemovingDriver_releasesOldDriver() {
        var oldTd = td(1L, DriverRole.MAIN);
        oldTd.getDriver().setStatus(DriverStatus.BUSY);
        var trip = new com.overcode250204.smartlogicticssystem.entities.LinehaulTrip();
        trip.setLinehaulId(21L);
        trip.setStatus(com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus.PREPARING);
        trip.setTripDrivers(new java.util.ArrayList<>(List.of(oldTd)));
        when(linehaulTripRepository.findById(21L)).thenReturn(Optional.of(trip));
        when(linehaulTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest();
        var dr2 = new com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripDriverUpdateRequest();
        dr2.setDriverId(2L);
        dr2.setRole(DriverRole.MAIN);
        req.setLinehaulTripDriverUpdateRequests(List.of(dr2));

        service.update(21L, req, 1, 1);

        assertEquals(DriverStatus.AVAILABLE, oldTd.getDriver().getStatus());
    }
}
