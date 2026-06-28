package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.PalletStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PalletUpdateRequest {

    @NotNull(message = "LinehaulTrip id is required")
    private Long linehaulTripId;

    @NotNull(message = "LinehaulTrip id is required")
    private List<Long> orderIds;

    @NotNull(message = "LinehaulTrip id is required")
    private PalletStatus status;

}
