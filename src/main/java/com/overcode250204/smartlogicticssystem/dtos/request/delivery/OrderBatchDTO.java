package com.overcode250204.smartlogicticssystem.dtos.request.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderBatchDTO {

    private Long id;

    private Long driverId; // Shipper được gán cụm đơn gom này

    private String status = "ASSIGNED"; // ASSIGNED, PACKING, PICKED_UP, COMPLETED

    private BigDecimal totalWeight;

    private LocalDateTime createdAt = LocalDateTime.now();

    private List<RoutePointDTO> routePoints;
}
