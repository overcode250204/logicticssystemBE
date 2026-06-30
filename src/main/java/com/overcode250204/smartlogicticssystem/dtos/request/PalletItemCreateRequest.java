package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PalletItemCreateRequest {
    @NotNull(message = "Order code id is required")
    private String orderCode;
}
