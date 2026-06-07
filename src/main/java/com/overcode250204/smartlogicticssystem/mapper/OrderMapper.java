package com.overcode250204.smartlogicticssystem.mapper;


import com.overcode250204.smartlogicticssystem.dtos.request.order.OrderRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.order.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponseDTO toResponse(Order order) {
        if (order == null) return null;
        return OrderResponseDTO.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .totalWeight(order.getTotalWeight())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(OrderRequest dto, Order entity) {
        if (dto == null || entity == null) return;
        if (dto.getDeliveryAddress() != null) entity.setDeliveryAddress(dto.getDeliveryAddress());
//        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
//        if (dto.getTotalAmount() != null) entity.setTotalAmount(dto.getTotalAmount());
//        if (dto.getTotalWeight() != null) entity.setTotalWeight(dto.getTotalWeight());
    }
}
