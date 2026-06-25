package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LinehaulTripUpdateRequest {

    @NotNull(message = "Driver id is required")
    private Long driverId;

    @NotNull(message = "Vehicle id  required")
    private Long vehicleId;

    @NotNull(message = "Status is required")
    private LinehaulTripStatus status;

}
