package com.overcode250204.smartlogicticssystem.dtos.request;

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

    @Nullable
    private Long vehicleId;

    @Nullable
    private Long routeId;

}

