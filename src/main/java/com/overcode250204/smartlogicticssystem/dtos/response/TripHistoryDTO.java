package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TripHistoryDTO {
    private Long id;
    private String tripCode;
    private String tripType;
    private String driverName;
    private String vehiclePlate;
    private String status;
    private LocalDateTime time;
    private String details;
}
