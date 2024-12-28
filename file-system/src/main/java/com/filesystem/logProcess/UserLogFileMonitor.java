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

    private final Map<String, Integer> failedLoginCounts = new HashMap<>();
    private final Map<String, Integer> passwordRequestCounts = new HashMap<>();

    @Scheduled(fixedRate = 30 * 1000) // 30 saniyede bir
    public void monitorLogs() {
        System.out.println("Scheduled process started - UserAnomaly");

        try (RandomAccessFile reader = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            reader.seek(lastPosition);
            String line;
            while ((line = reader.readLine()) != null) {
                failedLoginControl(line);
                passwordChangeControl(line);
                successLoginControl(line);
            }
            lastPosition = reader.getFilePointer();

            //failedLoginCounts.clear();
            passwordRequestCounts.clear();

        } catch (IOException e) {
            anomalyLogger.error("Error reading log file: {}", e.getMessage());
        }
    }

    private void failedLoginControl(String line) {
        if (line.contains("Failed login")) {
            String username = extractUsernameFromLog(line);
            failedLoginCounts.merge(username, 1, Integer::sum);

            if (failedLoginCounts.get(username) >= 3) {
                anomalyLogger.warn("Anomaly detected: {} failed login {} times.", username, failedLoginCounts.get(username));
            }
        }
    }

    private void successLoginControl(String line) {
        if (line.contains("successful")) {
            String username = extractUsernameFromLoginLog(line);

            failedLoginCounts.remove(username);
        }
    }

    private void passwordChangeControl(String line) {
        if (line.contains("Password change request process started")) {
            String username = extractIdFromPasswordLog(line);

            try {
                passwordRequestCounts.merge(username, 1, Integer::sum);

                if (passwordRequestCounts.get(username) >= 3) {
                    anomalyLogger.warn("Anomaly detected: {} requested password change {} times.", username, passwordRequestCounts.get(username));
                }
            } catch (NumberFormatException e) {
                anomalyLogger.error("Error parsing user ID from log line: {}", e.getMessage());
            }
        }
    }

    private String extractUsernameFromLog(String logLine) {
        int startIndex = logLine.indexOf("login:") + 6;
        return logLine.substring(startIndex).trim();
    }

    private String extractUsernameFromLoginLog(String logLine) {
        int startIndex = logLine.indexOf("successful:") + 11;
        return logLine.substring(startIndex).trim();
    }

    private String extractIdFromPasswordLog(String logLine) {
        int startIndex = logLine.indexOf("Username:") + 9;
        return logLine.substring(startIndex).trim();
    }
}