package com.filesystem.logProcess;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncLogFileMonitor {

    private static final Logger anomalyLogger = LoggerFactory.getLogger("SyncAnomalyLogger");
    private static final String LOG_FILE_PATH = "logs/application.log";
    private long lastPosition = 0;

    @Scheduled(fixedRate = 2 * 60 * 1000) // 30 saniyede bir
    public void monitorLogs() {
        System.out.println("Scheduled procces started - SyncAnomly");
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
        if (logLine.contains("Error")) {
            anomalyLogger.warn("Anomaly detected: {}", logLine);
        }
    }
}