package com.overcode250204.smartlogicticssystem.ai;

import java.util.Optional;

public interface LlmClient {
    Optional<String> complete(AdminAiPrompt prompt);
}
