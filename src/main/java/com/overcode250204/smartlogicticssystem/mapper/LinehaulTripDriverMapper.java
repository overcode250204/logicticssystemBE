package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.LinehaulTripDriverResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.LinehaulTripDriver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinehaulTripDriverMapper {
    private final DriverMapper driverMapper;


    public LinehaulTripDriverResponseDTO toResponse(LinehaulTripDriver entity) {
        if (entity == null) {
            return null;
        }

        return LinehaulTripDriverResponseDTO.builder()
                .id(entity.getId())
                .driver(driverMapper.toResponse(entity.getDriver()))
                .role(entity.getRole())
                .assignmentStatus(entity.getAssignmentStatus())
                .assignedAt(entity.getAssignedAt())
                .build();
    }
}
