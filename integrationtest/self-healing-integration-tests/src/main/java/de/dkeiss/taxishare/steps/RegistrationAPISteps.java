package de.dkeiss.taxishare.steps;

import com.github.javafaker.Faker;
import de.dkeiss.taxishare.store.User;
import io.cucumber.java.en.Given;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;

public class RegistrationAPISteps extends AbstractSteps {

    private final Faker faker = Faker.instance();

    @Given("new registered user")
    public void newRegisteredUser() {
        User user = new User(
                faker.name().username(),
                faker.internet().emailAddress(),
                Set.of("user"),
                faker.internet().password());

        createRequest()
                .body(user)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("message", equalTo("User registered successfully!"))
                .extract()
                .response();

        scenarioStore.setUser(user);
    }

}
