package com.overcode250204.smartlogicticssystem.dtos.response;

import com.overcode250204.smartlogicticssystem.enums.OrderStatus;
import com.overcode250204.smartlogicticssystem.enums.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private String orderCode;
    private String barcodeUrl;
    private String customerName;
    private String phone;
    private String deliveryAddress;
    private String deliveryProvince;
    private Double latitude;
    private Double longitude;
    private WarehouseResponseDTO assignedHub;
    private RouteConfigResponseDTO routeConfig;
    private BigDecimal totalAmount;
    private PaymentType paymentType;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> items;
}
