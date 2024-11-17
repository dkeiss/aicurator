package de.dkeiss.aicurator.config;

import java.nio.file.Path;

public record CodeChangeEvent(Path path, String sourceCode) {
}
