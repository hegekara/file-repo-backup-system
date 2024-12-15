package com.filesystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.filesystem.entities.PasswordChangeRequest;

public interface IPasswordChangeRequestRepository extends JpaRepository<PasswordChangeRequest, Long> {
}

