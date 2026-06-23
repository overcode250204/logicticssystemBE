package com.overcode250204.smartlogicticssystem.mapper;

import com.overcode250204.smartlogicticssystem.dtos.response.OrderItemResponseDTO;
import com.overcode250204.smartlogicticssystem.dtos.response.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Order;
import com.overcode250204.smartlogicticssystem.entities.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final WarehouseMapper warehouseMapper;
    private final RouteConfigMapper routeConfigMapper;

    public OrderResponseDTO toResponse(Order entity, List<OrderItem> items) {
        if (entity == null) {
            return null;
        }

        List<OrderItemResponseDTO> itemDTOs = null;
        if (items != null) {
            itemDTOs = items.stream().map(this::toItemResponse).collect(Collectors.toList());
        }

        Double latitude = null;
        Double longitude = null;
        if (entity.getDeliveryPoint() != null) {
            latitude = entity.getDeliveryPoint().getY();
            longitude = entity.getDeliveryPoint().getX();
        }

        return OrderResponseDTO.builder()
                .orderId(entity.getOrderId())
                .orderCode(entity.getOrderCode())
                .barcodeUrl(entity.getBarcodeUrl())
                .customerName(entity.getCustomerName())
                .phone(entity.getPhone())
                .deliveryAddress(entity.getDeliveryAddress())
                .deliveryProvince(entity.getDeliveryProvince())
                .latitude(latitude)
                .longitude(longitude)
                .assignedHub(warehouseMapper.toResponse(entity.getAssignedHub()))
                .routeConfig(routeConfigMapper.toResponse(entity.getRouteConfig()))
                .totalAmount(entity.getTotalAmount())
                .paymentType(entity.getPaymentType())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .items(itemDTOs)
                .build();
    }

    public OrderItemResponseDTO toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return OrderItemResponseDTO.builder()
                .itemId(item.getItemId())
                .productName(item.getProductName())
                .quantityOrdered(item.getQuantityOrdered())
                .quantityDelivered(item.getQuantityDelivered())
                .unitPrice(item.getUnitPrice())
                .weightKg(item.getWeightKg())
                .volumeM3(item.getVolumeM3())
                .build();
    }
}
