package com.filesystem.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filesystem.constants.PasswordStatus;
import com.filesystem.entities.PasswordChangeRequest;

public interface IPasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest, Long> {

    List<PasswordChangeRequest> findByStatus(PasswordStatus status);
}

