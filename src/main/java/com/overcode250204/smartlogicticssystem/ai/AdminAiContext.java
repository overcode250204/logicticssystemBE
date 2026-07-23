package com.overcode250204.smartlogicticssystem.ai;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record AdminAiContext(
        Map<String, Object> dashboard,
        Map<String, Long> orderStatusCounts,
        Map<String, Long> linehaulTripStatusCounts,
        Map<String, Long> localTripStatusCounts,
        Map<String, Long> vehicleStatusCounts,
        List<Map<String, Object>> lowStockBatches,
        List<Map<String, Object>> recentExceptions,
        List<String> usedData
) {
}
