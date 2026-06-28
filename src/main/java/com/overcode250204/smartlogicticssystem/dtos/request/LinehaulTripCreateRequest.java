package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinehaulTripCreateRequest {

    @Nullable
    private List<LinehaulTripDriverCreateRequest> linehaulTripDriverCreateRequest;

    @NotNull(message = "Vehicle id id is required")
    private Long vehicleId;

    @NotNull(message = "Route id id is required")
    private Long routeId;

}

