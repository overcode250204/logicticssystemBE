package com.overcode250204.smartlogicticssystem.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "app.ai")
public class AdminAiProperties {

    private boolean enabled = false;

    private String apiKey;

    private String baseUrl = "https://9routerhelios.duckdns.org/v1";

    private String model = "Hermes-9router-MainModel";

    private Duration timeout = Duration.ofSeconds(60);

    private int maxContextItems = 8;
}
