package com.overcode250204.smartlogicticssystem.dtos.request.delivery;

import com.overcode250204.smartlogicticssystem.dtos.response.order.OrderResponseDTO;
import com.overcode250204.smartlogicticssystem.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
public class RoutePointDTO {

    private Long id;

    private OrderResponseDTO order;

    private Integer sequenceNumber;

    private String status;

    private LocalDateTime updatedAt;
}
