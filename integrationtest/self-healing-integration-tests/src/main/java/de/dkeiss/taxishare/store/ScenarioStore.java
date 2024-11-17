package de.dkeiss.taxishare.store;

import de.dkeiss.taxishare.pages.AbstractPage;
import de.dkeiss.taxishare.steps.dto.Reservation;
import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@ScenarioScope
@Getter
@Setter
public class ScenarioStore {

    private HealingApproach healingApproach;
    private AbstractPage page;
    private User user;
    private RequestSpecification request;
    private Response response;
    private String jwtToken;
    private Reservation reservation;
    private InputStream reservationUpdates;

}
