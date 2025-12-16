package com.aicurator.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class AiCuratorClient {
    private static final Logger log = LoggerFactory.getLogger(AiCuratorClient.class);
    private final WebClient webClient;
    private final boolean mockEnabled;
    private final String model;

    public AiCuratorClient(@Value("${openai.base-url:https://api.openai.com/v1}") String baseUrl,
                           @Value("${openai.api-key:}") String apiKey,
                           @Value("${openai.model:gpt-4o-mini}") String model,
                           @Value("${llm.mockEnabled:true}") boolean mockEnabled) {
        this.mockEnabled = mockEnabled || apiKey == null || apiKey.isBlank();
        this.model = model;
        WebClient.Builder builder = WebClient.builder().baseUrl(baseUrl);
        if (!this.mockEnabled) {
            builder.defaultHeader("Authorization", "Bearer " + apiKey);
        }
        this.webClient = builder.build();
    }

    public String healSelector(String failedSelector) {
        if (mockEnabled) {
            log.info("Mock healing for selector={}", failedSelector);
            return failedSelector + "-healed";
        }
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", new Object[]{Map.of("role", "user", "content", "Find a resilient CSS selector alternative for: " + failedSelector)}},
                "temperature", 0
        );
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            Object choice = ((Map<String, Object>) ((java.util.List<?>) response.get("choices")).get(0)).get("message");
            if (choice instanceof Map<?, ?> choiceMap) {
                Object content = choiceMap.get("content");
                return content != null ? content.toString().trim() : failedSelector + "-healed";
            }
        } catch (Exception ex) {
            log.warn("LLM call failed, falling back to mock healing: {}", ex.getMessage());
        }
        return failedSelector + "-healed";
    }
}
