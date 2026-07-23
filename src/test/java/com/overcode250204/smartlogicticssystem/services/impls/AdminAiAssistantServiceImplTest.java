package com.overcode250204.smartlogicticssystem.services.impls;

import com.overcode250204.smartlogicticssystem.ai.AdminAiContext;
import com.overcode250204.smartlogicticssystem.ai.AdminAiContextRetrievalService;
import com.overcode250204.smartlogicticssystem.ai.AdminAiFallbackFactory;
import com.overcode250204.smartlogicticssystem.ai.AdminAiPrompt;
import com.overcode250204.smartlogicticssystem.ai.AdminAiPromptBuilder;
import com.overcode250204.smartlogicticssystem.ai.AdminAiResponseSanitizer;
import com.overcode250204.smartlogicticssystem.ai.LlmClient;
import com.overcode250204.smartlogicticssystem.dtos.request.AdminAiChatRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminAiAssistantServiceImplTest {

    private AdminAiContextRetrievalService contextRetrievalService;
    private AdminAiPromptBuilder promptBuilder;
    private LlmClient llmClient;
    private AdminAiResponseSanitizer responseSanitizer;
    private AdminAiFallbackFactory fallbackFactory;
    private AdminAiAssistantServiceImpl service;

    @BeforeEach
    void setUp() {
        contextRetrievalService = mock(AdminAiContextRetrievalService.class);
        promptBuilder = mock(AdminAiPromptBuilder.class);
        llmClient = mock(LlmClient.class);
        responseSanitizer = mock(AdminAiResponseSanitizer.class);
        fallbackFactory = mock(AdminAiFallbackFactory.class);

        service = new AdminAiAssistantServiceImpl(
                contextRetrievalService,
                promptBuilder,
                llmClient,
                responseSanitizer,
                fallbackFactory
        );
    }

    @Test
    void chatRetrievesDbContextBuildsPromptAndSanitizesLlmResponse() {
        AdminAiChatRequest request = AdminAiChatRequest.builder()
                .question("Tình hình vận hành tháng này thế nào?")
                .timeFilter(LogisticsDashboardTimeFilter.MONTH)
                .build();
        AdminAiContext context = AdminAiContext.builder()
                .usedData(List.of("logisticsDashboard"))
                .build();
        AdminAiPrompt prompt = new AdminAiPrompt("system", "user");
        AdminAiChatResponse expected = AdminAiChatResponse.builder()
                .answer("Vận hành ổn định.")
                .confidence(0.8)
                .fallback(false)
                .build();

        when(contextRetrievalService.retrieve(request.getQuestion(), LogisticsDashboardTimeFilter.MONTH))
                .thenReturn(context);
        when(promptBuilder.build(request.getQuestion(), context)).thenReturn(prompt);
        when(llmClient.complete(prompt)).thenReturn(Optional.of("{\"answer\":\"ok\"}"));
        when(responseSanitizer.sanitize("{\"answer\":\"ok\"}", context)).thenReturn(expected);

        AdminAiChatResponse actual = service.chat(request);

        assertEquals(expected, actual);
        assertFalse(actual.isFallback());
        verify(responseSanitizer).sanitize("{\"answer\":\"ok\"}", context);
    }

    @Test
    void chatReturnsFriendlyFallbackWhenLlmIsUnavailable() {
        AdminAiChatRequest request = AdminAiChatRequest.builder()
                .question("Có cảnh báo nào không?")
                .build();
        AdminAiContext context = AdminAiContext.builder()
                .usedData(List.of("recentExceptions"))
                .build();
        AdminAiPrompt prompt = new AdminAiPrompt("system", "user");
        AdminAiChatResponse fallback = AdminAiChatResponse.builder()
                .answer("Mình chưa thể tổng hợp câu trả lời rõ ràng lúc này.")
                .fallback(true)
                .build();

        when(contextRetrievalService.retrieve(request.getQuestion(), LogisticsDashboardTimeFilter.MONTH))
                .thenReturn(context);
        when(promptBuilder.build(request.getQuestion(), context)).thenReturn(prompt);
        when(llmClient.complete(prompt)).thenReturn(Optional.empty());
        when(fallbackFactory.general(context)).thenReturn(fallback);

        AdminAiChatResponse actual = service.chat(request);

        assertEquals(fallback, actual);
    }
}
