package de.dkeiss.taxishare.config.restassured;

import de.dkeiss.taxishare.store.ScenarioStore;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestAssuredLifecycle {

    @Autowired
    protected ScenarioStore scenarioStore;

    @PostConstruct
    public void beforeStories() {
        RestAssured.filters(List.of(new RequestInteractionFilter(scenarioStore)));
    }

}
