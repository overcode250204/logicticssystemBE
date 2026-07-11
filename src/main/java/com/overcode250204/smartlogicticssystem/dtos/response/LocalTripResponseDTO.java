package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.LocalTripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LocalTripResponseDTO {
    private Long localTripId;
    private WarehouseResponseDTO hub;
    private DriverResponseDTO driver;
    private VehicleResponseDTO vehicle;
    private LocalTripStatus status;
    private LocalDateTime createdAt;
    private List<LocalTripDetailResponseDTO> details;
}
