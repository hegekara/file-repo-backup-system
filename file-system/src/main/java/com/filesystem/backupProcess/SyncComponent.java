package com.filesystem.backupProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SyncComponent {

    private static final Logger logger = LoggerFactory.getLogger(SyncComponent.class);

    private final Path rootLocation = Paths.get("uploads");
    private final Path backupLocation = Paths.get("backups");

    public SyncComponent() {
        try {
            Files.createDirectories(backupLocation);
            logger.info("Backup directory initialized: {}", backupLocation.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Error initializing backup directory: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 dakikada bir
    public void backupFiles() {
        logger.info("Backup process started at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        try {
            Files.walk(rootLocation)
                .filter(Files::isRegularFile) // Sadece dosyaları yedekle
                .forEach(this::backupFile);

            logger.info("Backup process completed at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        } catch (IOException e) {
            logger.error("Error during backup process: {}", e.getMessage());
        }
    }

    private void backupFile(Path file) {
        try {
            // Yedek dizinine aynı dosya yapısıyla kopyala
            Path relativePath = rootLocation.relativize(file);
            Path backupFilePath = backupLocation.resolve(relativePath);

            Files.createDirectories(backupFilePath.getParent());
            Files.copy(file, backupFilePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File backed up: {} -> {}", file, backupFilePath);
        } catch (IOException e) {
            logger.error("Error backing up file {}: {}", file, e.getMessage());
        }
    }
}
