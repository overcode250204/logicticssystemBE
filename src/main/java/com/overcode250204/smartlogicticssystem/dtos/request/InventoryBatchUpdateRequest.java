package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryBatchUpdateRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    private LocalDateTime expirationDate;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must not be negative")
    private Integer quantity;

    @NotNull(message = "Remaining quantity is required")
    @PositiveOrZero(message = "Remaining quantity must not be negative")
    private Integer remainingQuantity;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
}
