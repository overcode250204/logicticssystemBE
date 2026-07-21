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
    private long totalOrders;

    private double totalOrdersGrowth;

    private double successRate;

    private double successRateGrowth;

    private long activeFleetLinehaul;

    private long activeFleetLocal;

    private long criticalAlerts;
}
