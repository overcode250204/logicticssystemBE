package com.overcode250204.smartlogicticssystem.ai.impl;

import com.overcode250204.smartlogicticssystem.ai.AdminAiContext;
import com.overcode250204.smartlogicticssystem.ai.AdminAiContextRetrievalService;
import com.overcode250204.smartlogicticssystem.ai.AdminAiProperties;
import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.entities.InventoryBatch;
import com.overcode250204.smartlogicticssystem.entities.OrderException;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.repositories.InventoryBatchRepository;
import com.overcode250204.smartlogicticssystem.repositories.LinehaulTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.LocalTripRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderExceptionRepository;
import com.overcode250204.smartlogicticssystem.repositories.OrderRepository;
import com.overcode250204.smartlogicticssystem.repositories.VehicleRepository;
import com.overcode250204.smartlogicticssystem.services.ILogisticsDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAiContextRetrievalServiceImpl implements AdminAiContextRetrievalService {

    private final AdminAiProperties properties;
    private final ILogisticsDashboardService logisticsDashboardService;
    private final OrderRepository orderRepository;
    private final LinehaulTripRepository linehaulTripRepository;
    private final LocalTripRepository localTripRepository;
    private final VehicleRepository vehicleRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final OrderExceptionRepository orderExceptionRepository;

    @Override
    public AdminAiContext retrieve(String question, LogisticsDashboardTimeFilter timeFilter) {
        LogisticsDashboardTimeFilter safeTimeFilter = timeFilter == null
                ? LogisticsDashboardTimeFilter.MONTH
                : timeFilter;
        int limit = Math.max(1, properties.getMaxContextItems());

        LogisticsDashboardResponse dashboard = logisticsDashboardService.getStats(safeTimeFilter);
        List<Map<String, Object>> lowStockBatches = inventoryBatchRepository
                .findLowStockBatches(PageRequest.of(0, limit))
                .stream()
                .map(this::toLowStockMap)
                .toList();
        List<Map<String, Object>> recentExceptions = orderExceptionRepository
                .findRecentExceptions(PageRequest.of(0, limit))
                .stream()
                .map(this::toExceptionMap)
                .toList();

        return AdminAiContext.builder()
                .dashboard(toDashboardMap(dashboard, safeTimeFilter))
                .orderStatusCounts(toCountMap(orderRepository.countOrdersByStatus()))
                .linehaulTripStatusCounts(toCountMap(linehaulTripRepository.countLinehaulTripsByStatus()))
                .localTripStatusCounts(toCountMap(localTripRepository.countLocalTripsByStatus()))
                .vehicleStatusCounts(toCountMap(vehicleRepository.countVehiclesByStatus()))
                .lowStockBatches(lowStockBatches)
                .recentExceptions(recentExceptions)
                .usedData(List.of(
                        "logisticsDashboard",
                        "orderStatusCounts",
                        "linehaulTripStatusCounts",
                        "localTripStatusCounts",
                        "vehicleStatusCounts",
                        "lowStockBatches",
                        "recentExceptions"
                ))
                .build();
    }

    private Map<String, Object> toDashboardMap(LogisticsDashboardResponse dashboard,
                                               LogisticsDashboardTimeFilter timeFilter) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("timeFilter", timeFilter.name());
        values.put("totalOrders", dashboard.getTotalOrders());
        values.put("totalOrdersGrowth", dashboard.getTotalOrdersGrowth());
        values.put("successRate", dashboard.getSuccessRate());
        values.put("successRateGrowth", dashboard.getSuccessRateGrowth());
        values.put("activeFleetLinehaul", dashboard.getActiveFleetLinehaul());
        values.put("activeFleetLocal", dashboard.getActiveFleetLocal());
        values.put("criticalAlerts", dashboard.getCriticalAlerts());
        return values;
    }

    private Map<String, Long> toCountMap(List<Object[]> rows) {
        Map<String, Long> values = new LinkedHashMap<>();
        for (Object[] row : rows) {
            if (row.length >= 2 && row[0] != null && row[1] instanceof Number number) {
                values.put(row[0].toString(), number.longValue());
            }
        }
        return values;
    }

    private Map<String, Object> toLowStockMap(InventoryBatch batch) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("batchId", batch.getBatchId());
        values.put("barcode", batch.getBarcode());
        values.put("productCode", batch.getProduct().getProductCode());
        values.put("productName", batch.getProduct().getProductName());
        values.put("remainingQuantity", batch.getRemainingQuantity());
        values.put("minStockLevel", batch.getProduct().getMinStockLevel());
        values.put("status", batch.getStatus() == null ? null : batch.getStatus().name());
        values.put("expirationDate", batch.getExpirationDate());
        return values;
    }

    private Map<String, Object> toExceptionMap(OrderException exception) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", exception.getId());
        values.put("createdAt", exception.getCreatedAt());
        values.put("orderCode", exception.getOrder() == null ? null : exception.getOrder().getOrderCode());
        values.put("orderStatus", exception.getOrder() == null || exception.getOrder().getStatus() == null
                ? null
                : exception.getOrder().getStatus().name());
        values.put("reasonCategory", exception.getExceptionReason() == null
                ? null
                : exception.getExceptionReason().getCategory());
        values.put("reasonText", exception.getExceptionReason() == null
                ? null
                : exception.getExceptionReason().getReasonText());
        values.put("notes", exception.getNotes());
        return values;
    }
}
