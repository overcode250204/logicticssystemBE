package com.overcode250204.smartlogicticssystem.ai;

import com.overcode250204.smartlogicticssystem.ai.impl.OpenAiCompatibleLlmClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenAiCompatibleLlmClientTest {

    private OpenAiCompatibleLlmClient client;

    @BeforeEach
    void setUp() {
        client = new OpenAiCompatibleLlmClient(new AdminAiProperties(), new ObjectMapper());
    }

    @Test
    void parseProviderResponseSupportsJsonResponse() {
        String responseBody = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "{\\"answer\\":\\"Vận hành ổn định.\\"}"
                      }
                    }
                  ]
                }
                """;

        Optional<String> result = client.parseProviderResponse(responseBody);

        assertEquals(Optional.of("{\"answer\":\"Vận hành ổn định.\"}"), result);
    }

    @Test
    void parseProviderResponseSupportsServerSentEvents() {
        String responseBody = """
                data: {"choices":[{"delta":{"content":"{\\"answer\\":\\""}}]}
                data: {"choices":[{"delta":{"content":"Vận hành ổn định.\\"}"}}]}
                data: [DONE]
                """;

        Optional<String> result = client.parseProviderResponse(responseBody);

        assertEquals(Optional.of("{\"answer\":\"Vận hành ổn định.\"}"), result);
    }
}
