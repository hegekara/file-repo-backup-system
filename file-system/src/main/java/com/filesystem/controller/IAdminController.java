package com.filesystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.Admin;

public interface IAdminController {

    public ResponseEntity<Admin> createAdmin(Admin admin);

    public ResponseEntity<Response> login( LoginRequest loginRequest);

    public ResponseEntity<Admin> getAdminById(Long id);

    public ResponseEntity<List<Admin>> getAllAdmins();

    public ResponseEntity<Admin> updateAdmin(Long id, Admin admin);
    
    public ResponseEntity<Void> deleteAdmin(Long id);

    public ResponseEntity<Void> approvePasswordChange(Long requestId);
}
