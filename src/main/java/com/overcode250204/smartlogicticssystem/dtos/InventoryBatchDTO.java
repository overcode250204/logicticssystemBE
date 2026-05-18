package com.overcode250204.smartlogicticssystem.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryBatchDTO {
    private Long batchId;
    private Long productId;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private Integer quantity;
    private String status;
}
