package com.overcode250204.smartlogicticssystem.ai.impl;

import com.overcode250204.smartlogicticssystem.ai.AdminAiPrompt;
import com.overcode250204.smartlogicticssystem.ai.AdminAiProperties;
import com.overcode250204.smartlogicticssystem.ai.LlmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCompatibleLlmClient implements LlmClient {

    private final AdminAiProperties properties;
    private final ObjectMapper objectMapper;
    private Map<String, String> localEnvCache;

    @Override
    public Optional<String> complete(AdminAiPrompt prompt) {
        boolean enabled = resolveEnabled();
        String apiKey = resolveSetting(properties.getApiKey(), "AI_API_KEY");
        String model = resolveSetting(properties.getModel(), "AI_MODEL");
        if (model == null || model.isBlank()) {
            model = "claude-sonnet-5";
        }

        if (!enabled) {
            log.warn("Admin AI provider is disabled. Set AI_ENABLED=true to enable it.");
            return Optional.empty();
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Admin AI provider API key is missing. Set AI_API_KEY in environment or .env.");
            return Optional.empty();
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(properties.getTimeout())
                    .build();
            URI requestUri = resolveChatCompletionsUri();

            String requestBody = buildRequestBody(prompt, model, true);

            HttpResponse<String> response = sendRequest(client, requestUri, apiKey, requestBody);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn(
                        "Admin AI provider returned status {}. url={}, model={}, bodyPreview={}",
                        response.statusCode(),
                        requestUri,
                        model,
                        preview(response.body())
                );
                return Optional.empty();
            }

            if (response.body() == null || response.body().isBlank()) {
                log.warn(
                        "Admin AI provider returned empty body. Retrying without response_format. url={}, model={}",
                        requestUri,
                        model
                );
                response = sendRequest(client, requestUri, apiKey, buildRequestBody(prompt, model, false));
            }

            log.info(
                    "Admin AI provider returned status {}. url={}, model={}, bodyLength={}",
                    response.statusCode(),
                    requestUri,
                    model,
                    response.body() == null ? 0 : response.body().length()
            );
            return parseProviderResponse(response.body());
        } catch (Exception ex) {
            log.warn("Admin AI provider request failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private String buildRequestBody(AdminAiPrompt prompt,
                                    String model,
                                    boolean includeResponseFormat) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.2);
        payload.put("stream", false);
        payload.put("messages", List.of(
                Map.of("role", "system", "content", prompt.systemPrompt()),
                Map.of("role", "user", "content", prompt.userPrompt())
        ));
        if (includeResponseFormat) {
            payload.put("response_format", Map.of("type", "json_object"));
        }
        return objectMapper.writeValueAsString(payload);
    }

    private HttpResponse<String> sendRequest(HttpClient client,
                                             URI requestUri,
                                             String apiKey,
                                             String requestBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(properties.getTimeout())
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    Optional<String> parseProviderResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return Optional.empty();
        }

        String trimmedBody = responseBody.trim();
        try {
            if (trimmedBody.startsWith("data:")) {
                return parseServerSentEvents(trimmedBody);
            }

            JsonNode root = objectMapper.readTree(trimmedBody);
            return extractContent(root);
        } catch (Exception ex) {
            log.warn(
                    "Admin AI provider response could not be parsed: {}. bodyPreview={}",
                    ex.getMessage(),
                    preview(responseBody)
            );
            return Optional.empty();
        }
    }

    private Optional<String> parseServerSentEvents(String responseBody) {
        StringBuilder content = new StringBuilder();
        for (String line : responseBody.split("\\R")) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("data:")) {
                continue;
            }

            String payload = trimmedLine.substring("data:".length()).trim();
            if (payload.isBlank() || "[DONE]".equals(payload)) {
                continue;
            }

            JsonNode root = objectMapper.readTree(payload);
            extractContent(root).ifPresent(content::append);
        }

        String value = content.toString().trim();
        return value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    private Optional<String> extractContent(JsonNode root) {
        JsonNode choice = root.path("choices").path(0);
        String messageContent = choice.path("message").path("content").asText();
        if (messageContent != null && !messageContent.isBlank()) {
            log.info("Admin AI provider content preview={}", preview(messageContent));
            return Optional.of(messageContent);
        }

        String deltaContent = choice.path("delta").path("content").asText();
        if (deltaContent != null && !deltaContent.isBlank()) {
            log.info("Admin AI provider delta content preview={}", preview(deltaContent));
            return Optional.of(deltaContent);
        }

        String textContent = choice.path("text").asText();
        if (textContent != null && !textContent.isBlank()) {
            log.info("Admin AI provider text content preview={}", preview(textContent));
        }
        return textContent == null || textContent.isBlank() ? Optional.empty() : Optional.of(textContent);
    }

    private URI resolveChatCompletionsUri() {
        String baseUrl = resolveSetting(properties.getBaseUrl(), "AI_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            return URI.create("https://9routerhelios.duckdns.org/v1/chat/completions");
        }

        String normalized = baseUrl.trim();
        if (normalized.endsWith("/chat/completions")) {
            return URI.create(normalized);
        }

        if (normalized.endsWith("/")) {
            return URI.create(normalized + "chat/completions");
        }

        return URI.create(normalized + "/chat/completions");
    }

    private boolean resolveEnabled() {
        if (properties.isEnabled()) {
            return true;
        }
        String envEnabled = resolveSetting(null, "AI_ENABLED");
        return Boolean.parseBoolean(envEnabled);
    }

    private String resolveSetting(String propertyValue, String envKey) {
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String systemValue = System.getenv(envKey);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        return localEnv().get(envKey);
    }

    private Map<String, String> localEnv() {
        if (localEnvCache != null) {
            return localEnvCache;
        }

        Map<String, String> values = new HashMap<>();
        for (Path path : List.of(
                Path.of(".env"),
                Path.of("logicticssystemBE", ".env"),
                Path.of("..", "logicticssystemBE", ".env")
        )) {
            if (!Files.isRegularFile(path)) {
                continue;
            }

            try {
                for (String line : Files.readAllLines(path)) {
                    int separator = line.indexOf('=');
                    if (separator <= 0 || line.startsWith("#")) {
                        continue;
                    }
                    values.putIfAbsent(
                            line.substring(0, separator).trim(),
                            line.substring(separator + 1).trim()
                    );
                }
                break;
            } catch (Exception ex) {
                log.warn("Unable to read local AI environment file {}: {}", path, ex.getMessage());
            }
        }

        localEnvCache = values;
        return localEnvCache;
    }

    private String preview(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String compact = value.replaceAll("\\s+", " ").trim();
        return compact.length() <= 300 ? compact : compact.substring(0, 300);
    }
}
