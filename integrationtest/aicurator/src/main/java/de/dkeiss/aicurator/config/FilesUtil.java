package de.dkeiss.aicurator.config;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesUtil {

    @SneakyThrows
    public static String readFileFromClasspath(String fileResource) {
        Resource resource = new ClassPathResource(fileResource);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    public static String readFileSilent(Path path) {
        return Files.readString(path);
    }

}
