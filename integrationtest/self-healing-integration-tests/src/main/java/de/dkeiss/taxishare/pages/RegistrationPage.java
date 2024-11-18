package de.dkeiss.taxishare.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegistrationPage extends AbstractPage {

    public static final String URL = "register";
    public static final String URL_V2 = "registerV2";

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(className = "btn-primary")
    private WebElement submitButton;

    @FindBy(className = "alert-success")
    private WebElement alertSuccess;

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void register(String username, String email, String password) {
        usernameInput.sendKeys(username);
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        submitButton.click();
    }

    public String getRegistrationSuccessMessage() {
        return alertSuccess.getText();
    }

    public String getURL() {
        return URL;
    }

}
