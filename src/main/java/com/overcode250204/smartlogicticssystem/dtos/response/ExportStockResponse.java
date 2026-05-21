package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.dtos.InventoryBatchDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportStockResponse {
    private Long productId;

    private String productName;

    private Integer requestedQuantity;

    private Integer exportedQuantity;

    private Integer remainingStock;

    private List<InventoryBatchDTO> batches;
}
