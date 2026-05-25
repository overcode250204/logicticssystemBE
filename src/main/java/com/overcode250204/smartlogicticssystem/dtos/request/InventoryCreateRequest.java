package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryCreateRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    @PositiveOrZero(message = "Total stock must not be negative")
    private Integer totalStock;
}
