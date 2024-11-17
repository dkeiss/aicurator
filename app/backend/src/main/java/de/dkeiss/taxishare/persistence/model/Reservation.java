package de.dkeiss.taxishare.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue
    private Long reservationId;

    @ManyToOne
    @JoinColumn
    private User initiator;

    @NotNull
    private BigDecimal initialPrice;

    private BigDecimal reducedPrice;

    @ManyToMany
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "reservationId"),
            inverseJoinColumns = @JoinColumn(name = "userId")
    )
    private List<User> participants;

    @NotNull
    private String departure;

    @NotNull
    private String destination;

    @NotNull
    private Date startTime;

    @NotNull
    private Date creationDate;

    private Date modificationDate;

    public Reservation() {}

    public Reservation(User initiator, String departure, String destination, Date startTime, Date creationDate, Date modificationDate) {
        this.initiator = initiator;
        this.departure = departure;
        this.destination = destination;
        this.startTime = startTime;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }
}