package com.overcode250204.smartlogicticssystem.dtos.response.order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDTO {

    private Long productId;

    private Integer quantity;

    private BigDecimal price;
}
