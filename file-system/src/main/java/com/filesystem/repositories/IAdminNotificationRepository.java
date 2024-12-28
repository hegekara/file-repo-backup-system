package com.filesystem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filesystem.entities.AdminNotification;

@Repository
public interface IAdminNotificationRepository extends JpaRepository<AdminNotification, Long> {

    List<AdminNotification> findByAdminId(Long adminId);
}
