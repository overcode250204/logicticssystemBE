package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PalletCreateRequest {
    @NotNull(message = "LinehaulTrip id is required")
    private Long linehaulTripId;

    private List<Long> orderIds;
}
