package com.overcode250204.smartlogicticssystem.ai;

import com.overcode250204.smartlogicticssystem.dtos.response.AdminAiChatResponse;

public interface AdminAiFallbackFactory {
    AdminAiChatResponse general(AdminAiContext context);

    AdminAiChatResponse noRelevantData();
}
