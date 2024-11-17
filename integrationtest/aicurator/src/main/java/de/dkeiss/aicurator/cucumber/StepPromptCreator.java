package de.dkeiss.aicurator.cucumber;

import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.Result;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.dkeiss.aicurator.config.FilesUtil.readFileFromClasspath;
import static de.dkeiss.aicurator.config.FilesUtil.readFileSilent;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Slf4j
public class StepPromptCreator {

    private final String template = readFileFromClasspath("step-prompt.st");
    private final PromptTemplate promptTemplate = new PromptTemplate(template);
    private final MapOutputConverter mapOutputConverter = new MapOutputConverter();

    private Optional<Result> errorResult = Optional.empty();
    private Optional<StackTraceElement> stackTraceElement = Optional.empty();
    private Optional<Path> sourceCodePath = Optional.empty();
    private Optional<String> sourceCode = Optional.empty();

    public void extractErrorResult(Scenario scenario) {
        try {
            Field delegateField = scenario.getClass().getDeclaredField("delegate");
            delegateField.setAccessible(true);
            TestCaseState delegate = (TestCaseState) delegateField.get(scenario);

            Field resultField = delegate.getClass().getDeclaredField("stepResults");
            resultField.setAccessible(true);
            List<Result> results = (List<Result>) resultField.get(delegate);

            errorResult = results.stream()
                    .filter(result -> result.getError() != null)
                    .findFirst();
        } catch (Exception e) {
            log.error("Error while retrieving error result", e);
            errorResult = Optional.empty();
        }
    }

    public void analyseErrorResult() {
        errorResult.ifPresent(result -> stackTraceElement = Arrays.stream(result.getError().getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("de.dkeiss.taxishare.steps"))
                .findFirst());
    }

    public void loadSourceCode() {
        stackTraceElement.ifPresentOrElse(ste -> {
            sourceCodePath = Optional.of(Paths.get("src/main/java/" + ste.getClassName().replace(".", "/") + ".java"));
            sourceCode = Optional.of(readFileSilent(sourceCodePath.get()));
        }, () -> log.warn("No stack trace element found"));
    }

    public Prompt createPrompt() {
        Map<String, Object> model = Map.of(
                "exception", errorResult.map(result -> result.getError().toString()).orElse(""),
                "method", stackTraceElement.map(StackTraceElement::getMethodName).orElse(""),
                "sourceCode", sourceCode.orElse(""),
                "format", new MapOutputConverter().getFormat()
        );

        Message message = promptTemplate.createMessage(model);
        return new Prompt(message);
    }
}
