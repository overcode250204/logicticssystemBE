package com.overcode250204.smartlogicticssystem.dtos;

import com.overcode250204.smartlogicticssystem.exception.ProductErrorCode;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank(message = ProductErrorCode.Messages.PRODUCT_CODE_REQUIRED)
    @Size(max = 50, message = ProductErrorCode.Messages.PRODUCT_CODE_TOO_LONG)
    private String productCode;

    @NotBlank(message = ProductErrorCode.Messages.PRODUCT_NAME_REQUIRED)
    @Size(max = 150, message = ProductErrorCode.Messages.PRODUCT_NAME_TOO_LONG)
    private String productName;

    @NotNull(message = ProductErrorCode.Messages.PRICE_REQUIRED)
    @Positive(message = ProductErrorCode.Messages.PRICE_POSITIVE)
    private BigDecimal price;

    private Integer minStockLevel;

    @NotNull(message = ProductErrorCode.Messages.SUPPLIER_ID_REQUIRED)
    private Integer supplierId;

    private String supplierName;
}
