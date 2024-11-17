package de.dkeiss.taxishare.steps.dto;

import lombok.Builder;

@Builder
public record ReservationSearch(String username,
                                String date,
                                String departure,
                                String destination,
                                String earliestStartTime,
                                String latestStartTime,
                                String message) {
}