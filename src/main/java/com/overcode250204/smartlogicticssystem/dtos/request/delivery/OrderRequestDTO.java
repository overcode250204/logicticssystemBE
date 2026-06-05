package com.overcode250204.smartlogicticssystem.dtos.request.delivery;

import com.overcode250204.smartlogicticssystem.entities.OrderItem;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;



import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    private Long customerId;

    private String deliveryAddress;

    private List<OrderItemDTO> items;

    private double longitude;

    private double latitude;
}
