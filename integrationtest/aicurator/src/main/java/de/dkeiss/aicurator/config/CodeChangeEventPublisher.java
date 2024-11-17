package de.dkeiss.aicurator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CodeChangeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCodeChange(Path path, String sourceCode) {
        CodeChangeEvent event = new CodeChangeEvent(path, sourceCode);
        eventPublisher.publishEvent(event);
    }

}
