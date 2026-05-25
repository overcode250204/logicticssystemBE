package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryTransactionUpdateRequest {
    @NotNull(message = "Batch id is required")
    private Long batchId;

    @NotNull(message = "Transaction type is required")
    private InventoryTransactionType type;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
