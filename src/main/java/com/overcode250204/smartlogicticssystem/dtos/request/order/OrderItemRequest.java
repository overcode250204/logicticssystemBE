package com.overcode250204.smartlogicticssystem.dtos.request.order;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemRequest {

    private Long productId;

    private Integer quantity;

}

