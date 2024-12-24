package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filesystem.controller.IAdminController;
import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.Admin;
import com.filesystem.service.IAdminService;

@RestController
@RequestMapping("rest/api/admin")
public class AdminControllerImpl implements IAdminController{

    @Autowired
    private IAdminService adminService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        return adminService.login(loginRequest);
    }

    @Override
    @PostMapping("/approve-password-change/{requestId}")
    public ResponseEntity<Void> approvePasswordChange(@PathVariable Long requestId) {
        return adminService.approvePasswordChange(requestId);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        return adminService.updateAdmin(id, admin);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        return adminService.deleteAdmin(id);
    }
}
