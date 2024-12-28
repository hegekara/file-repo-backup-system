package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filesystem.controller.INotificationController;
import com.filesystem.entities.Notification;
import com.filesystem.service.INotificationService;

@RestController
@RequestMapping("/rest/api/notification")
public class NotificationControllerImpl implements INotificationController {

    @Autowired
    private INotificationService notificationService;

    @Override
    @GetMapping("/get/{id}")
    public ResponseEntity<List<Notification>> getNotificationById(@PathVariable Long id) {
        System.out.println("bildirim çekme controller");
        return notificationService.getNotificationById(id);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

}
