package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryExportResponseDTO {
    private ProductResponseDTO product;
    private Integer requestedQuantity;
    private Integer exportedQuantity;
    private Integer remainingStock;
    private List<InventoryExportBatchDTO> batches;
}
