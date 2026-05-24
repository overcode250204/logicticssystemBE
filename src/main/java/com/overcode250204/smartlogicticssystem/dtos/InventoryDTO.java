package com.overcode250204.smartlogicticssystem.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryDTO {

    // Product fields
    private Long productId;
    private String productCode;
    private String productName;
    private Integer minStockLevel;
    private BigDecimal price;

    // Supplier fields
    private Integer supplierId;
    private String supplierName;
    private String contactPhone;
    private String address;

    // Batch fields
    private Long batchId;
    private LocalDateTime importDate;
    private LocalDateTime expirationDate;
    private Integer quantity;
    private Integer remainingQuantity;
    private String status;

    // Transaction fields
    private Long transactionId;
    private String type;
    private LocalDateTime createdAt;

    // Export stock fields
    private Integer requestedQuantity;
    private Integer exportedQuantity;
    private LocalDateTime exportDate;
    private Integer remainingStock;

    // Nested unified representations
    private InventoryDTO supplier;
    private InventoryDTO product;
    private List<InventoryDTO> batches;

}
