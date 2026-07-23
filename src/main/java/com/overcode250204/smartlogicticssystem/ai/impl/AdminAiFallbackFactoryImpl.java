package com.overcode250204.smartlogicticssystem.ai.impl;

import com.overcode250204.smartlogicticssystem.ai.AdminAiContext;
import com.overcode250204.smartlogicticssystem.ai.AdminAiFallbackFactory;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AdminAiFallbackFactoryImpl implements AdminAiFallbackFactory {

    @Override
    public AdminAiChatResponse general(AdminAiContext context) {
        if (context != null && context.dashboard() != null && !context.dashboard().isEmpty()) {
            return fromOperationalContext(context);
        }

        return AdminAiChatResponse.builder()
                .answer("Mình chưa thể tổng hợp câu trả lời rõ ràng lúc này. Bạn có thể thử lại với câu hỏi ngắn hơn hoặc chọn một phạm vi dữ liệu cụ thể hơn.")
                .severity("LOW")
                .insights(List.of())
                .recommendedActions(List.of(
                        "Thử hỏi với một mã đơn, mã chuyến, kho, tài xế hoặc trạng thái cụ thể.",
                        "Chọn khoảng thời gian rõ hơn như hôm nay, tuần này hoặc tháng này."
                ))
                .usedData(context == null ? List.of() : context.usedData())
                .confidence(0.0)
                .fallback(true)
                .build();
    }

    @Override
    public AdminAiChatResponse noRelevantData() {
        return AdminAiChatResponse.builder()
                .answer("Mình chưa tìm thấy dữ liệu vận hành phù hợp với câu hỏi này. Bạn có thể thử hỏi theo một khu vực, kho, tuyến hoặc trạng thái đơn hàng cụ thể hơn.")
                .severity("LOW")
                .insights(List.of())
                .recommendedActions(List.of(
                        "Hỏi theo nhóm dữ liệu như đơn hàng, chuyến xe, phương tiện, tồn kho hoặc sự cố.",
                        "Bổ sung khoảng thời gian nếu bạn muốn xem xu hướng vận hành."
                ))
                .usedData(List.of())
                .confidence(0.0)
                .fallback(true)
                .build();
    }

    private AdminAiChatResponse fromOperationalContext(AdminAiContext context) {
        Map<String, Object> dashboard = context.dashboard();
        long totalOrders = longValue(dashboard.get("totalOrders"));
        double successRate = doubleValue(dashboard.get("successRate"));
        long criticalAlerts = longValue(dashboard.get("criticalAlerts"));
        long activeLinehaul = longValue(dashboard.get("activeFleetLinehaul"));
        long activeLocal = longValue(dashboard.get("activeFleetLocal"));
        long activeFleet = activeLinehaul + activeLocal;
        List<Map<String, Object>> lowStockBatches = safeList(context.lowStockBatches());

        String severity = criticalAlerts > 0 ? "MEDIUM" : "LOW";
        List<String> insights = new ArrayList<>();
        insights.add("Tổng đơn trong phạm vi đang chọn là %d, tỷ lệ giao thành công đạt %.1f%%."
                .formatted(totalOrders, successRate));
        insights.add("Đội xe đang hoạt động gồm %d xe linehaul và %d xe local."
                .formatted(activeLinehaul, activeLocal));
        if (criticalAlerts > 0) {
            insights.add("Có %d cảnh báo vận hành cần được rà soát trong phạm vi này."
                    .formatted(criticalAlerts));
        }
        if (!lowStockBatches.isEmpty()) {
            insights.add("Có %d batch tồn kho thấp cần theo dõi bổ sung."
                    .formatted(lowStockBatches.size()));
        }

        List<String> recommendedActions = new ArrayList<>();
        if (criticalAlerts > 0) {
            recommendedActions.add("Ưu tiên kiểm tra danh sách cảnh báo và các đơn có ngoại lệ mới nhất.");
        }
        if (successRate < 90.0) {
            recommendedActions.add("Rà soát các trạng thái FAILED, IN_TRANSIT_LOCAL và các chuyến có nguy cơ trễ SLA.");
        }
        if (!lowStockBatches.isEmpty()) {
            recommendedActions.add("Kiểm tra các batch tồn kho thấp để lên kế hoạch bổ sung hoặc điều chuyển hàng.");
        }
        if (recommendedActions.isEmpty()) {
            recommendedActions.add("Tiếp tục theo dõi dashboard vận hành và hỏi sâu hơn theo kho, tuyến, đơn hoặc chuyến cụ thể.");
        }

        String answer = "Trong phạm vi dữ liệu đang chọn, hệ thống ghi nhận %d đơn hàng, tỷ lệ giao thành công %.1f%%, %d xe đang hoạt động và %d cảnh báo vận hành."
                .formatted(totalOrders, successRate, activeFleet, criticalAlerts);

        return AdminAiChatResponse.builder()
                .answer(answer)
                .severity(severity)
                .insights(insights)
                .recommendedActions(recommendedActions)
                .usedData(safeList(context.usedData()))
                .confidence(0.55)
                .fallback(true)
                .build();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ex) {
            return 0L;
        }
    }

    private double doubleValue(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
