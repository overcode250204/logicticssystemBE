package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.VehicleCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.VehicleUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.VehicleResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponseDTO toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponseDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleType(vehicle.getVehicleType())
                .maxWeightKg(vehicle.getMaxWeightKg())
                .maxVolumeM3(vehicle.getMaxVolumeM3())
                .status(vehicle.getStatus())
                .currentWarehouseId(vehicle.getCurrentWarehouse() != null ? vehicle.getCurrentWarehouse().getWarehouseId() : null)
                .currentWarehouseName(vehicle.getCurrentWarehouse() != null ? vehicle.getCurrentWarehouse().getName() : null)
                .build();
    }

    public Vehicle toEntity(VehicleCreateRequest request) {
        if (request == null) {
            return null;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setMaxWeightKg(request.getMaxWeightKg());
        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
        
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }

        return vehicle;
    }

    public void updateEntity(VehicleUpdateRequest request, Vehicle vehicle) {
        if (request == null || vehicle == null) {
            return;
        }

        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setMaxWeightKg(request.getMaxWeightKg());
        vehicle.setMaxVolumeM3(request.getMaxVolumeM3());
        
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
    }
}
