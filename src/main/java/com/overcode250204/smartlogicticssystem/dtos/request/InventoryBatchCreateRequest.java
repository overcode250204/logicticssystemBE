package com.overcode250204.smartlogicticssystem.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryBatchCreateRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    private LocalDateTime importDate;
    private LocalDateTime expirationDate;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;
}
