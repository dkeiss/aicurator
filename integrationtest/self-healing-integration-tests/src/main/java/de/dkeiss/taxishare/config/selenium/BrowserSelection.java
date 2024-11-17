package de.dkeiss.taxishare.config.selenium;

import static org.springframework.util.StringUtils.hasText;

public class BrowserSelection {

    public static String getBrowser() {
        String browser = System.getProperty("browser");
        return hasText(browser) ? browser : "chrome";
    }

}
