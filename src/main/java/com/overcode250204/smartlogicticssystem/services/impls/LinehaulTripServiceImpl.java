package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.base.BaseServiceImpl;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripDriverCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripDriverUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.LinehaulTripUpdateRequest;
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


//NOTE===============CRUD for ADMIN manage Linehaul Trip============================
@Service
@RequiredArgsConstructor
public class LinehaulTripServiceImpl extends BaseServiceImpl implements ILinehaulTripService {
    private final LinehaulTripRepository linehaulTripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final RouteConfigRepository routeConfigRepository;
    private final LinehaulTripMapper linehaulTripMapper;
    private final PalletRepository palletRepository;

    private void checkAdminRole(int roleId) {
        if (roleId != 1) {
            throw new AppException(RoleErrorCode.ROLE_HAS_NO_PERMISSION);
        }
    }

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

        //MAP VEHICLE
        linehaulTrip.setVehicle(mapVehicle(request.getVehicleId()));
        //MAP ROUTE
        linehaulTrip.setRouteConfig(mapRoute(request.getRouteId()));
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

        LinehaulTripStatus newStatus = request.getStatus();
        if (newStatus != null) {
            linehaulTrip.setStatus(newStatus);

            List<Pallet> pallets = palletRepository.findPalletByLinehaulTrip(linehaulTrip);

            if (LinehaulTripStatus.EN_ROUTE.equals(newStatus)) {
                if (pallets.stream().anyMatch(p -> PalletStatus.CREATING.equals(p.getStatus())) || pallets.isEmpty()) {
                    throw new AppException(LinehaulTripErrorCode.LINEHAUL_TRIP_CAN_NOT_EN_ROUTE);
                }
                linehaulTrip.setDepartureTime(LocalDateTime.now());
                for (Pallet pallet : pallets) {
                    pallet.setStatus(PalletStatus.IN_TRANSIT);
                    palletRepository.save(pallet);
                }
            }

            if (LinehaulTripStatus.ARRIVED.equals(newStatus)) {
                linehaulTrip.setArrivalTime(LocalDateTime.now());
                for (LinehaulTripDriver tripDriver : linehaulTrip.getTripDrivers()) {
                    Driver driver = tripDriver.getDriver();
                    driver.setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(driver);
                }
            }
        }

        linehaulTripRepository.save(linehaulTrip);
        return linehaulTripMapper.toResponse(linehaulTrip);
    }

    @Override
    @Transactional
    public void delete(Long id, int roleId, int userId) {
        checkAdminRole(roleId);
        LinehaulTrip linehaulTrip = findByIdOrThrow(linehaulTripRepository, id, LinehaulTripErrorCode.LINEHAUL_TRIP_NOT_FOUND);
        
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
}

