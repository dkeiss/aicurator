package de.dkeiss.aicurator.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PromptCache {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File cacheFile = new File("target/promptCache.json");
    private final Map<String, String> cache = loadCache();

    public boolean contains(Prompt prompt) {
        return cache.containsKey(prompt.toString());
    }

    public String get(Prompt prompt) {
        return cache.get(prompt.toString());
    }

    public void put(Prompt prompt, String content) {
        cache.put(prompt.toString(), content);
        saveCache();
    }

    private Map<String, String> loadCache() {
        if (cacheFile.exists()) {
            try {
                return objectMapper.readValue(cacheFile, new TypeReference<>() {
                });
            } catch (IOException e) {
                log.error("Failed to load cache", e);
            }
        }
        return new HashMap<>();
    }

    private void saveCache() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(cacheFile, cache);
        } catch (IOException e) {
            log.error("Failed to save cache", e);
        }
    }
}
