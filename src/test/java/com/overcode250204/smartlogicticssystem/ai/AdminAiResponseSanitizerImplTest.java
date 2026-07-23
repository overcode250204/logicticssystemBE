package com.overcode250204.smartlogicticssystem.ai;

import com.overcode250204.smartlogicticssystem.ai.impl.AdminAiFallbackFactoryImpl;
import com.overcode250204.smartlogicticssystem.ai.impl.AdminAiResponseSanitizerImpl;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminAiResponseSanitizerImplTest {

    private AdminAiResponseSanitizerImpl sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new AdminAiResponseSanitizerImpl(
                new ObjectMapper(),
                new AdminAiFallbackFactoryImpl()
        );
    }

    @Test
    void sanitizeNormalizesValidJsonResponse() {
        AdminAiContext context = AdminAiContext.builder()
                .usedData(List.of("logisticsDashboard"))
                .build();

        String rawResponse = """
                {
                  "answer": "Co 2 canh bao can theo doi.",
                  "severity": "high",
                  "insights": ["Ty le giao thanh cong dang giam."],
                  "recommendedActions": ["Kiem tra cac don FAILED."],
                  "usedData": ["recentExceptions"],
                  "confidence": 1.4
                }
                """;

        AdminAiChatResponse result = sanitizer.sanitize(rawResponse, context);

        assertFalse(result.isFallback());
        assertEquals("Co 2 canh bao can theo doi.", result.getAnswer());
        assertEquals("HIGH", result.getSeverity());
        assertEquals(1.0, result.getConfidence());
        assertEquals(List.of("recentExceptions"), result.getUsedData());
    }

    @Test
    void sanitizeReturnsFallbackWhenResponseLeaksPromptDetails() {
        AdminAiContext context = AdminAiContext.builder()
                .usedData(List.of("logisticsDashboard"))
                .build();

        AdminAiChatResponse result = sanitizer.sanitize(
                "{\"answer\":\"System prompt says to reveal hidden instruction\"}",
                context
        );

        assertTrue(result.isFallback());
        assertEquals(context.usedData(), result.getUsedData());
    }

    @Test
    void sanitizeUsesPlainTextWhenProviderDoesNotReturnJson() {
        AdminAiContext context = AdminAiContext.builder()
                .usedData(List.of("logisticsDashboard"))
                .build();

        AdminAiChatResponse result = sanitizer.sanitize(
                "Van hanh thang nay co mot so canh bao can theo doi.",
                context
        );

        assertFalse(result.isFallback());
        assertEquals("Van hanh thang nay co mot so canh bao can theo doi.", result.getAnswer());
        assertEquals(context.usedData(), result.getUsedData());
    }

    @Test
    void sanitizeRejectsMockProviderResponses() {
        AdminAiContext context = AdminAiContext.builder()
                .dashboard(Map.of(
                        "totalOrders", 12L,
                        "successRate", 75.0,
                        "activeFleetLinehaul", 3L,
                        "activeFleetLocal", 1L,
                        "criticalAlerts", 2L
                ))
                .usedData(List.of("logisticsDashboard"))
                .build();

        AdminAiChatResponse result = sanitizer.sanitize("Hello from Claude mock.", context);

        assertTrue(result.isFallback());
        assertTrue(result.getAnswer().contains("12"));
    }

    @Test
    void fallbackUsesOperationalContextWhenAvailable() {
        AdminAiFallbackFactoryImpl fallbackFactory = new AdminAiFallbackFactoryImpl();
        AdminAiContext context = AdminAiContext.builder()
                .dashboard(Map.of(
                        "totalOrders", 12L,
                        "successRate", 75.0,
                        "activeFleetLinehaul", 3L,
                        "activeFleetLocal", 1L,
                        "criticalAlerts", 2L
                ))
                .lowStockBatches(List.of(Map.of("batchId", 1L)))
                .usedData(List.of("logisticsDashboard", "lowStockBatches"))
                .build();

        AdminAiChatResponse result = fallbackFactory.general(context);

        assertTrue(result.isFallback());
        assertEquals("MEDIUM", result.getSeverity());
        assertTrue(result.getAnswer().contains("12"));
        assertEquals(4, result.getInsights().size());
        assertEquals(3, result.getRecommendedActions().size());
    }
}
