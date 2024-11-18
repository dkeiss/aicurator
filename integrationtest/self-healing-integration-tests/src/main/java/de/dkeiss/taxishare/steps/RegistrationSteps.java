package de.dkeiss.taxishare.steps;

import com.github.javafaker.Faker;
import de.dkeiss.taxishare.pages.RegistrationPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;


public class RegistrationSteps extends AbstractSteps {

    private final Faker faker = Faker.instance();

    @Given("the registration page is open")
    public void theRegistrationPageIsOpen() {
        openRelative(RegistrationPage.URL);
        expectPage(RegistrationPage.class);
    }

    @Given("the registration page v2 is open")
    public void theRegisterPageV2IsOpen() {
        openRelative(RegistrationPage.URL_V2);
        expectPage(RegistrationPage.class);
    }

    @When("the user registers")
    public void theUserRegisters() {
        RegistrationPage registrationPage = getCurrentPage();
        registrationPage.register(
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.internet().password());
    }

    @Then("the registration is successful")
    public void theRegistrationIsSuccessful() {
        RegistrationPage registrationPage = getCurrentPage();
        String registrationSuccessMessage = registrationPage.getRegistrationSuccessMessage();
        assertEquals("User registered successfully!", registrationSuccessMessage);
    }

}
