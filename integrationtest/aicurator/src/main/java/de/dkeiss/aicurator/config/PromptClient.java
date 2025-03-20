package de.dkeiss.aicurator.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PromptClient {

    private final ChatClient chatClient;
    private final PromptCache promptCache;

    public String call(Prompt prompt) {
        if(promptCache.contains(prompt)) {
            return promptCache.get(prompt);
        }

        log.info("Prompt: {}", prompt);
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("Prompt Response: {}", content);

        promptCache.put(prompt, content);
        return content;
    }
}
