package de.dkeiss.taxishare.controller;

import de.dkeiss.taxishare.service.ReservationService;
import de.dkeiss.taxishare.service.dto.reservation.ReservationDTO;
import de.dkeiss.taxishare.service.dto.reservation.ReservationSearchDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationDTO createReservation(Principal principal, @Valid @RequestBody ReservationDTO reservationDTO) {
        return reservationService.initialReservation(principal.getName(), reservationDTO);
    }

    @DeleteMapping
    public void deleteReservation(Principal principal) {
        reservationService.deleteAllReservations(principal.getName());
    }

    @PostMapping("search")
    public List<ReservationDTO> searchReservations(Principal principal, @Valid @RequestBody ReservationSearchDTO reservationSearch) {
        return reservationService.searchForReservations(reservationSearch);
    }

    @PostMapping("{reservationId}/join")
    public ReservationDTO joinReservation(Principal principal, @PathVariable Long reservationId) {
        return reservationService.joinReservation(principal.getName(), reservationId);
    }

    @GetMapping(value = "{reservationId}/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter joined(Principal principal, @PathVariable Long reservationId) {
        return reservationService.joined(principal.getName(), reservationId);
    }

}
