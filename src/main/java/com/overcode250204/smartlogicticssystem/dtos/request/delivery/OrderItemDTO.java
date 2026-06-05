package com.overcode250204.smartlogicticssystem.dtos.request.delivery;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemDTO {

    private Long productId;

    private Integer quantity;
}

