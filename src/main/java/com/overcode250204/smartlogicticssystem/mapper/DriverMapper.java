package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.DriverResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Driver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverMapper {
    private final VehicleMapper vehicleMapper;
    private final WarehouseMapper warehouseMapper;
    private final ZoneMapper zoneMapper;

    public DriverResponseDTO toResponse(Driver driver) {
        if (driver == null) {
            return null;
        }

        return DriverResponseDTO.builder()
                .driverId(driver.getDriverId())
                .phone(driver.getPhone())
                .name(driver.getName())
                .driverType(driver.getDriverType())
                .currentVehicle(vehicleMapper.toResponse(driver.getCurrentVehicle()))
                .status(driver.getStatus())
                .currentWarehouse(warehouseMapper.toResponse(driver.getCurrentWarehouse()))
                .zone(zoneMapper.toResponse(driver.getZone()))
                .build();
    }

//    public Driver toEntity(VehicleCreateRequest request) {
//        if (request == null) {
//            return null;
//        }
//
//        Vehicle vehicle = new Vehicle();
//        vehicle.setLicensePlate(request.getLicensePlate());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setMaxWeightKg(request.getMaxWeightKg());
//        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
//
//        if (request.getStatus() != null) {
//            vehicle.setStatus(request.getStatus());
//        }
//
//        return vehicle;
//    }
//
//    public void updateEntity(VehicleUpdateRequest request, Vehicle vehicle) {
//        if (request == null || vehicle == null) {
//            return;
//        }
//
//        vehicle.setLicensePlate(request.getLicensePlate());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setMaxWeightKg(request.getMaxWeightKg());
//        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
//
//        if (request.getStatus() != null) {
//            vehicle.setStatus(request.getStatus());
//        }
//    }
}
