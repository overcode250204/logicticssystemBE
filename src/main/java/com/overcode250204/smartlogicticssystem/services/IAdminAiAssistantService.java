package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.request.AdminAiChatRequest;
import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;

public interface IAdminAiAssistantService {
    AdminAiChatResponse chat(AdminAiChatRequest request);
}
