package com.overcode250204.smartlogicticssystem.dtos.response;

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
public class InventoryBatchSimpleResponseDTO {
    private Long batchId;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private Integer quantity;
    private Integer remainingQuantity;
    private String status;
}
