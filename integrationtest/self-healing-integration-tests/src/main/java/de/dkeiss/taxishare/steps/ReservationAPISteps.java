package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.steps.dto.Reservation;
import de.dkeiss.taxishare.steps.dto.ReservationSearch;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@AllArgsConstructor
public class ReservationAPISteps extends AbstractSteps {

    private final LoginAPISteps loginAPISteps;

    @DataTableType
    public Reservation reservationEntry(Map<String, String> entry) {
        return Reservation.builder()
                .date(entry.get("date"))
                .departure(entry.get("departure"))
                .destination(entry.get("destination"))
                .startTime(entry.get("startTime"))
                .build();
    }

    @DataTableType
    public ReservationSearch reservationSearchEntry(Map<String, String> entry) {
        return ReservationSearch.builder()
                .departure(entry.get("departure"))
                .destination(entry.get("destination"))
                .date(entry.get("date"))
                .earliestStartTime(entry.get("earliestStartTime"))
                .latestStartTime(entry.get("latestStartTime"))
                .build();
    }

    @Given("there are available reservations from another user")
    public void availableReservationFromAnotherUser(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            availableReservation(reservation);
        }
    }

    private void availableReservation(Reservation reservation) {
        loginAPISteps.loggedInCustomerViaAPI();
        theUserCreatesNewReservation(Reservation.builder()
                .date(reservation.date())
                .departure(reservation.departure())
                .destination(reservation.destination())
                .startTime(reservation.startTime())
                .build());
        theReservationIsCreatedSuccessfully();
    }

    @Given("reservation from different user")
    public void reservationFromDifferentUser(List<Reservation> reservations) {
        loginAPISteps.loggedInCustomerViaAPI();
        theUserCreatesNewReservation(reservations);
        theReservationIsCreatedSuccessfully();
    }

    @Given("no reservations exist")
    public void noReservationsExist() {
        loginAPISteps.loggedInCustomerViaAPI();
        createRequest()
                .auth().oauth2(scenarioStore.getJwtToken())
                .when()
                .delete("/api/reservations")
                .then()
                .statusCode(200);
    }

    @When("the user creates a new reservation")
    public void theUserCreatesNewReservation(List<Reservation> reservations) {
        theUserCreatesNewReservation(reservations.getFirst());
    }

    private void theUserCreatesNewReservation(Reservation reservation) {
        createRequest()
                .auth().oauth2(scenarioStore.getJwtToken())
                .body(reservation)
                .when()
                .post("/api/reservations");
    }

    @When("the user joins the reservation")
    @When("somebody joins the reservation")
    public void theUserJoinsTheReservation() {
        createRequest()
                .basePath("api/reservations")
                .auth().oauth2(scenarioStore.getJwtToken())
                .contentType(ContentType.JSON)
                .when()
                .pathParam("reservationId", scenarioStore.getReservation().id())
                .post("{reservationId}/join");
    }

    @When("the user searches for reservations")
    public void theUserSearchesForReservations(List<ReservationSearch> reservationSearches) {
        ReservationSearch reservationSearch = reservationSearches.getFirst();
        createRequest()
                .auth().oauth2(scenarioStore.getJwtToken())
                .body(reservationSearch)
                .when()
                .post("/api/reservations/search");

    }

    @When("the user register for updates")
    public void theUserRegisterForUpdates() {
        RestAssured.registerParser("text/event-stream", io.restassured.parsing.Parser.JSON);
        InputStream reservationUpdates = given().log().all().expect().log().all().request()
                .baseUri(taxiShareBackendUrl)
                .basePath("api/reservations")
                .auth().oauth2(scenarioStore.getJwtToken())
                .body(ReservationSearch.builder()
                        .departure("Station A")
                        .destination("Station B")
                        .date("2024-11-07")
                        .earliestStartTime("09:00")
                        .latestStartTime("11:00")
                        .build())
                .when()
                .pathParam("reservationId", scenarioStore.getReservation().id())
                .get("{reservationId}/updates")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .asInputStream();
        scenarioStore.setReservationUpdates(reservationUpdates);
    }

    @Then("the reservation is created successfully")
    public void theReservationIsCreatedSuccessfully() {
        Reservation reservation = scenarioStore.getResponse().then()
                .statusCode(200)
                .extract()
                .response().as(Reservation.class);
        scenarioStore.setReservation(reservation);
    }

    @Then("the user is added to the reservation successfully")
    public void theUserIsAddedToTheReservationSuccessfully() {
        Reservation reservation = scenarioStore.getResponse().then()
                .statusCode(200)
                .extract()
                .response().as(Reservation.class);
        assertThat("The number of participants should be greater than 2",
                reservation.participants().size(),
                greaterThanOrEqualTo(2));
    }

    @Then("the available reservations are returned")
    public void theAvailableReservationsAreReturned() {
        Reservation[] reservationEvents = scenarioStore.getResponse().then()
                .statusCode(200)
                .extract()
                .response().as(Reservation[].class);
        assertThat("The number of participants should be greater than 2",
                reservationEvents.length,
                greaterThanOrEqualTo(1));
    }

    @Then("there are {int} updates available")
    public void thereAreUpdatesAvailable(int updates) throws IOException {
        InputStream reservationUpdates = scenarioStore.getReservationUpdates();
        BufferedReader reader = new BufferedReader(new InputStreamReader(reservationUpdates));
        int count = 0;
        while (reader.readLine() != null) {
            count++;
        }

        assertThat("The number of updates should be " + updates, count, equalTo(updates));
    }
}
