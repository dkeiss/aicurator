package com.aicurator.gateway.web;

import com.aicurator.gateway.model.McpEnvelope;
import com.aicurator.gateway.model.SelfHealingResult;
import com.aicurator.gateway.service.AiCuratorClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class McpController {

    private static final Logger log = LoggerFactory.getLogger(McpController.class);
    private final AiCuratorClient aiCuratorClient;

    public McpController(AiCuratorClient aiCuratorClient) {
        this.aiCuratorClient = aiCuratorClient;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }

    @PostMapping("/mcp")
    public ResponseEntity<?> handleMcp(@Valid @RequestBody McpEnvelope envelope) {
        String conversationId = envelope.getConversationId();
        MDC.put("conversationId", conversationId);
        log.info("Received MCP envelope for task={} type={} conversationId={}", envelope.getTask(), envelope.getType(), conversationId);
        Instant start = Instant.now();

        Map<String, Object> payload = new HashMap<>(envelope.getPayload());

        if ("prepare_run".equalsIgnoreCase(envelope.getTask())) {
            payload.putIfAbsent("message", "Prepared execution; n8n should trigger job next.");
        } else if ("run_tests".equalsIgnoreCase(envelope.getTask())) {
            payload.putIfAbsent("message", "Runner triggered");
            payload.put("status", "STARTED");
        } else if ("heal_locator".equalsIgnoreCase(envelope.getTask())) {
            String failedSelector = String.valueOf(envelope.getPayload().getOrDefault("failedSelector", "unknown"));
            String healedSelector = aiCuratorClient.healSelector(failedSelector);
            SelfHealingResult result = SelfHealingResult.builder()
                    .status("PASS")
                    .healingApplied(true)
                    .failedSelector(failedSelector)
                    .healedSelector(healedSelector)
                    .duration(Duration.between(start, Instant.now()))
                    .artifacts(Map.of("note", "healing simulated"))
                    .build();
            log.info("Healed selector conversationId={} -> {}", conversationId, healedSelector);
            return ResponseEntity.ok(result);
        }

        payload.put("conversationId", conversationId);
        payload.put("durationMs", Duration.between(start, Instant.now()).toMillis());
        return ResponseEntity.ok(payload);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleError(Exception ex) {
        log.error("Failed to process MCP request", ex);
        return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
    }

    @org.springframework.web.bind.annotation.ModelAttribute
    public void clearMdc() {
        MDC.clear();
    }
}
