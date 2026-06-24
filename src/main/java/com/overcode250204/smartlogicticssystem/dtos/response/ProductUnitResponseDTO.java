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
public class ProductUnitResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private UnitResponseDTO unit;
    private BigDecimal conversionFactor;
}
