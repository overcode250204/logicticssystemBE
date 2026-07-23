package com.overcode250204.smartlogicticssystem.ai.impl;

import com.overcode250204.smartlogicticssystem.ai.AdminAiContext;
import com.overcode250204.smartlogicticssystem.ai.AdminAiFallbackFactory;
import com.overcode250204.smartlogicticssystem.ai.AdminAiResponseSanitizer;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAiResponseSanitizerImpl implements AdminAiResponseSanitizer {

    private static final int MAX_ANSWER_LENGTH = 1200;
    private static final int MAX_LIST_ITEMS = 5;
    private static final Set<String> ALLOWED_SEVERITIES = Set.of("LOW", "MEDIUM", "HIGH");
    private static final List<String> BLOCKED_TERMS = List.of(
            "system prompt",
            "developer prompt",
            "hidden instruction",
            "api key",
            "credential",
            "secret",
            "mock",
            "hello from claude mock"
    );

    private final ObjectMapper objectMapper;
    private final AdminAiFallbackFactory fallbackFactory;

    @Override
    public AdminAiChatResponse sanitize(String rawResponse, AdminAiContext context) {
        if (rawResponse == null || rawResponse.isBlank() || containsBlockedTerm(rawResponse)) {
            log.warn("Admin AI response rejected before parsing. preview={}", preview(rawResponse));
            return fallbackFactory.general(context);
        }

        try {
            JsonNode root = objectMapper.readTree(extractJson(rawResponse));
            String answer = limit(cleanText(root.path("answer").asText()), MAX_ANSWER_LENGTH);

            if (answer.isBlank() || containsBlockedTerm(answer)) {
                log.warn("Admin AI response rejected because answer is empty or blocked. preview={}", preview(rawResponse));
                return fallbackFactory.general(context);
            }

            return AdminAiChatResponse.builder()
                    .answer(answer)
                    .severity(normalizeSeverity(root.path("severity").asText()))
                    .insights(cleanStringList(root.path("insights")))
                    .recommendedActions(cleanStringList(root.path("recommendedActions")))
                    .usedData(resolveUsedData(root.path("usedData"), context))
                    .confidence(normalizeConfidence(root.path("confidence").asDouble(0.0)))
                    .fallback(false)
                    .build();
        } catch (Exception ex) {
            String plainAnswer = limit(cleanText(stripCodeFence(rawResponse)), MAX_ANSWER_LENGTH);
            if (!plainAnswer.isBlank() && !containsBlockedTerm(plainAnswer)) {
                log.warn(
                        "Admin AI response was not valid JSON, using sanitized plain text. error={}, preview={}",
                        ex.getMessage(),
                        preview(rawResponse)
                );
                return AdminAiChatResponse.builder()
                        .answer(plainAnswer)
                        .severity("LOW")
                        .insights(List.of())
                        .recommendedActions(List.of(
                                "Hỏi cụ thể hơn theo mã đơn, mã chuyến, kho, tài xế hoặc trạng thái nếu cần phân tích sâu hơn."
                        ))
                        .usedData(context == null ? List.of() : context.usedData())
                        .confidence(0.35)
                        .fallback(false)
                        .build();
            }

            log.warn("Admin AI response parsing failed. error={}, preview={}", ex.getMessage(), preview(rawResponse));
            return fallbackFactory.general(context);
        }
    }

    private String extractJson(String rawResponse) {
        String value = stripCodeFence(rawResponse);
        int start = value.indexOf('{');
        int end = value.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return value.substring(start, end + 1);
        }
        return value;
    }

    private String stripCodeFence(String value) {
        String cleaned = value.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", "");
            int fenceEnd = cleaned.lastIndexOf("```");
            if (fenceEnd >= 0) {
                cleaned = cleaned.substring(0, fenceEnd);
            }
        }
        return cleaned.trim();
    }

    private String normalizeSeverity(String severity) {
        String normalized = severity == null ? "LOW" : severity.trim().toUpperCase(Locale.ROOT);
        return ALLOWED_SEVERITIES.contains(normalized) ? normalized : "LOW";
    }

    private List<String> cleanStringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (!node.isArray()) {
            return values;
        }
        for (JsonNode item : node) {
            String value = limit(cleanText(item.asText()), 220);
            if (!value.isBlank() && !containsBlockedTerm(value)) {
                values.add(value);
            }
            if (values.size() == MAX_LIST_ITEMS) {
                break;
            }
        }
        return values;
    }

    private List<String> resolveUsedData(JsonNode node, AdminAiContext context) {
        List<String> sanitized = cleanStringList(node);
        if (!sanitized.isEmpty()) {
            return sanitized;
        }
        return context == null ? List.of() : context.usedData();
    }

    private double normalizeConfidence(double confidence) {
        if (Double.isNaN(confidence) || Double.isInfinite(confidence)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, confidence));
    }

    private boolean containsBlockedTerm(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return BLOCKED_TERMS.stream().anyMatch(normalized::contains);
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String limit(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength).trim();
    }

    private String preview(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String compact = cleanText(value);
        return compact.length() <= 300 ? compact : compact.substring(0, 300);
    }
}
