package de.dkeiss.taxishare.steps;

import de.dkeiss.aicurator.cucumber.StepHealer;
import de.dkeiss.taxishare.config.TestConfig;
import de.dkeiss.taxishare.config.selenium.BrowserDriverUpdater;
import de.dkeiss.taxishare.config.selenium.WebDriverWrapper;
import de.dkeiss.taxishare.store.ScenarioStore;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import static de.dkeiss.taxishare.store.HealingApproach.AICURATOR;

@CucumberContextConfiguration
@ContextConfiguration(classes = TestConfig.class)
public class CucumberGlueSteps {

    private final WebDriverWrapper webDriverWrapper;
    private final StepHealer stepHealer;
    private final ScenarioStore scenarioStore;
    private final boolean stepHealingEnabled;

    public CucumberGlueSteps(WebDriverWrapper webDriverWrapper, StepHealer stepHealer, ScenarioStore scenarioStore,
                             @Value("${step.healing.enabled:false}") boolean stepHealingEnabled) {
        this.webDriverWrapper = webDriverWrapper;
        this.stepHealer = stepHealer;
        this.scenarioStore = scenarioStore;
        this.stepHealingEnabled = stepHealingEnabled;
    }

    @BeforeAll
    public static void updateWebdriver() {
        BrowserDriverUpdater.updateDriver();
    }

    @After
    public void quitWebdriver() {
        webDriverWrapper.quit();
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed() && AICURATOR.equals(scenarioStore.getHealingApproach()) && stepHealingEnabled) {
            stepHealer.fixFailedStep(scenario);
        }
    }

}
