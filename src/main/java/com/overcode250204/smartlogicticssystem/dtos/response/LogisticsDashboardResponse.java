package com.overcode250204.smartlogicticssystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsDashboardResponse {
    private Long totalOrders;

    private Double totalOrderRateChange;

    private Double successRate;

    private Long activeFleet;

    private Long criticalAlerts;

    private Double successRateChange;

}
