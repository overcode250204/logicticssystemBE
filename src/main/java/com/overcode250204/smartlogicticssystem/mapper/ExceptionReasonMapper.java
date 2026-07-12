package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonCreateRequest;
import com.overcode250204.smartlogicticssystem.dtos.request.ExceptionReasonUpdateRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.ExceptionReasonResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.ExceptionReason;
import org.springframework.stereotype.Component;

@Component
public class ExceptionReasonMapper {

    public ExceptionReasonResponseDTO toResponse(ExceptionReason entity) {
        if (entity == null) {
            return null;
        }
        return ExceptionReasonResponseDTO.builder()
                .reasonId(entity.getReasonId())
                .category(entity.getCategory())
                .reasonText(entity.getReasonText())
                .isActive(entity.getIsActive())
                .build();
    }

    public ExceptionReason toEntity(ExceptionReasonCreateRequest request) {
        if (request == null) {
            return null;
        }
        ExceptionReason entity = new ExceptionReason();
        entity.setCategory(request.getCategory());
        entity.setReasonText(request.getReasonText());
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        return entity;
    }

    public void updateEntity(ExceptionReasonUpdateRequest request, ExceptionReason entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setCategory(request.getCategory());
        entity.setReasonText(request.getReasonText());
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
    }
}
