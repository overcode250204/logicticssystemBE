package com.overcode250204.smartlogicticssystem.dtos.request;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.List;

@Data
public class LinehaulTripUpdateRequest {

    @Nullable
    private List<LinehaulTripDriverUpdateRequest> linehaulTripDriverUpdateRequests;

    @Nullable
    private Long vehicleId;

}
