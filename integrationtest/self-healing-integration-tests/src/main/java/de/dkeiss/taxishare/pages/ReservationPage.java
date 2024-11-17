package de.dkeiss.taxishare.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ReservationPage extends AbstractPage {

    public static final String URL = "reservation";
    public static final String URL_V2 = "reservationV2";

    @FindBy(name = "date")
    private WebElement dateInput;

    @FindBy(name = "departure")
    private WebElement departureInput;

    @FindBy(name = "earliestStartTime")
    private WebElement earliestStartTimeInput;

    @FindBy(name = "destination")
    private WebElement destinationInput;

    @FindBy(name = "latestStartTime")
    private WebElement latestStartTimeInput;

    @FindBy(id = "searchButton")
    private WebElement searchButton;

    @FindBy(id = "reserveButton")
    private WebElement reserveButton;

    @FindBy(className = "alert-success")
    private WebElement alertSuccessDiv;

    @FindBy(className = "alert-info")
    private WebElement alertInfoDiv;

    @FindBy(className = "table-responsive")
    private WebElement reservationDiv;

    public ReservationPage(WebDriver driver) {
        super(driver);
    }

    public void setDate(String date) {
        dateInput.sendKeys(date);
    }

    public void setDeparture(String departure) {
        departureInput.sendKeys(departure);
    }

    public void setEarliestStartTime(String earliestStartTime) {
        earliestStartTimeInput.sendKeys(earliestStartTime);
    }

    public void setDestination(String destination) {
        destinationInput.sendKeys(destination);
    }

    public void setLatestStartTime(String latestStartTime) {
        latestStartTimeInput.sendKeys(latestStartTime);
    }

    public void submitSearch() {
        searchButton.click();
    }

    public void submitReservation() {
        reserveButton.click();
    }

    public boolean reverseButtonIsDisplayed() {
        return reserveButton.isDisplayed();
    }

    public String getAlertSuccessMessage() {
        return alertSuccessDiv.getText();
    }

    public String getAlertInfoMessage() {
        return alertInfoDiv.getText();
    }

    public String getReservationId(int no) {
        WebElement reservationTr = reservationDiv.findElement(By.xpath("(//tr[@data-reservation-id])[" + (no + 1) + "]"));
        return reservationTr.getAttribute("data-reservation-id");
    }

    public List<String> getReservations() {
        return reservationDiv.findElements(By.tagName("tr"))
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    public boolean joinButtonExists(long reservationId) {
        return !reservationDiv.findElements(By.id("joinButton" + reservationId)).isEmpty();
    }

    public void clickJoinButton(long reservationId) {
        reservationDiv.findElement(By.id("joinButton" + reservationId)).click();
    }

    public String getURL() {
        return URL;
    }

}
