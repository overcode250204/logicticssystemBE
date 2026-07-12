package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.WarehouseType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class WarehouseResponseDTO {
    private Long warehouseId;
    private String name;
    private WarehouseType type;
    private String address;
    private String province;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalTime startDeliveryTime;
}
