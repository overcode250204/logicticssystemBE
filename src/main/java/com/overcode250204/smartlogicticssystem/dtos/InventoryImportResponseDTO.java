package com.overcode250204.smartlogicticssystem.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InventoryImportResponseDTO {
    private Long batchId;
    private Long transactionId;
    private Long productId;
    private Integer quantity;
    private Integer remainingQuantity;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private String status;
}
