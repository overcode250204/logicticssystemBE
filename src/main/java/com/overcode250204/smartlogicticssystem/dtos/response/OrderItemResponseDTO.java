package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponseDTO {
    private Long itemId;
    private String productName;
    private Integer quantityOrdered;
    private Integer quantityDelivered;
    private BigDecimal unitPrice;
    private BigDecimal weightKg;
    private BigDecimal volumeM3;
}
