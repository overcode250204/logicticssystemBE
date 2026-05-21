package com.overcode250204.smartlogicticssystem.dtos;

import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryTransactionDTO {
    private Long transactionId;
    private Long batchId;
    private InventoryTransactionType type;
    private Integer quantity;
    private LocalDateTime createdAt;
}
