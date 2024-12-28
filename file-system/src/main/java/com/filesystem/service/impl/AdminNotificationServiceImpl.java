package com.filesystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.filesystem.entities.AdminNotification;
import com.filesystem.repositories.IAdminNotificationRepository;

@Service
public class AdminNotificationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private IAdminNotificationRepository notificationRepository;

    public ResponseEntity<List<AdminNotification>> getNotificationById(Long userId) {
        System.out.println("admin bildirim çekme controller");
        try {
            List<AdminNotification> notificationList = notificationRepository.findByAdminId(userId);

            if (!notificationList.isEmpty()) {
                return ResponseEntity.ok(notificationList);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching notifications for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> deleteNotification(Long id) {
        try {
            Optional<AdminNotification> optional = notificationRepository.findById(id);

            if (optional.isPresent()) {
                notificationRepository.delete(optional.get());
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error deleting notification with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}