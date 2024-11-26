package de.dkeiss.taxishare.controller;

import de.dkeiss.taxishare.config.Base64PropertyEditor;
import de.dkeiss.taxishare.service.ReservationService;
import de.dkeiss.taxishare.service.dto.reservation.ReservationDTO;
import de.dkeiss.taxishare.service.dto.reservation.ReservationSearchDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationDTO> searchReservations(Principal principal,
                                                   @RequestParam String date,
                                                   @RequestParam String departure,
                                                   @RequestParam String destination,
                                                   @RequestParam String earliestStartTime,
                                                   @RequestParam String latestStartTime) {
        return reservationService.searchForReservations(new ReservationSearchDTO(principal.getName(), LocalDate.parse(date), departure, destination, LocalTime.parse(earliestStartTime), LocalTime.parse(latestStartTime)));
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new Base64PropertyEditor());
    }

    @PostMapping
    public ReservationDTO createReservation(Principal principal, @Valid @RequestBody ReservationDTO reservationDTO) {
        return reservationService.initialReservation(principal.getName(), reservationDTO);
    }

    @DeleteMapping
    public void deleteReservation(Principal principal) {
        reservationService.deleteAllReservations(principal.getName());
    }

    @PutMapping("{reservationId}/join")
    public ReservationDTO joinReservation(Principal principal, @PathVariable Long reservationId) {
        return reservationService.joinReservation(principal.getName(), reservationId);
    }

    @GetMapping(value = "{reservationId}/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter joined(Principal principal, @PathVariable Long reservationId) {
        return reservationService.joined(principal.getName(), reservationId);
    }

}
