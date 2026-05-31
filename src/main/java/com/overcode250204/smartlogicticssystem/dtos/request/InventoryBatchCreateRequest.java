package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryBatchCreateRequest {
    @NotNull(message = "Product id is required")
    private Long productId;
    private  boolean received;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private InventoryBatchStatus status;
}
