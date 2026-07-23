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
import com.overcode250204.smartlogicticssystem.services.IAdminAiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAiAssistantServiceImpl implements IAdminAiAssistantService {

    private final AdminAiContextRetrievalService contextRetrievalService;
    private final AdminAiPromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final AdminAiResponseSanitizer responseSanitizer;
    private final AdminAiFallbackFactory fallbackFactory;

    @Override
    public AdminAiChatResponse chat(AdminAiChatRequest request) {
        String question = request.getQuestion() == null ? "" : request.getQuestion().trim();
        LogisticsDashboardTimeFilter timeFilter = request.getTimeFilter() == null
                ? LogisticsDashboardTimeFilter.MONTH
                : request.getTimeFilter();

        if (question.isBlank()) {
            return fallbackFactory.noRelevantData();
        }

        AdminAiContext context = contextRetrievalService.retrieve(question, timeFilter);
        AdminAiPrompt prompt = promptBuilder.build(question, context);

        return llmClient.complete(prompt)
                .map(rawResponse -> responseSanitizer.sanitize(rawResponse, context))
                .orElseGet(() -> fallbackFactory.general(context));
    }
}
