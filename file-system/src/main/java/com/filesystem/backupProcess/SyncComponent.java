package com.filesystem.backupProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class SyncComponent {

    private static final Logger logger = LoggerFactory.getLogger(SyncComponent.class);

    private final Path rootLocation = Paths.get("repos");
    private final Path backupLocation = Paths.get("backups");

    // Önceki dosya durumlarını saklamak için bir harita
    private final Map<Path, FileTime> fileTimestamps = new HashMap<>();

    public SyncComponent() {
        try {
            Files.createDirectories(backupLocation);
            logger.info("Backup directory initialized: {}", backupLocation.toAbsolutePath());
            initializeFileTimestamps();
        } catch (IOException e) {
            logger.error("Error initializing backup directory: {}", e.getMessage());
        }
    }

    private void initializeFileTimestamps() throws IOException {
        Files.walkFileTree(rootLocation, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileTimestamps.put(file, attrs.lastModifiedTime());
                return FileVisitResult.CONTINUE;
            }
        });
        logger.info("File timestamps initialized.");
    }

    @Scheduled(fixedRate = 30000) // Her 30 saniyede bir çalışır
    public void backupChangedFiles() {
        logger.info("Backup process started at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        try {
            Files.walkFileTree(rootLocation, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        FileTime currentModifiedTime = attrs.lastModifiedTime();
                        FileTime previousModifiedTime = fileTimestamps.get(file);

                        // Dosya yeni mi veya değişmiş mi kontrol et
                        if (previousModifiedTime == null || !currentModifiedTime.equals(previousModifiedTime)) {
                            backupFile(file);
                            fileTimestamps.put(file, currentModifiedTime); // Durumu güncelle
                        }
                    } catch (Exception e) {
                        logger.error("Error processing file {}: {}", file, e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    logger.error("Error accessing file {}: {}", file, exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });

            logger.info("Backup process completed at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        } catch (IOException e) {
            logger.error("Error during backup process: {}", e.getMessage());
        }
    }

    private void backupFile(Path file) {
        try {
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
