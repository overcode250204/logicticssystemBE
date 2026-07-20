package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveVehicleInfo {
    private String trip_code;
    private String shipper_name;
    private String deadline; // formatted e.g., "16:00 20/07/2026"
    private Double lat;
    private Double lng;
    private String status; // "gray", "green", "yellow"
    private String last_ping_time; // formatted e.g., "15:30 20/07/2026"
}
