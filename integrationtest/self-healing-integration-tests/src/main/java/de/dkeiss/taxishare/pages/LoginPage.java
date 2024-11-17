package de.dkeiss.taxishare.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends AbstractPage {

    public static final String URL = "login";
    public static final String URL_2 = "loginV2";
    public static final String URL_3 = "loginV3";
    public static final String URL_4 = "loginV4";

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public String getURL() {
        return URL;
    }

    @FindBy(name = "username")
    private WebElement usernameInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(className = "btn-primary")
    private WebElement submitButton;

    public void login(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        submitButton.click();
    }

}
