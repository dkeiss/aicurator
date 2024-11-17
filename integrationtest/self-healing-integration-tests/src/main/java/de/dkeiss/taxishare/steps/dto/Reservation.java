package de.dkeiss.taxishare.steps.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Reservation(Long id,
                          String initiator,
                          String date,
                          String departure,
                          String destination,
                          String startTime,
                          BigDecimal price,
                          List<String> participants,
                          String message) {
}