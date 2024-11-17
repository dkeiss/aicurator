package de.dkeiss.taxishare.persistence;

import de.dkeiss.taxishare.persistence.model.Reservation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.departure = :departure AND r.destination = :destination AND r.startTime BETWEEN :earliestStartTime AND :latestStartTime")
    List<Reservation> findByDepartureAndDestinationAndEarliestStartTimeAndLatestStartTime(
            @NotBlank @Param("departure") String departure,
            @NotBlank @Param("destination") String destination,
            @NotBlank @Param("earliestStartTime") Date earliestStartTime,
            @NotBlank @Param("latestStartTime") Date latestStartTime
    );

}
