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
public class ProductUpdateRequest {
    @NotBlank(message = ProductErrorCode.Messages.PRODUCT_CODE_REQUIRED)
    @Size(max = 50, message = ProductErrorCode.Messages.PRODUCT_CODE_TOO_LONG)
    private String productCode;

    @NotBlank(message = ProductErrorCode.Messages.PRODUCT_NAME_REQUIRED)
    @Size(max = 150, message = ProductErrorCode.Messages.PRODUCT_NAME_TOO_LONG)
    private String productName;

    @NotNull(message = ProductErrorCode.Messages.PRICE_REQUIRED)
    @Positive(message = ProductErrorCode.Messages.PRICE_POSITIVE)
    private BigDecimal price;

    @PositiveOrZero(message = "Minimum stock level must not be negative")
    private Integer minStockLevel;

    @NotNull(message = ProductErrorCode.Messages.SUPPLIER_ID_REQUIRED)
    private Integer supplierId;
}
