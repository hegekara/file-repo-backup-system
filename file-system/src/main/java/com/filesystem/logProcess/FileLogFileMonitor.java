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

    @Scheduled(fixedRate = 30 * 1000) // 30 saniyede bir
    public void monitorLogs() {
        System.out.println("Scheduled procces started - FileAnomly");
        try (RandomAccessFile reader = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            reader.seek(lastPosition);
            String line;
            while ((line = reader.readLine()) != null) {
                processLogLine(line);
            }
            lastPosition = reader.getFilePointer();
        } catch (IOException e) {
            anomalyLogger.error("Error reading log file: {}", e.getMessage());
        }
    }

    private void processLogLine(String logLine) {
        if (logLine.contains("File uploaded successfully")) {
            String userInfo = extractUserInfoFromLog(logLine);
            fileUploadCounts.merge(userInfo, 1, Integer::sum);

            if (fileUploadCounts.get(userInfo) >= 2) {
                anomalyLogger.warn("Anomaly detected: {} uploaded the same file twice.", userInfo);
            }
        }
    }

    private String extractUserInfoFromLog(String logLine) {
        int startIndex = logLine.indexOf("to:") + 4;
        return logLine.substring(startIndex).trim();
    }
}