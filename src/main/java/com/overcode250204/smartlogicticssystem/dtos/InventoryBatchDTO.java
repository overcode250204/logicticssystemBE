package com.overcode250204.smartlogicticssystem.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryBatchDTO {
    private Long batchId;
    private Long productId;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private Integer quantity;
    private Integer remainingQuantity;
    private String status;
}
