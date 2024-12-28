package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filesystem.entities.AdminNotification;
import com.filesystem.service.impl.AdminNotificationServiceImpl;

@RestController
@RequestMapping("/rest/api/admin-notification")
public class AdminNotificationControllerImpl {

    @Autowired
    private AdminNotificationServiceImpl notificationService;

    @GetMapping("/get/{id}")
    public ResponseEntity<List<AdminNotification>> getNotificationById(@PathVariable Long id) {
        System.out.println("admin bildirim çekme controller");
        return notificationService.getNotificationById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }

}
