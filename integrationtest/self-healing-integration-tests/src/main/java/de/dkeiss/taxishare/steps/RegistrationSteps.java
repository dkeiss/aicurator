package de.dkeiss.taxishare.steps;

import com.github.javafaker.Faker;
import de.dkeiss.taxishare.pages.RegisterPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;


public class RegistrationSteps extends AbstractSteps {

    private final Faker faker = Faker.instance();

    @Given("the register page is open")
    public void theRegisterPageIsOpen() {
        openRelative(RegisterPage.URL);
        expectPage(RegisterPage.class);
    }

    @Given("the register page v2 is open")
    public void theRegisterPageV2IsOpen() {
        openRelative(RegisterPage.URL_V2);
        expectPage(RegisterPage.class);
    }

    @When("the user registers")
    public void theUserRegisters() {
        RegisterPage registerPage = getCurrentPage();
        registerPage.register(
                faker.name().username(),
                faker.internet().emailAddress(),
                faker.internet().password());
    }

    @Then("the registration is successful")
    public void theRegistrationIsSuccessful() {
        RegisterPage registerPage = getCurrentPage();
        String registrationSuccessMessage = registerPage.getRegistrationSuccessMessage();
        assertEquals("User registered successfully!", registrationSuccessMessage);
    }

}
