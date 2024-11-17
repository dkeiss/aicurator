package de.dkeiss.taxishare.service;

import de.dkeiss.taxishare.persistence.ReservationRepository;
import de.dkeiss.taxishare.persistence.UserRepository;
import de.dkeiss.taxishare.persistence.model.Reservation;
import de.dkeiss.taxishare.persistence.model.User;
import de.dkeiss.taxishare.service.dto.reservation.ReservationDTO;
import de.dkeiss.taxishare.service.dto.reservation.ReservationSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReservationService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationEvents reservationEvents;

    public ReservationDTO initialReservation(String username, ReservationDTO reservationDTO) {
        User user = getUser(username);

        Reservation reservation = new Reservation();
        reservation.setInitiator(user);
        reservation.setInitialPrice(calculateInitialPrice(reservationDTO));
        reservation.setCreationDate(new Date());
        reservation.setParticipants(List.of(user));
        reservation.setStartTime(Date.from(LocalDateTime.of(reservationDTO.date(), reservationDTO.startTime()).atZone(ZoneId.systemDefault()).toInstant()));
        reservation.setDestination(reservationDTO.destination());
        reservation.setDeparture(reservationDTO.departure());
        reservationRepository.save(reservation);
        return getReservationDTO(reservation);
    }

    public ReservationDTO joinReservation(String username, Long reservationId) {
        User user = getUser(username);
        Reservation reservation = reservationRepository.findById(reservationId).get();
        reservation.getParticipants().add(user);
        reservation.setReducedPrice(reservation.getInitialPrice().multiply(new BigDecimal("0.8"))); // Mocked price reduction
        reservationRepository.save(reservation);

        ReservationDTO reservationDTO = getReservationDTO(reservation);
        reservationEvents.notifyEmitters(reservationDTO);
        return reservationDTO;
    }

    public List<ReservationDTO> searchForReservations(ReservationSearchDTO reservationSearch) {
        return reservationRepository.findByDepartureAndDestinationAndEarliestStartTimeAndLatestStartTime(
                reservationSearch.departure(),
                reservationSearch.destination(),
                getDate(reservationSearch.date(), reservationSearch.earliestStartTime()),
                getDate(reservationSearch.date(), reservationSearch.latestStartTime())
        ).stream().map(this::getReservationDTO).toList();
    }

    private Date getDate(LocalDate date, LocalTime localTime) {
        LocalDateTime dateTime = LocalDateTime.of(date, localTime);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).get();
    }

    private BigDecimal calculateInitialPrice(ReservationDTO reservationDTO) {
        return new BigDecimal("24.50"); // Mocked price calculation
    }

    public SseEmitter joined(String name, Long reservationId) {
        return reservationEvents.addEmitter(reservationId);
    }

    private ReservationDTO getReservationDTO( Reservation reservation) {
        ZonedDateTime zonedDateTime = reservation.getStartTime().toInstant().atZone(ZoneId.systemDefault());
        LocalDate localDate = zonedDateTime.toLocalDate();
        LocalTime localTime = zonedDateTime.toLocalTime();
        return new ReservationDTO(
                reservation.getReservationId(),
                reservation.getInitiator().getUsername(),
                localDate,
                reservation.getDeparture(),
                reservation.getDestination(),
                localTime,
                reservation.getInitialPrice(),
                reservation.getParticipants().stream().map(User::getUsername).toList(),
                null);
    }

    public void deleteAllReservations(String name) {
        reservationRepository.deleteAll();
    }

}
