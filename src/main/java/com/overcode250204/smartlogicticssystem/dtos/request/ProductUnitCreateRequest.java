package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUnitCreateRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Unit ID is required")
    private Long unitId;

    @NotNull(message = "Conversion factor is required")
    @Positive(message = "Conversion factor must be positive")
    private BigDecimal conversionFactor;
}
