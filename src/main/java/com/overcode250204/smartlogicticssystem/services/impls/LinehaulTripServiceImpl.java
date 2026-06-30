package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.*;
import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.*;
import com.overcode250204.smartlogicticssystem.enums.*;
import com.overcode250204.smartlogicticssystem.exception.*;
import com.overcode250204.smartlogicticssystem.mapper.LinehaulTripMapper;
import com.overcode250204.smartlogicticssystem.repositories.*;
import com.overcode250204.smartlogicticssystem.services.ILinehaulTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class LinehaulTripServiceImpl extends BaseServiceImpl implements ILinehaulTripService {
    private final LinehaulTripRepository linehaulTripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final LinehaulTripMapper linehaulTripMapper;
    private final PalletRepository palletRepository;
    private final OrderRepository orderRepository;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

    //NOTE===============CRUD for ADMIN manage Linehaul Trip============================
    @Override
    @Transactional
    public LinehaulTripResponseDTO create(LinehaulTripCreateRequest request, int roleId, int userId) {
        // CHECK ROLE
        checkAdminRole(roleId);

        LinehaulTrip linehaulTrip = new LinehaulTrip();

        //MAP ROUTE
        linehaulTrip.setRouteConfig(mapRoute(request.getRouteId()));
        //MAP VEHICLE
        linehaulTrip.setVehicle(mapVehicle(request.getVehicleId()));
        // SET STATUS
        linehaulTrip.setStatus(LinehaulTripStatus.PREPARING);

        // MAP DRIVERS
        linehaulTrip.setTripDrivers(mapDrivers(request.getLinehaulTripDriverCreateRequest(), linehaulTrip));

        LinehaulTrip savedTrip = linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(savedTrip);
    }

    @Override
    public LinehaulTripResponseDTO getById(Long id, int roleId, int userId) {
        //CHECK ROLE
        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        return linehaulTripMapper.toResponse(linehaulTrip);
    }

    @Override
    public List<LinehaulTripResponseDTO> getAll(int roleId, int userId) {
        checkAdminRole(roleId);
        return linehaulTripRepository.findAll().stream()
                .map(linehaulTripMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public LinehaulTripResponseDTO update(Long id, LinehaulTripUpdateRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);


        if(!LinehaulTripStatus.PREPARING.equals(linehaulTrip.getStatus())){
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_UPDATE);
        }

        //MAP VEHICLE
        linehaulTrip.setVehicle(mapVehicle(request.getVehicleId()));
        // todo check constraint of vehicle for trip if have pallet

        if (request.getLinehaulTripDriverUpdateRequests() != null) {
            List<Long> newDriverIds = request.getLinehaulTripDriverUpdateRequests().stream()
                    .map(LinehaulTripDriverUpdateRequest::getDriverId).toList();

            // Release old drivers not in the new list
            for (LinehaulTripDriver tripDriver : linehaulTrip.getTripDrivers()) {
                Driver driver = tripDriver.getDriver();
                if (!newDriverIds.contains(driver.getDriverId())) {
                    driver.setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(driver);
                }
            }

            // Validate and assign new drivers
            List<LinehaulTripDriver> newTripDrivers = new ArrayList<>();
            for (LinehaulTripDriverUpdateRequest driverReq : request.getLinehaulTripDriverUpdateRequests()) {
                Driver driver = findByIdOrThrow(driverRepository, driverReq.getDriverId(), DriverErrorCode.DRIVER_NOT_FOUND);
                if (!DriverType.LINEHAUL.equals(driver.getDriverType())) {
                    throw new AppException(DriverErrorCode.DRIVER_NOT_LINEHAUL);
                }
                if(linehaulTrip.getRouteConfig() != null){
                    Long warehouseId = linehaulTrip.getRouteConfig().getFromWarehouse().getWarehouseId();
                    if(!Objects.equals(warehouseId, driver.getCurrentWarehouse().getWarehouseId())){
                        throw new AppException(DriverErrorCode.DRIVER_NOT_IN_WAREHOUSE);
                    }
                }

                boolean previouslyAssigned = linehaulTrip.getTripDrivers().stream()
                        .anyMatch(td -> td.getDriver().getDriverId().equals(driver.getDriverId()));
                if (!previouslyAssigned) {
                    if (!DriverStatus.AVAILABLE.equals(driver.getStatus())) {
                        throw new AppException(DriverErrorCode.DRIVER_NOT_AVAILABLE);
                    }
                    driver.setStatus(DriverStatus.BUSY);
                    driverRepository.save(driver);
                }


                LinehaulTripDriver tripDriver = new LinehaulTripDriver();
                tripDriver.setLinehaulTrip(linehaulTrip);
                tripDriver.setDriver(driver);
                tripDriver.setRole(driverReq.getRole());
                tripDriver.setAssignmentStatus(AssignmentStatus.ASSIGNED);
                tripDriver.setAssignedAt(LocalDateTime.now());
                newTripDrivers.add(tripDriver);
            }

            linehaulTrip.getTripDrivers().clear();
            linehaulTrip.getTripDrivers().addAll(newTripDrivers);
        }else{
            linehaulTrip.setTripDrivers(null);
        }

        linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(linehaulTrip);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {

        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        if(!LinehaulTripStatus.PREPARING.equals(linehaulTrip.getStatus())){
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_DELETE);
        }

        List<Pallet> pallets = palletRepository.findPalletByLinehaulTrip(linehaulTrip);
        if (pallets != null && !pallets.isEmpty()) {
            for (Pallet pallet : pallets) {
                pallet.setLinehaulTrip(null);
                //todo need to change status order to IN_Pallet
                palletRepository.save(pallet);
            }
        }

        for (LinehaulTripDriver tripDriver : linehaulTrip.getTripDrivers()) {
            Driver driver = tripDriver.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }

        linehaulTripRepository.delete(linehaulTrip);
    }

    private RouteConfig mapRoute(Long routeId){
        if (routeId != null) {
            return findByIdOrThrow(routeConfigRepository, routeId, RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);

        }else{
            return null;
        }
    }

    private Vehicle mapVehicle(Long vehicleId){
        if (vehicleId != null) {
            Vehicle vehicle = findByIdOrThrow(vehicleRepository, vehicleId, VehicleErrorCode.VEHICLE_NOT_FOUND);
            if (!VehicleStatus.ACTIVE.equals(vehicle.getStatus())) {
                throw new AppException(VehicleErrorCode.VEHICLE_NOT_ACTIVE);
            }
            return vehicle;
        }else {
            return  null;
        }
    }

    private List<LinehaulTripDriver> mapDrivers(List<LinehaulTripDriverCreateRequest> requests, LinehaulTrip linehaulTrip){


        List<LinehaulTripDriver> results = new ArrayList<>();
        if (requests != null) {
            for (LinehaulTripDriverCreateRequest driverReq : requests) {
                Driver driver = findByIdOrThrow(driverRepository, driverReq.getDriverId(), DriverErrorCode.DRIVER_NOT_FOUND);
                if (!DriverType.LINEHAUL.equals(driver.getDriverType())) {
                    throw new AppException(DriverErrorCode.DRIVER_NOT_LINEHAUL);
                }
                if (!DriverStatus.AVAILABLE.equals(driver.getStatus())) {
                    throw new AppException(DriverErrorCode.DRIVER_NOT_AVAILABLE);
                }
                if(linehaulTrip.getRouteConfig() != null){
                    Long warehouseId = linehaulTrip.getRouteConfig().getFromWarehouse().getWarehouseId();
                    if(warehouseId != driver.getCurrentWarehouse().getWarehouseId()){
                        throw new AppException(DriverErrorCode.DRIVER_NOT_IN_WAREHOUSE);
                    }
                }

                driver.setStatus(DriverStatus.BUSY);
                driverRepository.save(driver);

                LinehaulTripDriver tripDriver = new LinehaulTripDriver();
                tripDriver.setLinehaulTrip(linehaulTrip);
                tripDriver.setDriver(driver);
                tripDriver.setRole(driverReq.getRole());
                tripDriver.setAssignmentStatus(AssignmentStatus.ASSIGNED);
                tripDriver.setAssignedAt(LocalDateTime.now());
               results.add(tripDriver);
            }
            return results;
        }else {
            return null;
        }
    }

    //NOTE===============Handle add Pallet for TRIP ============================
    @Override
    @Transactional
    public LinehaulTripResponseDTO addPallet(Long id, LinehaulTripAddPalletRequest request, int roleId, int userId) {
        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);

        if (!LinehaulTripStatus.PREPARING.equals(linehaulTrip.getStatus())) {
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_UPDATE);
        }

        Pallet pallet = findByIdOrThrow(palletRepository, request.getPalletId(), PalletErrorCode.PALLET_NOT_FOUND);


        if (pallet.getLinehaulTrip() != null) {
            throw new AppException(PalletErrorCode.PALLET_AlREADY_ASSIGNED);
        }

        if(!PalletStatus.SEALED.equals(pallet.getStatus())){
            throw new AppException(PalletErrorCode.PALLET_AlREADY_ASSIGNED);
        }

        if (pallet.getRouteConfig() == null || linehaulTrip.getRouteConfig() == null ||
                !pallet.getRouteConfig().getRouteId().equals(linehaulTrip.getRouteConfig().getRouteId())) {
            throw new AppException(PalletErrorCode.ROUTE_MISMATCH);
        }

        Vehicle vehicle = linehaulTrip.getVehicle();
        if (vehicle == null) {
            vehicle = linehaulTrip.getRouteConfig().getDefaultVehicle();
        }

        if (vehicle != null) {
            java.math.BigDecimal currentWeight = linehaulTrip.getPallets().stream()
                    .map(Pallet::getTotalWeightKg)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal newWeight = currentWeight.add(pallet.getTotalWeightKg());
            if (vehicle.getMaxWeightKg() != null && newWeight.compareTo(vehicle.getMaxWeightKg()) > 0) {
                throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAPACITY_EXCEEDED);
            }

            java.math.BigDecimal currentVolume = linehaulTrip.getPallets().stream()
                    .map(Pallet::getTotalVolumeM3)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal newVolume = currentVolume.add(pallet.getTotalVolumeM3());
            if (vehicle.getMaxVolumeM3() != null && newVolume.compareTo(vehicle.getMaxVolumeM3()) > 0) {
                throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAPACITY_EXCEEDED);
            }
        }

        pallet.setLinehaulTrip(linehaulTrip);
        palletRepository.save(pallet);

        if (!linehaulTrip.getPallets().contains(pallet)) {
            linehaulTrip.getPallets().add(pallet);
        }

        return linehaulTripMapper.toResponse(linehaulTrip);
    }

    @Override
    @Transactional
    public LinehaulTripResponseDTO removePallet(Long id, Long palletId, int roleId, int userId) {
        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);

        if (!LinehaulTripStatus.PREPARING.equals(linehaulTrip.getStatus())) {
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_UPDATE);
        }

        Pallet pallet = findByIdOrThrow(palletRepository, palletId, PalletErrorCode.PALLET_NOT_FOUND);

        if (pallet.getLinehaulTrip() == null || !pallet.getLinehaulTrip().getLinehaulId().equals(id)) {
            throw new AppException(PalletErrorCode.PALLET_CANNOT_UPDATE);
        }

        pallet.setLinehaulTrip(null);
        palletRepository.save(pallet);

        linehaulTrip.getPallets().remove(pallet);

        return linehaulTripMapper.toResponse(linehaulTrip);
    }

    private double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    //NOTE===============DELIVERY for DRIVER ============================
    @Override
    @Transactional
    public LinehaulTripResponseDTO dispatchTrip(Long id, LinehaulTripGpsRequest request, int roleId, int userId) {
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);

        //Check driver
        //TODO must have trip drivers even actor is admin
        boolean isAssignedDriver = linehaulTrip.getTripDrivers().stream()
                .anyMatch(td -> td.getDriver() != null && td.getDriver().getUser() != null 
                        && td.getDriver().getUser().getUserId() != null 
                        && td.getDriver().getUser().getUserId().intValue() == userId);
        if (roleId != 1 && !isAssignedDriver) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }

        //Check previous status
        if (!LinehaulTripStatus.PREPARING.equals(linehaulTrip.getStatus())) {
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);
        }

        if (linehaulTrip.getPallets() == null || linehaulTrip.getPallets().isEmpty()) {
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);
        }
        for (Pallet pallet : linehaulTrip.getPallets()) {
            if (PalletStatus.CREATING.equals(pallet.getStatus())) {
                throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);
            }
        }
        //check route
        if (linehaulTrip.getRouteConfig() == null || linehaulTrip.getRouteConfig().getFromWarehouse() == null) {
            throw new AppException(RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        }
        // check warehouse and GPS of driver
        Warehouse fromWarehouse = linehaulTrip.getRouteConfig().getFromWarehouse();
        if (fromWarehouse.getLocation() == null) {
            throw new AppException(WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        }
        double lat1 = fromWarehouse.getLocation().getY();
        double lon1 = fromWarehouse.getLocation().getX();
        double distance = calculateDistanceInMeters(lat1, lon1, request.getLatitude(), request.getLongitude());
        if (distance > 500.0) {
            throw new AppException(LinehaulTripErrorCode.GPS_NOT_NEAR_FROM_WAREHOUSE);
        }
        //Change status to EN_ROUTE

        linehaulTrip.setStatus(LinehaulTripStatus.EN_ROUTE);
        linehaulTrip.setDepartureTime(LocalDateTime.now());

        //Change status of pallet
        for (Pallet pallet : linehaulTrip.getPallets()) {
            pallet.setStatus(PalletStatus.IN_TRANSIT);
            List<PalletItem> items = pallet.getPalletItems();
            //Change status of order
            for(PalletItem item :items){
                Order order = item.getOrder();
                if(order != null){
                    order.setStatus(OrderStatus.IN_TRANSIT_LINEHAUL);
                    orderRepository.save(order);
                }
            }
            palletRepository.save(pallet);
        }

        LinehaulTrip savedTrip = linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(savedTrip);
    }

    @Override
    @Transactional
    public LinehaulTripResponseDTO finishTrip(Long id, LinehaulTripGpsRequest request, int roleId, int userId) {
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        //Check driver
        boolean isAssignedDriver = linehaulTrip.getTripDrivers().stream()
                .anyMatch(td -> td.getDriver() != null && td.getDriver().getUser() != null 
                        && td.getDriver().getUser().getUserId() != null 
                        && td.getDriver().getUser().getUserId().intValue() == userId);
        if (roleId != 1 && !isAssignedDriver) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
        //Check previous status
        if (!LinehaulTripStatus.EN_ROUTE.equals(linehaulTrip.getStatus())) {
            throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_UPDATE);
        }
        //Check route
        if (linehaulTrip.getRouteConfig() == null || linehaulTrip.getRouteConfig().getToWarehouse() == null) {
            throw new AppException(RouteConfigErrorCode.ROUTE_CONFIG_NOT_FOUND);
        }
        //Check warehouse and GPS of driver
        Warehouse toWarehouse = linehaulTrip.getRouteConfig().getToWarehouse();
        if (toWarehouse.getLocation() == null) {
            throw new AppException(WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
        }
        double lat1 = toWarehouse.getLocation().getY();
        double lon1 = toWarehouse.getLocation().getX();
        double distance = calculateDistanceInMeters(lat1, lon1, request.getLatitude(), request.getLongitude());
        if (distance > 500.0) {
            throw new AppException(LinehaulTripErrorCode.GPS_NOT_NEAR_TO_WAREHOUSE);
        }
        //Change status
        linehaulTrip.setStatus(LinehaulTripStatus.ARRIVED);
        linehaulTrip.setArrivalTime(LocalDateTime.now());

        // ============================
        //Change status driver
        for (LinehaulTripDriver tripDriver : linehaulTrip.getTripDrivers()) {
            Driver driver = tripDriver.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setCurrentWarehouse(toWarehouse);
            driverRepository.save(driver);
        }

        LinehaulTrip savedTrip = linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(savedTrip);
    }
}

