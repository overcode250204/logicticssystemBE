package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExceptionReasonCreateRequest {

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Reason text is required")
    private String reasonText;

    private Boolean isActive = true;
}
