package de.dkeiss.taxishare.config.selenium;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import static de.dkeiss.taxishare.config.selenium.BrowserSelection.getBrowser;
import static java.lang.System.getProperty;

/**
 * A useful configuration with some additions to selenium default for all supported and tested browsers.
 */
@Component
@Slf4j
public class WebDriverConfig {

    @Value("${webdriver.maximize:false}")
    private boolean maximizeBrowser;

    WebDriver loadWebdriver() {
        var browser = getBrowser();
        log.info("WebDriver is set to {}", browser);
        DesiredCapabilities extraCapabilities = extraCapabilities(browser);
        if (isRemoteWebdriver()) {
            return loadRemoteWebdriver(extraCapabilities);
        }
        return switch (browser.toLowerCase()) {
            case "firefox" -> loadFirefox(extraCapabilities);
            case "chrome" -> loadChrome(extraCapabilities);
            case "edge" -> loadEdge(extraCapabilities);
            case "safari" -> loadSafari(extraCapabilities);
            default -> throw new IllegalArgumentException("No browser defined! Given browser is: " + browser);
        };
    }

    public ChromeOptions chromeOptions(DesiredCapabilities capabilities) {
        var chromeOptions = new ChromeOptions();
        if (isHeadless()) {
            log.info("Chrome is set to headless mode");
            chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new", "--disable-search-engine-choice-screen");
        }
        chromeOptions.addArguments("--remote-allow-origins=*", "--disable-search-engine-choice-screen");
        capabilities.setCapability("disable-restore-session-state", true);
        capabilities.setCapability("disable-application-cache", true);
        capabilities.setCapability("useAutomationExtension", false);
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    public FirefoxOptions firefoxOptions(DesiredCapabilities capabilities) {
        var firefoxOptions = new FirefoxOptions();
        if (isHeadless()) {
            log.info("Firefox is set to headless mode");
            firefoxOptions.addArguments("-headless");
        }
        capabilities.setCapability("overlappingCheckDisabled", true);
        firefoxOptions.merge(capabilities);
        return firefoxOptions;
    }

    public void afterLoad(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        if (maximizeBrowser) {
            driver.manage().window().maximize();
        }
    }

    private boolean isRemoteWebdriver() {
        return StringUtils.isNotBlank(getGridURL());
    }

    private Optional<AbstractDriverOptions<? extends AbstractDriverOptions>> getBrowserOptionsForRemoteDriver(DesiredCapabilities capabilities) {
        String browser = getBrowser();
        return switch (browser.toLowerCase()) {
            case "chrome" -> Optional.of(chromeOptions(capabilities));
            case "firefox" -> Optional.of(firefoxOptions(capabilities));
            case "edge" -> Optional.of(edgeOptions(capabilities));
            case "safari" -> Optional.of(safariOptions(capabilities));
            default -> Optional.empty();
        };
    }

    private DesiredCapabilities remoteWebDriverOptions(DesiredCapabilities capabilities) {
        var remoteCaps = new DesiredCapabilities();

        var driverOptions = getBrowserOptionsForRemoteDriver(capabilities);
        driverOptions.ifPresentOrElse(remoteCaps::merge, () -> remoteCaps.merge(capabilities));
        return remoteCaps;
    }

    private WebDriver loadRemoteWebdriver(DesiredCapabilities capabilities) {
        var gridURL = getGridURL();
        log.info("Running on: {}", gridURL);
        try {
            return new RemoteWebDriver(new URL(gridURL), remoteWebDriverOptions(capabilities));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL for remote webdriver is malformed!", e);
        }
    }

    private WebDriver loadFirefox(DesiredCapabilities capabilities) {
        var firefoxOptions = firefoxOptions(capabilities);
        var browserPath = getBrowserPath();
        if (StringUtils.isNotBlank(browserPath)) {
            log.info("Load portable firefox instance from '{}'", browserPath);
            firefoxOptions.setBinary(browserPath);
        }
        return new FirefoxDriver(firefoxOptions);
    }

    private WebDriver loadChrome(DesiredCapabilities capabilities) {
        var chromeOptions = chromeOptions(capabilities);
        var browserPath = getBrowserPath();
        if (StringUtils.isNotBlank(browserPath)) {
            log.info("Load portable chrome instance from '{}'", browserPath);
            chromeOptions.setBinary(browserPath);
        }
        return new ChromeDriver(chromeOptions);
    }

    private EdgeOptions edgeOptions(DesiredCapabilities capabilities) {
        var edgeOptions = new EdgeOptions();
        if (isHeadless()) {
            log.warn("No headless mode for edge available!");
        }
        edgeOptions.merge(capabilities);
        return edgeOptions;
    }

    private WebDriver loadEdge(DesiredCapabilities capabilities) {
        var edgeOptions = edgeOptions(capabilities);
        var browserPath = getBrowserPath();
        if (StringUtils.isNotBlank(browserPath)) {
            throw new IllegalArgumentException("Can't use 'browserPath' for edge browser. Portable is not supported!");
        }
        return new EdgeDriver(edgeOptions);
    }

    private SafariOptions safariOptions(DesiredCapabilities capabilities) {
        var safariOptions = new SafariOptions();
        if (isHeadless()) {
            log.warn("No headless mode for Safari available!");
        }
        safariOptions.merge(capabilities);
        return safariOptions;
    }

    private WebDriver loadSafari(DesiredCapabilities capabilities) {
        var safariOptions = safariOptions(capabilities);
        var browserPath = getBrowserPath();
        if (StringUtils.isNotBlank(browserPath)) {
            throw new IllegalArgumentException("Can't use 'browserPath' for Safari. Portable is not supported!");
        }
        return new SafariDriver(safariOptions);
    }

    private boolean isHeadless() {
        // set to headless manually
        if (StringUtils.isNotBlank(getProperty("headless"))) {
            var headless = Boolean.parseBoolean(getProperty("headless"));
            log.info("Test execution is set to headless={}!", headless);
            return headless;
        }
        // headless detection
        if (GraphicsEnvironment.isHeadless()) {
            log.info("Headless execution detected! You can override this with \"headless=false\".");
            return true;
        } else {
            log.info("Execution with graphical user interface detected! You can override this with \"headless=true\".");
            return false;
        }
    }

    /**
     * Path for portable browser
     *
     * @return path
     */
    private String getBrowserPath() {
        return getProperty("browser.path");
    }

    private String getGridURL() {
        return getProperty("gridURL");
    }

    private DesiredCapabilities extraCapabilities(String browser) {
        return new DesiredCapabilities();
    }

}
