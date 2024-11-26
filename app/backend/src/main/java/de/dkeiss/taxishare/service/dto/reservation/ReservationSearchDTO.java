package de.dkeiss.taxishare.service.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ReservationSearchDTO(String username,
                                   @NotNull LocalDate date,
                                   @NotBlank String departure,
                                   @NotBlank String destination,
                                   @NotNull LocalTime earliestStartTime,
                                   @NotNull LocalTime latestStartTime) {
}