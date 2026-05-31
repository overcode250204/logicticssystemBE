package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.InventoryBatchStatus;
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
public class InventoryBatchResponseDTO {
    private Long batchId;
    private ProductResponseDTO product;
    private SupplierResponseDTO supplier;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private String barcode;
    private String barcodeImageUrl;
    private Integer quantity;
    private Integer remainingQuantity;
    private InventoryBatchStatus status;
}
