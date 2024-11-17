package de.dkeiss.taxishare.steps;

import de.dkeiss.taxishare.store.HealingApproach;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Value;

public class SelfHealingSteps extends AbstractSteps {

    @Value("${healing.approach:aicurator}")
    private String healingApproach;

    @Given("self-healing for locators is enabled")
    public void selfHealingForLocatorsIsEnabled() {
        if (!webDriverWrapper.isClosed()) {
            throw new IllegalArgumentException("This step should be called before the WebDriver is loaded");
        }

        switch (healingApproach.toLowerCase()) {
            case "healenium":
                scenarioStore.setHealingApproach(HealingApproach.HEALENIUM);
                break;
            case "aicurator":
                scenarioStore.setHealingApproach(HealingApproach.AICURATOR);
                break;
            default:
                throw new IllegalArgumentException("Unknown healing approach: " + healingApproach);
        }
    }

}
