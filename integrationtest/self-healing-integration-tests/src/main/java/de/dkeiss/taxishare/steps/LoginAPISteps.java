package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.steps.dto.LoginJwtResponse;
import de.dkeiss.taxishare.store.User;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginAPISteps extends AbstractSteps {

    private final RegistrationAPISteps registrationAPISteps;

    @Given("a logged-in customer via API")
    public void loggedInCustomerViaAPI() {
        registrationAPISteps.newRegisteredUser();
        loginViaAPI();
    }

    private void loginViaAPI() {
        User user = scenarioStore.getUser();
        Response response = RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();
        LoginJwtResponse loginJwtResponse = response.getBody().as(LoginJwtResponse.class);
        scenarioStore.setJwtToken(loginJwtResponse.accessToken());
    }

}
