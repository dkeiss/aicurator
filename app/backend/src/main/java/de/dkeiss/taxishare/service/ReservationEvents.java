package de.dkeiss.taxishare.service;

import de.dkeiss.taxishare.service.dto.reservation.ReservationDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReservationEvents {

    private final CopyOnWriteArrayList<ReservationEventEmitter> emitters = new CopyOnWriteArrayList<>();

    public void notifyEmitters(ReservationDTO reservationDTO) {
        for (ReservationEventEmitter emitter : emitters) {
            if(Objects.equals(emitter.reservationId, reservationDTO.id())) {
                try {
                    emitter.send(SseEmitter.event().name("reservationJoined").data(reservationDTO));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }

    public SseEmitter addEmitter(Long reservationId) {
        ReservationEventEmitter emitter = new ReservationEventEmitter(reservationId);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    static class ReservationEventEmitter extends SseEmitter {
        final Long reservationId;

        ReservationEventEmitter(Long reservationId) {
            super(0L);
            this.reservationId = reservationId;
        }
    }

}
