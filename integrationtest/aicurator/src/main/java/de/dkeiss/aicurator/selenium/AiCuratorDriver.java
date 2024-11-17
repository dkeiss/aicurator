package de.dkeiss.aicurator.selenium;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiCuratorDriver implements WebDriver {

    private final WebDriver delegateWebDriver;
    private final LocatorHealer locatorHealer;

    public static WebDriver create(WebDriver delegateWebDriver, LocatorHealer locatorHealer) {
        return new AiCuratorDriver(delegateWebDriver, locatorHealer);
    }

    @Override
    public void get(String url) {
        delegateWebDriver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return delegateWebDriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return delegateWebDriver.getTitle();
    }

    @Override
    public WebElement findElement(By by) {
        try {
            return delegateWebDriver.findElement(by);
        } catch (WebDriverException e) {
            Optional<By> alternativeLocator = locatorHealer.createAlternativeLocator(delegateWebDriver, by, e);
            if (alternativeLocator.isPresent()) {
                return delegateWebDriver.findElement(alternativeLocator.get());
            }
            throw e;
        }
    }

    @Override
    public List<WebElement> findElements(By by) {
        try {
            return delegateWebDriver.findElements(by);
        } catch (WebDriverException e) {
            Optional<By> alternativeLocator = locatorHealer.createAlternativeLocator(delegateWebDriver, by, e);
            if (alternativeLocator.isPresent()) {
                return delegateWebDriver.findElements(alternativeLocator.get());
            }
            throw e;
        }
    }

    @Override
    public String getPageSource() {
        return delegateWebDriver.getPageSource();
    }

    @Override
    public void close() {
        delegateWebDriver.close();
    }

    @Override
    public void quit() {
        delegateWebDriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return delegateWebDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return delegateWebDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return delegateWebDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return delegateWebDriver.navigate();
    }

    @Override
    public Options manage() {
        return delegateWebDriver.manage();
    }
}
