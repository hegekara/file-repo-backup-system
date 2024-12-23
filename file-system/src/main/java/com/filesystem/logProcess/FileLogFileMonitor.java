package com.filesystem.logProcess;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileLogFileMonitor {

    private static final Logger anomalyLogger = LoggerFactory.getLogger("FileAnomalyLogger");
    private static final String LOG_FILE_PATH = "logs/application.log";
    private long lastPosition = 0;
    private final Map<String, Integer> fileUploadCounts = new HashMap<>();
    private final Map<String, Integer> fileDownloadCounts = new HashMap<>();

    @Scheduled(fixedRate = 30 * 1000) // 30 saniyede bir
    public void monitorLogs() {
        System.out.println("Scheduled procces started - FileAnomly");
        try (RandomAccessFile reader = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            reader.seek(lastPosition);
            String line;
            while ((line = reader.readLine()) != null) {
                uploadControl(line);
                downloadControl(line);
            }
            lastPosition = reader.getFilePointer();

            fileUploadCounts.clear();
            fileDownloadCounts.clear();
            
        } catch (IOException e) {
            anomalyLogger.error("Error reading log file: {}", e.getMessage());
        }
    }

    private void uploadControl(String logLine) {
        if (logLine.contains("File uploaded successfully")) {
            String userInfo = extractUserInfoFromUploadLog(logLine);
            fileUploadCounts.merge(userInfo, 1, Integer::sum);

            if (fileUploadCounts.get(userInfo) >= 2) {
                anomalyLogger.warn("Anomaly detected: {} uploaded the same file twice.", userInfo);
            }
        }
    }

    private void downloadControl(String logLine) {
        if (logLine.contains("File downloading started")) {
            String userInfo = extractUserInfoFromDownloadLog(logLine);
            fileDownloadCounts.merge(userInfo, 1, Integer::sum);

            fileDownloadCounts.forEach((user, downloadCount) -> {
                if (downloadCount >= 5) {
                    anomalyLogger.warn("Anomaly detected: {} performed {} downloads in the last 30 seconds.", user, downloadCount);
                }
            });
        }
    }

    private String extractUserInfoFromUploadLog(String logLine) {
        int startIndex = logLine.indexOf("to:") + 4;
        return logLine.substring(startIndex).trim();
    }

    private String extractUserInfoFromDownloadLog(String logLine) {
        int startIndex = logLine.indexOf("entity : ") + 9;
        int endIndex = logLine.indexOf(" entity-id");
        if (startIndex >= 0 && endIndex > startIndex) {
            return logLine.substring(startIndex, endIndex).trim();
        }
        return "UnknownUser";
    }
}