package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.driverprofile.DriverProfileDTO;
import com.overcode250204.smartlogicticssystem.entities.DriverProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverProfileMapper {

    
    public DriverProfileDTO toResponse(DriverProfile driverProfile) {
        if (driverProfile == null) {
            return null;
        }

        return DriverProfileDTO.builder()
                .id(driverProfile.getId())
                .userId(driverProfile.getUserId())
                .maxWeightCapacity(driverProfile.getMaxWeightCapacity())
                .status(driverProfile.getStatus())
                .plateNumber(driverProfile.getPlateNumber())
                .build();
    }


    public DriverProfile toEntity(DriverProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        return DriverProfile.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .maxWeightCapacity(dto.getMaxWeightCapacity())
                .plateNumber(dto.getPlateNumber())
                .build();
    }

    public void updateEntity(DriverProfileDTO dto, DriverProfile entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setMaxWeightCapacity(dto.getMaxWeightCapacity());
        entity.setStatus(dto.getStatus());
        if (dto.getPlateNumber() != null) {
            entity.setPlateNumber(dto.getPlateNumber());
        }
    }
}
