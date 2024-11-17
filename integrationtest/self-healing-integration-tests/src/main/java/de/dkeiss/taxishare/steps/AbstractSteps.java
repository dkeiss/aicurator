package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.config.selenium.WebDriverWrapper;
import de.dkeiss.taxishare.pages.AbstractPage;
import de.dkeiss.taxishare.store.ScenarioStore;
import io.restassured.config.DecoderConfig;
import io.restassured.config.EncoderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.InvocationTargetException;

import static de.dkeiss.taxishare.config.selenium.UrlAppender.appendUrl;
import static io.restassured.RestAssured.given;
import static org.openqa.selenium.support.PageFactory.initElements;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class AbstractSteps {

    @Value("${taxi-share.frontend.url:http://localhost:3000}")
    protected String taxiShareFrontendUrl;

    @Value("${taxi-share.url:http://localhost:8080}")
    protected String taxiShareBackendUrl;

    protected final RestAssuredConfig restAssuredConfig = new RestAssuredConfig()
            .decoderConfig(new DecoderConfig("UTF-8"))
            .encoderConfig(new EncoderConfig("UTF-8", "UTF-8"))
            .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2));

    @Autowired
    protected WebDriverWrapper webDriverWrapper;

    @Autowired
    protected ScenarioStore scenarioStore;

    protected <T extends AbstractPage> T expectPage(Class<T> page) {
        var driver = webDriverWrapper.getDriver();
        T currentPage;
        try {
            var constructor = page.getConstructor(WebDriver.class);
            currentPage = constructor.newInstance(driver);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        initElements(driver, currentPage);
        currentPage.checkPage();
        scenarioStore.setPage(currentPage);
        log.info("Created page object for class {}", currentPage);
        return currentPage;
    }

    @SuppressWarnings("unchecked")
    protected <T extends AbstractPage> T getCurrentPage() {
        return (T) scenarioStore.getPage();
    }

    protected void open(String url) {
        var driver = webDriverWrapper.getDriver();
        driver.get(url);
    }

    protected void openRelative(String url) {
        var driver = webDriverWrapper.getDriver();
        driver.get(appendUrl(taxiShareFrontendUrl, url));
    }

    protected RequestSpecification createRequest() {
        RequestSpecification request = given().log().all().expect().log().all().request();
        request.config(restAssuredConfig);
        request.baseUri(taxiShareBackendUrl);
        request.header("Accept", ContentType.JSON.toString());
        request.header("Content-Type", ContentType.JSON.toString());

        scenarioStore.setRequest(request);
        return request;
    }

}
