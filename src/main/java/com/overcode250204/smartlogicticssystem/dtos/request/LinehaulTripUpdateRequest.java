package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.LinehaulTripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class LinehaulTripUpdateRequest {

    @Nullable
    private List<LinehaulTripDriverUpdateRequest> linehaulTripDriverUpdateRequests;

    @NotNull(message = "Vehicle id id is required")
    private Long vehicleId;

    @NotNull(message = "Status is required")
    private LinehaulTripStatus status;

    @NotNull(message = "Route id id is required")
    private Long routeId;

}
