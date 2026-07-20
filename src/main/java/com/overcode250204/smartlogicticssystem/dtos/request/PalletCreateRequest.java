package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PalletCreateRequest {

    @NotNull(message = "Route config id is required")
    private Long routeConfigId;
}
