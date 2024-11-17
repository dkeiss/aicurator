package de.dkeiss.taxishare.config.selenium;

import com.epam.healenium.SelfHealingDriver;
import de.dkeiss.aicurator.selenium.AiCuratorDriver;
import de.dkeiss.aicurator.selenium.LocatorHealer;
import de.dkeiss.taxishare.store.HealingApproach;
import de.dkeiss.taxishare.store.ScenarioStore;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class WebDriverWrapper {

    @Setter
    private WebDriver driver;

    private final WebDriverConfig webDriverConfig;
    private final LocatorHealer locatorHealer;
    private final ScenarioStore scenarioStore;

    public WebDriverWrapper(WebDriverConfig webDriverConfig, LocatorHealer locatorHealer, ScenarioStore scenarioStore) {
        this.webDriverConfig = webDriverConfig;
        this.locatorHealer = locatorHealer;
        this.scenarioStore = scenarioStore;
    }


    public WebDriver getDriver() {
        if (isClosed()) {
            loadWebdriver();
        }
        return driver;
    }

    public void loadWebdriver() {
        WebDriver delegateWebDriver = webDriverConfig.loadWebdriver();


        if (HealingApproach.HEALENIUM.equals(scenarioStore.getHealingApproach())) {
            try {
                driver = SelfHealingDriver.create(delegateWebDriver);
            } catch (Exception e) {
                log.error("Healenium is probably not available. Please check if the Healenium server is running.");
                delegateWebDriver.quit();
                throw e;
            }
        } else if (HealingApproach.AICURATOR.equals(scenarioStore.getHealingApproach())) {
            driver = AiCuratorDriver.create(delegateWebDriver, locatorHealer);
        } else {
            driver = delegateWebDriver;
        }

        webDriverConfig.afterLoad(driver);
    }

    public void quit() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        driver = null;
    }

    public boolean isClosed() {
        return driver == null;
    }

    public String createScreenshot(String path) {
        try {
            log.info("Create screenshot to '{}'", path);
            if (driver == null) {
                log.error("Can not create screenshot because webdriver is null!");
                return null;
            }
            var screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            var destFile = new File(path);
            FileUtils.copyFile(screenshot, destFile);
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("Exception at capture screenshot", e);
            return null;
        }
    }

}
