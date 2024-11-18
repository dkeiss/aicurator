package de.dkeiss.aicurator.selenium;

import de.dkeiss.aicurator.config.CodeChangeEventPublisher;
import de.dkeiss.aicurator.config.PromptClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocatorHealer {

    private final PromptClient promptClient;
    private final CodeChangeEventPublisher codeChangeEventPublisher;
    private final ObjectFactory<LocatorPromptCreator> locatorFactory;

    @SneakyThrows
    public Optional<By> createAlternativeLocator(WebDriver webDriver, By brokenLocator, Exception exception) {

        LocatorPromptCreator locatorPromptCreator = locatorFactory.getObject();
        locatorPromptCreator.analyseException(exception);
        locatorPromptCreator.loadPageSourceCode();
        locatorPromptCreator.loadStepSourceCode();
        locatorPromptCreator.extractHtmlBody(webDriver);
        locatorPromptCreator.setBrokenLocator(brokenLocator);

        Prompt prompt = locatorPromptCreator.createPrompt();
        String content = promptClient.call(prompt);

        Map<String, Object> result = locatorPromptCreator.getMapOutputConverter().convert(content);
        assert result != null;
        if (locatorPromptCreator.getPageSourceCodePath().isPresent() && result.containsKey("locator")) {
            codeChangeEventPublisher.publishCodeChange(locatorPromptCreator.getPageSourceCodePath().get(), result.get("locator").toString());
        }

        if (!result.containsKey("locator")) {
            throw new RuntimeException("No fixed locator found");
        }
        String fixedLocator = result.get("locator").toString();
        return constructLocator(fixedLocator);
    }

    private Optional<By> constructLocator(String fixedLocator) {
        if (fixedLocator.startsWith("By.")) {
            return Optional.of(createBy(fixedLocator));
        }
        return Optional.empty();
    }

    @SneakyThrows
    public By createBy(String byString) {
        String regex = "By\\.(\\w+)\\(\"([^\"]*)\"\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(byString);

        if (matcher.find()) {
            String methodName = matcher.group(1);
            String value = matcher.group(2);

            Method method = By.class.getMethod(methodName, String.class);
            return (By) method.invoke(null, value);
        }

        throw new IllegalArgumentException("Invalid By string: " + byString);
    }

}
