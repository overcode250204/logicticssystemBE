package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleCreateRequest {
    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Max weight is required")
    private BigDecimal maxWeightKg;

    @NotNull(message = "Max volume is required")
    private BigDecimal maxVolumeM3;

    private VehicleStatus status;
}
