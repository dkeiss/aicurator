package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.pages.ReservationPage;
import de.dkeiss.taxishare.steps.dto.Reservation;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReservationSteps extends AbstractSteps {

    @When("searching for a taxi")
    public void searchingForTaxi(List<Map<String, String>> reservationSearches) {
        Map<String, String> entry = reservationSearches.getFirst();
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        reservationPage.setDate(entry.get("date"));
        reservationPage.setDeparture(entry.get("departure"));
        reservationPage.setEarliestStartTime(entry.get("earliestStartTime"));
        reservationPage.setDestination(entry.get("destination"));
        reservationPage.setLatestStartTime(entry.get("latestStartTime"));
        reservationPage.submitSearch();
    }

    @When("a reservation is made")
    public void reservationIsMade() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        reservationPage.submitReservation();
    }

    @When("joining the first reservation")
    public void joiningTheFirstReservation() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        Long id = scenarioStore.getReservation().id();
        reservationPage.clickJoinButton(id);
    }

    @Then("the reserve option is visible")
    public void reserveOptionIsVisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertTrue(reservationPage.reverseButtonIsDisplayed());
    }

    @Then("the reserve option is invisible")
    public void reserveOptionIsInvisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertFalse(reservationPage.reverseButtonIsDisplayed());
    }

    @Then("the reservation is shown in the list")
    public void reservationIsShowInTheList() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getAlertSuccessMessage()).contains("Reservation created successfully");
        String reservationId = reservationPage.getReservationId(0);
        scenarioStore.setReservation(Reservation.builder().id(Long.valueOf(reservationId)).build());
    }

    @Then("taxis are available")
    public void taxisAreAvailable() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getReservations().isEmpty()).isFalse();
    }

    @Then("the join option is invisible")
    public void joinOptionIsInvisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        Long id = scenarioStore.getReservation().id();
        assertFalse(reservationPage.joinButtonExists(id));
    }

    @Then("the current user is added to the participants")
    public void theCurrentUserIsAddedToTheParticipants() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getReservations().get(1)).contains(scenarioStore.getUser().username());
    }

    @Then("a join notification is shown")
    public void joinNotificationIsShown() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getAlertInfoMessage()).contains("There is an update for your reservation");
    }

    @Then("the reservation page is shown")
    public void theReservationPageIsShown() {
        expectPage(ReservationPage.class);
    }

    @Then("the reservation page v2 is shown")
    public void theReservationPageV2IsShown() {
        openRelative(ReservationPage.URL_V2);
        expectPage(ReservationPage.class);
    }

    @Then("no taxis are available")
    public void noTaxisAreAvailable() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getAlertSuccessMessage()).contains("No reservations found");
    }

}