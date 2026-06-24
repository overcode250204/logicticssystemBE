package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private String sku;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private UnitResponseDTO baseUnit;
    private Integer minStockLevel;
    private SupplierResponseDTO supplier;
    private ProductCategoryResponseDTO category;
}
