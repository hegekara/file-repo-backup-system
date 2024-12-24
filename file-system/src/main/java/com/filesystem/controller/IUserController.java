package com.filesystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.User;

public interface IUserController {

    public ResponseEntity<User> createUser(User user);

    public ResponseEntity<Response> login( LoginRequest loginRequest);

    public ResponseEntity<User> getUserById(Long id);

    public ResponseEntity<List<User>> getAllUsers();

    public ResponseEntity<User> updateUser(Long id, User user);
    
    public ResponseEntity<Void> deleteUser(Long id);

    public ResponseEntity<User> updateUsername(Long id, String newUsername);

    public ResponseEntity<Void> requestPasswordChange(Long userId, String newPassword);

}
