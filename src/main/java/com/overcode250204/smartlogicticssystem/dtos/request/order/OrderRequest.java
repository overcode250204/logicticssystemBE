package com.overcode250204.smartlogicticssystem.dtos.request.order;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {

    private String deliveryAddress;

    private double latitude;

    private double longitude;

    private List<OrderItemRequest> items;
}
