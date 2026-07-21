package com.overcode250204.smartlogicticssystem.dataSeeder;

import com.overcode250204.smartlogicticssystem.entities.Driver;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTrip;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver;
import com.overcode250204.smartlogicticssystem.entities.LocalTrip;
import com.overcode250204.smartlogicticssystem.entities.LocalTripDetail;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.Pallet;
import com.overcode250204.smartlogicticssystem.entities.PalletItem;
import com.overcode250204.smartlogicticssystem.entities.RouteConfig;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import com.overcode250204.smartlogicticssystem.entities.Warehouse;
import com.overcode250204.smartlogicticssystem.enums.DriverRole;
import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import com.overcode250204.smartlogicticssystem.enums.LocalTripDetailStatus;
import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import com.overcode250204.smartlogicticssystem.repositories.DriverRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripDetailRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletItemRepository;
import com.overcode250204.smartlogicticssystem.repositories.PalletRepository;
import com.overcode250204.smartlogicticssystem.repositories.RouteConfigRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransportationDataSeeder implements DataSeeder {

    private final PalletRepository palletRepository;
    private final PalletItemRepository palletItemRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final LocalTripRepository localTripRepository;
    private final LocalTripDetailRepository localTripDetailRepository;
    private final OrderRepository orderRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public int getOrder() {
        return 17;
    }

    @Override
    @Transactional
    public void seed() {
        seedPallets();
        seedLinehaulTrips();
        seedLocalTrips();
        log.info("Pallet, linehaul-trip, and local-trip seed data completed.");
    }

    private void seedPallets() {
        List<PalletSeed> pallets = List.of(
                new PalletSeed("PL-SEED-0001", PalletStatus.CREATING, "ORD-SEED-000002", false),
                new PalletSeed("PL-SEED-0002", PalletStatus.CAN_SEAL, "ORD-SEED-000003", true),
                new PalletSeed("PL-SEED-0003", PalletStatus.SEALED, "ORD-SEED-000011", true),
                new PalletSeed("PL-SEED-0004", PalletStatus.IN_TRANSIT, "ORD-SEED-000004", true),
                new PalletSeed("PL-SEED-0005", PalletStatus.ARRIVED, "ORD-SEED-000005", true)
        );
        pallets.forEach(this::createPalletAggregate);
    }

    private void seedLinehaulTrips() {
        LocalDateTime now = LocalDateTime.now();
        List<LinehaulSeed> trips = List.of(
                new LinehaulSeed("LH-SEED-0001", LinehaulTripStatus.PREPARING,
                        RouteConfigDataSeeder.HCM_ROUTE, "51A-10001", null, now.plusHours(3), null),
                new LinehaulSeed("LH-SEED-0002", LinehaulTripStatus.CAN_START,
                        RouteConfigDataSeeder.HCM_ROUTE, "51A-10001", "PL-SEED-0003", now.plusHours(1), null),
                new LinehaulSeed("LH-SEED-0003", LinehaulTripStatus.EN_ROUTE,
                        RouteConfigDataSeeder.HCM_ROUTE, "51A-10002", "PL-SEED-0004", now.minusHours(3), null),
                new LinehaulSeed("LH-SEED-0004", LinehaulTripStatus.ARRIVED,
                        RouteConfigDataSeeder.DONG_NAI_ROUTE, "51A-10002", "PL-SEED-0005",
                        now.minusDays(2), now.minusDays(2).plusHours(5)),
                new LinehaulSeed("LH-SEED-0005", LinehaulTripStatus.CANCELLED,
                        RouteConfigDataSeeder.DONG_NAI_ROUTE, "51A-90001", null, now.minusDays(1), null)
        );
        trips.forEach(this::createLinehaulTrip);
    }

    private void seedLocalTrips() {
        List<LocalTripSeed> trips = List.of(
                new LocalTripSeed("LT-SEED-0001", LocalTripStatus.PENDING_ACCEPTANCE,
                        WarehouseZoneDataSeeder.THU_DUC_HUB, "0903333335", "59B1-30001", 50),
                new LocalTripSeed("LT-SEED-0002", LocalTripStatus.ACCEPTED,
                        WarehouseZoneDataSeeder.DISTRICT_7_HUB, "0903333336", "59B1-30002", 65),
                new LocalTripSeed("LT-SEED-0003", LocalTripStatus.CANCELLED,
                        WarehouseZoneDataSeeder.BIEN_HOA_HUB, "0903333338", "51C-20002", 80),
                new LocalTripSeed("LT-SEED-0004", LocalTripStatus.ASSIGNED,
                        WarehouseZoneDataSeeder.BIEN_HOA_HUB, "0903333338", "51C-20002", 75),
                new LocalTripSeed("LT-SEED-0005", LocalTripStatus.EXECUTING,
                        WarehouseZoneDataSeeder.DISTRICT_7_HUB, "0903333336", "59B1-30002", 90),
                new LocalTripSeed("LT-SEED-0006", LocalTripStatus.COMPLETED,
                        WarehouseZoneDataSeeder.THU_DUC_HUB, "0903333335", "59B1-30001", 110)
        );

        trips.forEach(this::createLocalTrip);
        createLocalTripDetail("LTD-SEED-0001", "LT-SEED-0004", "ORD-SEED-000005", 1,
                LocalTripDetailStatus.PENDING, false, null);
        createLocalTripDetail("LTD-SEED-0002", "LT-SEED-0005", "ORD-SEED-000006", 1,
                LocalTripDetailStatus.ARRIVED, true, null);
        createLocalTripDetail("LTD-SEED-0003", "LT-SEED-0006", "ORD-SEED-000008", 1,
                LocalTripDetailStatus.COMPLETED, true, "seed://proof/ORD-SEED-000008");
        createLocalTripDetail("LTD-SEED-0004", "LT-SEED-0006", "ORD-SEED-000009", 2,
                LocalTripDetailStatus.FAILED, true, null);
    }

    private void createPalletAggregate(PalletSeed seed) {
        Order order = requireOrder(seed.orderCode());
        RouteConfig route = order.getRouteConfig();
        if (route == null) {
            throw new IllegalStateException("Order has no route: " + seed.orderCode());
        }
        Pallet pallet = palletRepository.findByPalletCode(seed.code())
                .orElseGet(() -> {
                    Pallet created = new Pallet();
                    created.setPalletCode(seed.code());
                    created.setBarcodeUrl("seed://pallet/" + seed.code());
                    created.setRouteConfig(route);
                    created.setStatus(seed.status());
                    created.setIsCreatedSystem(true);
                    created.setTotalWeightKg(order.getTotalWeightKg());
                    created.setTotalVolumeM3(order.getTotalVolumeM3());
                    return palletRepository.save(created);
                });
        normalizePallet(pallet, seed, route, order);

        if (palletItemRepository.existsByOrder_OrderId(order.getOrderId())) {
            return;
        }

        PalletItem item = new PalletItem();
        item.setPallet(pallet);
        item.setOrder(order);
        item.setIsScanned(seed.scanned());
        item.setScannedAt(seed.scanned() ? LocalDateTime.now().minusHours(1) : null);
        palletItemRepository.save(item);
    }

    private void normalizePallet(Pallet pallet, PalletSeed seed, RouteConfig route, Order order) {
        boolean changed = false;
        if (pallet.getStatus() != seed.status()) {
            pallet.setStatus(seed.status());
            changed = true;
        }
        if (pallet.getRouteConfig() == null) {
            pallet.setRouteConfig(route);
            changed = true;
        }
        if (pallet.getBarcodeUrl() == null || pallet.getBarcodeUrl().isBlank()) {
            pallet.setBarcodeUrl("seed://pallet/" + seed.code());
            changed = true;
        }
        if (pallet.getIsCreatedSystem() == null || !pallet.getIsCreatedSystem()) {
            pallet.setIsCreatedSystem(true);
            changed = true;
        }
        if (pallet.getTotalWeightKg() == null) {
            pallet.setTotalWeightKg(order.getTotalWeightKg());
            changed = true;
        }
        if (pallet.getTotalVolumeM3() == null) {
            pallet.setTotalVolumeM3(order.getTotalVolumeM3());
            changed = true;
        }
        if (changed) {
            palletRepository.save(pallet);
        }
    }

    private void createLinehaulTrip(LinehaulSeed seed) {
        LinehaulTrip trip = linehaulTripRepository.findByLinehaulTripCode(seed.code())
                .orElseGet(() -> {
                    LinehaulTrip created = new LinehaulTrip();
                    created.setLinehaulTripCode(seed.code());
                    created.setRouteConfig(requireRoute(seed.routeName()));
                    created.setVehicle(requireVehicle(seed.licensePlate()));
                    created.setStatus(seed.status());
                    created.setDepartureTime(seed.departureTime());
                    created.setArrivalTime(seed.arrivalTime());
                    created.setIsCreatedSystem(true);
                    addDriver(created, "0903333333", DriverRole.MAIN);
                    addDriver(created, "0903333334", DriverRole.ASSISTANT);
                    return linehaulTripRepository.save(created);
                });
        normalizeLinehaulTrip(trip, seed);

        if (seed.palletCode() != null) {
            Pallet pallet = palletRepository.findByPalletCode(seed.palletCode())
                    .orElseThrow(() -> new IllegalStateException("Pallet not found: " + seed.palletCode()));
            if (pallet.getLinehaulTrip() == null) {
                pallet.setLinehaulTrip(trip);
                palletRepository.save(pallet);
            }
        }
    }

    private void addDriver(LinehaulTrip trip, String phone, DriverRole role) {
        Driver driver = driverRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalStateException("Driver not found: " + phone));
        LinehaulTripDriver assignment = new LinehaulTripDriver();
        assignment.setLinehaulTrip(trip);
        assignment.setDriver(driver);
        assignment.setRole(role);
        assignment.setAssignedAt(LocalDateTime.now().minusHours(4));
        trip.getTripDrivers().add(assignment);
    }

    private void normalizeLinehaulTrip(LinehaulTrip trip, LinehaulSeed seed) {
        boolean changed = false;
        RouteConfig route = requireRoute(seed.routeName());
        Vehicle vehicle = requireVehicle(seed.licensePlate());
        if (trip.getStatus() != seed.status()) {
            trip.setStatus(seed.status());
            changed = true;
        }
        if (trip.getRouteConfig() == null
                || !trip.getRouteConfig().getRouteId().equals(route.getRouteId())) {
            trip.setRouteConfig(route);
            changed = true;
        }
        if (trip.getVehicle() == null
                || !trip.getVehicle().getVehicleId().equals(vehicle.getVehicleId())) {
            trip.setVehicle(vehicle);
            changed = true;
        }
        if (trip.getIsCreatedSystem() == null || !trip.getIsCreatedSystem()) {
            trip.setIsCreatedSystem(true);
            changed = true;
        }
        if (trip.getDepartureTime() == null) {
            trip.setDepartureTime(seed.departureTime());
            changed = true;
        }
        if (trip.getArrivalTime() == null && seed.arrivalTime() != null) {
            trip.setArrivalTime(seed.arrivalTime());
            changed = true;
        }
        if (changed) {
            linehaulTripRepository.save(trip);
        }
    }

    private void createLocalTrip(LocalTripSeed seed) {
        LocalTrip existing = localTripRepository.findAll().stream()
                .filter(candidate -> seed.code().equals(candidate.getLocalTripCode()))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            normalizeLocalTrip(existing, seed);
            return;
        }

        Driver driver = driverRepository.findByPhone(seed.driverPhone())
                .orElseThrow(() -> new IllegalStateException("Driver not found: " + seed.driverPhone()));
        Warehouse hub = warehouseRepository.findByNameIgnoreCase(seed.hubName())
                .orElseThrow(() -> new IllegalStateException("Hub not found: " + seed.hubName()));

        LocalTrip trip = new LocalTrip();
        trip.setLocalTripCode(seed.code());
        trip.setHub(hub);
        trip.setDriver(driver);
        trip.setVehicle(requireVehicle(seed.licensePlate()));
        trip.setStatus(seed.status());
        trip.setVrpEstimatedMinutes(seed.estimatedMinutes());
        localTripRepository.save(trip);
    }

    private void normalizeLocalTrip(LocalTrip trip, LocalTripSeed seed) {
        Driver driver = driverRepository.findByPhone(seed.driverPhone())
                .orElseThrow(() -> new IllegalStateException("Driver not found: " + seed.driverPhone()));
        Warehouse hub = warehouseRepository.findByNameIgnoreCase(seed.hubName())
                .orElseThrow(() -> new IllegalStateException("Hub not found: " + seed.hubName()));
        Vehicle vehicle = requireVehicle(seed.licensePlate());
        boolean changed = false;
        if (trip.getStatus() != seed.status()) {
            trip.setStatus(seed.status());
            changed = true;
        }
        if (trip.getDriver() == null || !trip.getDriver().getDriverId().equals(driver.getDriverId())) {
            trip.setDriver(driver);
            changed = true;
        }
        if (trip.getHub() == null || !trip.getHub().getWarehouseId().equals(hub.getWarehouseId())) {
            trip.setHub(hub);
            changed = true;
        }
        if (trip.getVehicle() == null || !trip.getVehicle().getVehicleId().equals(vehicle.getVehicleId())) {
            trip.setVehicle(vehicle);
            changed = true;
        }
        if (trip.getVrpEstimatedMinutes() == null || trip.getVrpEstimatedMinutes() != seed.estimatedMinutes()) {
            trip.setVrpEstimatedMinutes(seed.estimatedMinutes());
            changed = true;
        }
        if (changed) {
            localTripRepository.save(trip);
        }
    }

    private void createLocalTripDetail(
            String code,
            String tripCode,
            String orderCode,
            int stopOrder,
            LocalTripDetailStatus status,
            boolean barcodeScanned,
            String proofUrl
    ) {
        if (localTripDetailRepository.existsByLocalTripDetailCode(code)) {
            return;
        }
        LocalTrip trip = localTripRepository.findAll().stream()
                .filter(candidate -> tripCode.equals(candidate.getLocalTripCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Local trip not found: " + tripCode));

        LocalTripDetail detail = new LocalTripDetail();
        detail.setLocalTripDetailCode(code);
        detail.setLocalTrip(trip);
        detail.setOrder(requireOrder(orderCode));
        detail.setStopOrder(stopOrder);
        detail.setStatus(status);
        detail.setBarcodeScanned(barcodeScanned);
        detail.setProofUrl(proofUrl);
        localTripDetailRepository.save(detail);
    }

    private RouteConfig requireRoute(String routeName) {
        return routeConfigRepository.findByRouteNameIgnoreCase(routeName)
                .orElseThrow(() -> new IllegalStateException("Route not found: " + routeName));
    }

    private Vehicle requireVehicle(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalStateException("Vehicle not found: " + licensePlate));
    }

    private Order requireOrder(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderCode));
    }

    private record PalletSeed(String code, PalletStatus status, String orderCode, boolean scanned) {
    }

    private record LinehaulSeed(
            String code,
            LinehaulTripStatus status,
            String routeName,
            String licensePlate,
            String palletCode,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime
    ) {
    }

    private record LocalTripSeed(
            String code,
            LocalTripStatus status,
            String hubName,
            String driverPhone,
            String licensePlate,
            int estimatedMinutes
    ) {
    }
}
