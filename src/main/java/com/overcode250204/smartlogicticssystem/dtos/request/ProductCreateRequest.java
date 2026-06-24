package com.overcode250204.smartlogicticssystem.dtos.request;

import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = ProductErrorCode.Messages.PRODUCT_NAME_REQUIRED)
    @Size(max = 150, message = ProductErrorCode.Messages.PRODUCT_NAME_TOO_LONG)
    private String productName;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @NotNull(message = ProductErrorCode.Messages.PRICE_REQUIRED)
    @Positive(message = ProductErrorCode.Messages.PRICE_POSITIVE)
    private BigDecimal price;

    @NotNull(message = ProductErrorCode.Messages.WEIGHT_REQUIRED)
    @Positive(message = ProductErrorCode.Messages.WEIGHT_POSITIVE)
    private BigDecimal weight;

    @Positive(message = "Length must be positive")
    private BigDecimal length;

    @Positive(message = "Width must be positive")
    private BigDecimal width;

    @Positive(message = "Height must be positive")
    private BigDecimal height;


    /**
     * Optional: ID of the base unit (quantity unit, e.g. PCS).
     */
    private Long baseUnitId;

    @PositiveOrZero(message = "Minimum stock level must not be negative")
    private Integer minStockLevel;

    @NotNull(message = ProductErrorCode.Messages.SUPPLIER_ID_REQUIRED)
    private Integer supplierId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
