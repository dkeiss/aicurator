package de.dkeiss.aicurator.cucumber;

import de.dkeiss.aicurator.config.CodeChangeEventPublisher;
import de.dkeiss.aicurator.config.PromptClient;
import io.cucumber.java.Scenario;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StepHealer {

    private final PromptClient promptClient;
    private final CodeChangeEventPublisher codeChangeEventPublisher;
    private final ObjectFactory<StepPromptCreator> stepFactory;

    @SneakyThrows
    public void fixFailedStep(Scenario scenario) {

        StepPromptCreator stepPromptCreator = stepFactory.getObject();
        stepPromptCreator.extractErrorResult(scenario);
        stepPromptCreator.analyseErrorResult();
        stepPromptCreator.loadSourceCode();
        Prompt prompt = stepPromptCreator.createPrompt();
        String content = promptClient.call(prompt);

        Map<String, Object> result = stepPromptCreator.getMapOutputConverter().convert(content);
        assert result != null;
        if (stepPromptCreator.getSourceCodePath().isPresent() && result.containsKey("fixedCode")) {
            codeChangeEventPublisher.publishCodeChange(stepPromptCreator.getSourceCodePath().get(), result.get("fixedCode").toString());
        }
    }

}
