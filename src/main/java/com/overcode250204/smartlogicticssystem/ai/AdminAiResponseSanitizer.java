package com.overcode250204.smartlogicticssystem.ai;

import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;

public interface AdminAiResponseSanitizer {
    AdminAiChatResponse sanitize(String rawResponse, AdminAiContext context);
}
