package com.overcode250204.smartlogicticssystem.dtos.request.driverprofile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverProfileDTO {
    private Long id;
    private Long userId;
    private Integer maxWeightCapacity;
    private String status;
    private String plateNumber;
}
