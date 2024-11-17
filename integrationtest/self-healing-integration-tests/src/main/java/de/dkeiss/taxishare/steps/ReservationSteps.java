package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.pages.ReservationPage;
import de.dkeiss.taxishare.steps.dto.Reservation;
import de.dkeiss.taxishare.steps.dto.ReservationSearch;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReservationSteps extends AbstractSteps {

    @When("search for a taxi")
    public void searchForTaxi(List<ReservationSearch> reservationSearches) {
        ReservationSearch reservationSearch = reservationSearches.getFirst();
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        reservationPage.setDate(reservationSearch.date());
        reservationPage.setDeparture(reservationSearch.departure());
        reservationPage.setEarliestStartTime(reservationSearch.earliestStartTime());
        reservationPage.setDestination(reservationSearch.destination());
        reservationPage.setLatestStartTime(reservationSearch.latestStartTime());
        reservationPage.submitSearch();
    }


    @When("reservation is done")
    public void reservationIsDone() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        reservationPage.submitReservation();
    }

    @When("join first reservation")
    public void joinFirstReservation() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        Long id = scenarioStore.getReservation().id();
        reservationPage.clickJoinButton(id);
    }

    @Then("reserve is visible")
    public void reserveIsVisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertTrue(reservationPage.reverseButtonIsDisplayed());
    }

    @Then("reserve is invisible")
    public void reserveIsInvisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertFalse(reservationPage.reverseButtonIsDisplayed());
    }

    @Then("reservation is created successfully")
    public void reservationIsCreatedSuccessfully() {
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

    @Then("join is invisible")
    public void joinIsInvisible() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        Long id = scenarioStore.getReservation().id();
        assertFalse(reservationPage.joinButtonExists(id));
    }

    @Then("the current user is added to participants")
    public void theCurrentUserIsAddedToParticipants() {
        ReservationPage reservationPage = expectPage(ReservationPage.class);
        assertThat(reservationPage.getReservations().get(1)).contains(scenarioStore.getUser().username());
    }

    @Then("join notification is shown")
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