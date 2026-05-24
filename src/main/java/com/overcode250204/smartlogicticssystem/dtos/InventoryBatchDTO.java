package com.overcode250204.smartlogicticssystem.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InventoryBatchDTO {
    private Long batchId;
    private Long productId;
    private String productName;
    private long requestedQuantity;
    private long exportedQuantity;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private Integer quantity;
    private Integer remainingQuantity;
    private String status;


}
