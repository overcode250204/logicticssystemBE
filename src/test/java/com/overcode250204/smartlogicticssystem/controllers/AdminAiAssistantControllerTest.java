package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.dtos.request.AdminAiChatRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.services.IAdminAiAssistantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAiAssistantControllerTest {

    private IAdminAiAssistantService adminAiAssistantService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        adminAiAssistantService = mock(IAdminAiAssistantService.class);
        objectMapper = new ObjectMapper();
        AdminAiAssistantController controller = new AdminAiAssistantController(adminAiAssistantService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void chatDelegatesRequestToService() throws Exception {
        AdminAiChatRequest request = AdminAiChatRequest.builder()
                .question("Tổng quan vận hành hôm nay?")
                .timeFilter(LogisticsDashboardTimeFilter.TODAY)
                .build();
        AdminAiChatResponse response = AdminAiChatResponse.builder()
                .answer("Vận hành hôm nay ổn định.")
                .severity("LOW")
                .confidence(0.8)
                .build();

        when(adminAiAssistantService.chat(argThat(argument ->
                argument.getQuestion().equals(request.getQuestion())
                        && argument.getTimeFilter() == LogisticsDashboardTimeFilter.TODAY
        ))).thenReturn(response);

        mockMvc.perform(post("/api/admin/ai-assistant/chat")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value("Vận hành hôm nay ổn định."))
                .andExpect(jsonPath("$.data.severity").value("LOW"))
                .andExpect(jsonPath("$.data.confidence").value(0.8));

        verify(adminAiAssistantService).chat(argThat(argument ->
                argument.getQuestion().equals(request.getQuestion())
                        && argument.getTimeFilter() == LogisticsDashboardTimeFilter.TODAY
        ));
    }
}
