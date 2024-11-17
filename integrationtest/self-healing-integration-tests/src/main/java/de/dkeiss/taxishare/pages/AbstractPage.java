package de.dkeiss.taxishare.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class AbstractPage {

    protected final WebDriver driver;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
    }

    public void checkPage() {
        checkUrl();
        checkPageState();
    }

    public void checkUrl() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> driver.getCurrentUrl().matches(".*" + getURL() + ".*"));
    }

    public void checkPageState() {
        if (driver instanceof JavascriptExecutor) {
            Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        }
    }

    /*
     * At page instantiation, it's checked that this URL is contained in the open browser URL.
     * You can use regular expressions like "/path/.+/path".
     * Be careful with query params; you have to mask "?" by "\?".
     */
    public abstract String getURL();

}
