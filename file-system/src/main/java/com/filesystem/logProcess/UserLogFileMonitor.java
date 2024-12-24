package com.filesystem.logProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.filesystem.entities.user.User;
import com.filesystem.repositories.IUserRepository;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class UserLogFileMonitor {

    @Autowired
    private IUserRepository userRepository;

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
            }
            lastPosition = reader.getFilePointer();

            // Sayaçları sıfırlama (isteğe bağlı)
            failedLoginCounts.clear();
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

    private void passwordChangeControl(String line) {
        if (line.contains("Password change request")) {
            String id = extractIdFromPasswordLog(line);

            try {
                Optional<User> user = userRepository.findById(Long.parseLong(id));
                if (user.isPresent()) {
                    String username = user.get().getUsername();
                    passwordRequestCounts.merge(username, 1, Integer::sum);

                    if (passwordRequestCounts.get(username) >= 3) {
                        anomalyLogger.warn("Anomaly detected: {} requested password change 3 times.", username);
                    }
                } else {
                    anomalyLogger.warn("Anomaly detected: No user found with ID {} for password change request.", id);
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

    private String extractIdFromPasswordLog(String logLine) {
        int startIndex = logLine.indexOf("ID:") + 3;
        return logLine.substring(startIndex).trim();
    }
}