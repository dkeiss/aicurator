package com.aicurator.gateway.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class McpEnvelope {
    @Builder.Default
    private String mcpVersion = "0.1";

    @Builder.Default
    private String conversationId = UUID.randomUUID().toString();

    @NotBlank
    private String sender;

    @NotBlank
    private String type;

    @NotBlank
    private String task;

    @NotNull
    private Map<String, Object> payload;

    private Map<String, Object> trace;
}
