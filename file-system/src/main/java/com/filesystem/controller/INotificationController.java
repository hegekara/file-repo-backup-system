package com.filesystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.filesystem.entities.Notification;

public interface INotificationController {

    public ResponseEntity<List<Notification>> getNotificationById(Long userId);

    public ResponseEntity<Void> deleteNotification(Long id);

}
