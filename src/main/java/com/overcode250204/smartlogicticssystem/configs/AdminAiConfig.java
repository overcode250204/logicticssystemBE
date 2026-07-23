package com.overcode250204.smartlogicticssystem.configs;

import com.overcode250204.smartlogicticssystem.ai.AdminAiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(AdminAiProperties.class)
public class AdminAiConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
