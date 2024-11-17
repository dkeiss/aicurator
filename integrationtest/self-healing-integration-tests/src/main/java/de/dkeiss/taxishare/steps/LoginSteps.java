package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.pages.LoginPage;
import de.dkeiss.taxishare.store.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
public class LoginSteps extends AbstractSteps {

    private final RegistrationAPISteps registrationAPISteps;

    @Given("the home page is open")
    public void theHomePageIsOpen() {
        open(taxiShareFrontendUrl);
    }

    @Given("the login page is open")
    public void theLoginPageIsOpen() {
        openRelative(LoginPage.URL);
        expectPage(LoginPage.class);
    }

    @Given("the login page v2 is open")
    @Given("the login page with changed username field is open")
    public void theLoginPageWithChangedUsernameFieldIsOpen() {
        openRelative(LoginPage.URL_2);
        expectPage(LoginPage.class);
    }

    @Given("the login page v3 is open")
    @Given("the login page with changed login button and additional button is open")
    public void theLoginPageWithChangedLoginButtonAndAdditionalButtonIsOpen() {
        openRelative(LoginPage.URL_3);
        expectPage(LoginPage.class);
    }

    @Given("the login page v4 is open")
    @Given("the login page with changed login button and additional button without id is open")
    public void theLoginPageWithChangedLoginButtonAndAdditionalButtonWithoutIdIsOpenV() {
        openRelative(LoginPage.URL_4);
        expectPage(LoginPage.class);
    }

    @Given("a logged-in customer")
    public void loggedInCustomer() {
        registrationAPISteps.newRegisteredUser();
        theLoginPageIsOpen();
        theUserLogsIn();
    }

    @Given("clean session")
    public void cleanSession() {
        webDriverWrapper.getDriver().manage().deleteAllCookies();
    }

    @When("the user logs in with invalid credentials")
    public void theUserLogsInWithInvalidCredentials() {
        LoginPage loginPage = getCurrentPage();
        loginPage.login("invalidUser", "invalidPassword");
    }

    @Then("the login page is shown")
    public void theLoginPageIsShown() {
        assertThat(getCurrentPage() instanceof LoginPage).isTrue();
    }

    @When("the user logs in")
    public void theUserLogsIn() {
        LoginPage loginPage = getCurrentPage();
        User currentUser = scenarioStore.getUser();
        loginPage.login(currentUser.username(), currentUser.password());
    }

    @When("the user logs in with email")
    public void theUserLogsInWithEmail() {
        LoginPage loginPage = getCurrentPage();
        User currentUser = scenarioStore.getUser();
        loginPage.login(currentUser.email(), currentUser.password());
    }

    @Then("the user receives the message that the login data is invalid")
    public void theUserReceivesTheMessageThatTheLoginDataIsInvalid() {
    }
}
