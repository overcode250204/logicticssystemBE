package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionReasonResponseDTO {
    private Long reasonId;
    private String category;
    private String reasonText;
    private Boolean isActive;
}
