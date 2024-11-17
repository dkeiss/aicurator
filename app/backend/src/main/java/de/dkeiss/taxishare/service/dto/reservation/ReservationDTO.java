package de.dkeiss.taxishare.service.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record ReservationDTO(Long id,
                             String initiator,
                             @NotNull LocalDate date,
                             @NotBlank String departure,
                             @NotBlank String destination,
                             @NotNull LocalTime startTime,
                             BigDecimal price,
                             List<String> participants,
                             String message) {
}