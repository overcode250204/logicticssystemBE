package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FailPointRequestDTO {

    @NotNull(message = "Reason ID is required")
    private Long reasonId;

    private String notes;
}
