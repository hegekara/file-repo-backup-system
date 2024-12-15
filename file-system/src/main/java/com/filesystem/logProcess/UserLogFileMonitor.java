package com.filesystem.logProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserLogFileMonitor {

    private static final Logger anomalyLogger = LoggerFactory.getLogger("AnomalyLogger");

    private static final String LOG_FILE_PATH = "logs/application.log";

    @Scheduled(fixedRate = 60 * 1000) // 60 saniyede bir
    public void monitorLogs() {
        System.out.println("Zamanlanmış görev başladı.");
        Map<String, Integer> failedLoginCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Hatalı şifre ile giriş denemesi")) {
                    String username = extractUsernameFromLog(line);
                    failedLoginCounts.merge(username, 1, Integer::sum);

                    if (failedLoginCounts.get(username) >= 3) {
                        anomalyLogger.warn("Anomali saptandı: {} kullanıcısı 3 kere hatalı giriş yaptı.", username);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractUsernameFromLog(String logLine) {
        int startIndex = logLine.indexOf("denemesi:") + 9;
        return logLine.substring(startIndex).trim();
    }
}
