package de.dkeiss.aicurator.selenium;

import de.dkeiss.aicurator.config.FilesUtil;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static de.dkeiss.aicurator.config.FilesUtil.readFileFromClasspath;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
public class LocatorPromptCreator {

    private final String template = readFileFromClasspath("locator-prompt-large.st");
    private final PromptTemplate promptTemplate = new PromptTemplate(template);
    private final MapOutputConverter mapOutputConverter = new MapOutputConverter();

    private Exception exception;
    private String seleniumExceptionMessage;

    @Value("${aiCurator.pageNamePattern:Page}")
    private String pageNamePattern;
    private Optional<StackTraceElement> pageStackTraceElement = Optional.empty();
    private Optional<Path> pageSourceCodePath = Optional.empty();
    private Optional<String> sourceCodePage = Optional.empty();

    @Value("${aiCurator.stepNamePattern:Step}")
    private String stepNamePattern;
    private Optional<StackTraceElement> stepStackTraceElement = Optional.empty();
    private Optional<Path> stepSourceCodePath = Optional.empty();
    private Optional<String> sourceCodeStep = Optional.empty();

    private String htmlBody;

    @Setter
    private By brokenLocator;

    public void analyseException(Exception exception) {
        this.exception = exception;
        this.seleniumExceptionMessage = extractFirstLine(exception.getMessage());
        this.pageStackTraceElement = getStackTraceElementFor(exception, pageNamePattern);
        this.stepStackTraceElement = getStackTraceElementFor(exception, stepNamePattern);
    }

    public void loadPageSourceCode() {
        this.pageSourceCodePath = getSourceCodePath(pageStackTraceElement);
        this.sourceCodePage = pageSourceCodePath.map(FilesUtil::readFileSilent);
    }

    public void loadStepSourceCode() {
        this.stepSourceCodePath = getSourceCodePath(stepStackTraceElement);
        this.sourceCodeStep = stepSourceCodePath.map(FilesUtil::readFileSilent);
    }

    public void extractHtmlBody(WebDriver delegateWebDriver) {
        Document document = Jsoup.parse(delegateWebDriver.getPageSource());
        this.htmlBody = document.body().toString();
    }

    public Prompt createPrompt() {
        Map<String, Object> model = Map.of(
                "seleniumExceptionMessage", seleniumExceptionMessage,
                "pageSourceCode", sourceCodePage.orElse(""),
                "stepsSourceCode", sourceCodeStep.orElse(""),
                "callMethodPage", pageStackTraceElement.map(StackTraceElement::getMethodName).orElse(""),
                "callStepMethod", stepStackTraceElement.map(StackTraceElement::getMethodName).orElse(""),
                "locator", brokenLocator.toString(),
                "htmlBody", htmlBody,
                "format", mapOutputConverter.getFormat()
        );

        Message message = promptTemplate.createMessage(model);
        return new Prompt(message);
    }

    private Optional<Path> getSourceCodePath(Optional<StackTraceElement> pageStackTraceElement) {
        return pageStackTraceElement.map(StackTraceElement::getClassName)
                .map(className -> {
                    String path = "src/main/java/" + className.replace(".", "/") + ".java";
                    return Paths.get(path);
                });
    }

    private Optional<StackTraceElement> getStackTraceElementFor(Exception exception, String namePattern) {
        return Arrays.stream(exception.getStackTrace())
                .filter(stackTraceElement -> stackTraceElement.getClassName().contains(namePattern))
                .findFirst();
    }

    private String extractFirstLine(String message) {
        return message.split("\n")[0];
    }

}
