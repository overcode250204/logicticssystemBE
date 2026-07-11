package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.VehicleStatus;
import com.overcode250204.smartlogicticssystem.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VehicleResponseDTO {
    private Long vehicleId;
    private String licensePlate;
    private VehicleType vehicleType;
    private BigDecimal maxWeightKg;
    private BigDecimal maxVolumeM3;
    private VehicleStatus status;
    private Long currentWarehouseId;
    private String currentWarehouseName;
}
