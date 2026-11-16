package com.aicurator.gateway.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;

@Data
@Builder
public class SelfHealingResult {
    private String status;
    @Builder.Default
    private boolean healingApplied = false;
    private String failedSelector;
    private String healedSelector;
    private Duration duration;
    private Map<String, Object> artifacts;
}
