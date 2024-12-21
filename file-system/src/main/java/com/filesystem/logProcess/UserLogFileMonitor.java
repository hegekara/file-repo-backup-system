package com.filesystem.logProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserLogFileMonitor {

    private static final Logger anomalyLogger = LoggerFactory.getLogger("UserAnomalyLogger");

    private static final String LOG_FILE_PATH = "logs/application.log";
    private long lastPosition = 0;

    @Scheduled(fixedRate = 30 * 1000) // 30 saniyede bir
    public void monitorLogs() {
        System.out.println("Scheduled procces started - UserAnomly");
        Map<String, Integer> failedLoginCounts = new HashMap<>();

        try (RandomAccessFile reader = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            reader.seek(lastPosition);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Failed login")) {
                    String username = extractUsernameFromLog(line);
                    failedLoginCounts.merge(username, 1, Integer::sum);

                    if (failedLoginCounts.get(username) >= 3) {
                        anomalyLogger.warn("Anomaly detected : {} failed login 3 times", username);
                    }
                }
            }
            lastPosition = reader.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractUsernameFromLog(String logLine) {
        int startIndex = logLine.indexOf("login:") + 6;
        return logLine.substring(startIndex).trim();
    }
}
