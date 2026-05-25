package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.InventoryTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponseDTO {
    private Long transactionId;
    private InventoryBatchSimpleResponseDTO batch;
    private ProductResponseDTO product;
    private InventoryTransactionType type;
    private Integer quantity;
    private LocalDateTime createdAt;
}
