package de.dkeiss.taxishare.config.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import lombok.extern.slf4j.Slf4j;

import static io.github.bonigarcia.wdm.config.DriverManagerType.*;

@Slf4j
public class BrowserDriverUpdater {

    /**
     * Here you should be careful that the number of 60 requests per hour in the direction of github is not exceeded.
     * This applies to the driver for firefox and opera.
     * <p>
     * <a href="https://github.com/bonigarcia/webdrivermanager">...</a>
     * <a href="https://developer.github.com/v3/#rate-limiting">...</a>
     */
    public static void updateDriver() {
        String browser = BrowserSelection.getBrowser();
        DriverManagerType driverManagerType = mapToDriverManagerType(browser);
        if (driverManagerType == null) {
            log.info("No driver update available for {} browser", browser);
            return;
        }
        log.info("Update driver for {}", browser);

        WebDriverManager.getInstance(driverManagerType).setup();
        log.info("Updated instrumentalization driver for {}({})", driverManagerType, browser);
    }

    private static DriverManagerType mapToDriverManagerType(String browser) {
        return switch (browser.toLowerCase()) {
            case "firefox" -> FIREFOX;
            case "chrome" -> CHROME;
            case "edge" -> EDGE;
            case "ie", "internetexplorer" -> IEXPLORER;
            case "opera" -> OPERA;
            default -> null;
        };
    }

}
