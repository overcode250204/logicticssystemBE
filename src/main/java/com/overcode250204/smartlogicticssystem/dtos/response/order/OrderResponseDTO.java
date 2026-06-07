package com.overcode250204.smartlogicticssystem.dtos.response.order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;

    private BigDecimal totalAmount;

    private BigDecimal totalWeight;

    private String deliveryAddress;

    private String status ;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponseDTO> items;
}
