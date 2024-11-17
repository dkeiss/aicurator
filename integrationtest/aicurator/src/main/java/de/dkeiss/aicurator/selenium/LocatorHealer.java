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
        if (fixedLocator.contains("name")) {
            return Optional.of(By.name(extractValue("name", fixedLocator)));
        } else if (fixedLocator.contains("id")) {
            return Optional.of(By.id(extractValue("id", fixedLocator)));
        } else if (fixedLocator.contains("xpath")) {
            return Optional.of(By.xpath(extractValue("xpath", fixedLocator)));
        } else if (fixedLocator.contains("cssSelector")) {
            return Optional.of(By.cssSelector(extractValue("cssSelector", fixedLocator)));
        } else if (fixedLocator.contains("linkText")) {
            return Optional.of(By.linkText(extractValue("linkText", fixedLocator)));
        } else if (fixedLocator.contains("partialLinkText")) {
            return Optional.of(By.partialLinkText(extractValue("partialLinkText", fixedLocator)));
        } else if (fixedLocator.contains("tagName")) {
            return Optional.of(By.tagName(extractValue("tagName", fixedLocator)));
        } else if (fixedLocator.contains("className")) {
            return Optional.of(By.className(extractValue("className", fixedLocator)));
        } else if (fixedLocator.contains(".") || fixedLocator.contains("#")) {
            return Optional.of(By.cssSelector(fixedLocator));
        } else if (!fixedLocator.isEmpty()) {
            return Optional.of(By.cssSelector(fixedLocator));
        }
        return Optional.empty();
    }

    private String extractValue(String name, String locator) {
        String regex = name + "\\s*=\\s*\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(locator);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
