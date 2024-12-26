package com.filesystem.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.filesystem.controller.IUserController;
import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.User;
import com.filesystem.service.IUserService;

@RestController
@RequestMapping("rest/api/user")
public class UserControllerImpl implements IUserController {

    @Autowired
    private IUserService userService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @Override
    @PutMapping("/{id}/username")
    public ResponseEntity<User> updateUsername(@PathVariable Long id, @RequestBody String newUsername) {
        return userService.updateUsername(id, newUsername);
    }

    @Override
    @PostMapping("/{id}/request-password-change")
    public ResponseEntity<Void> requestPasswordChange(@PathVariable Long id) {
        return userService.requestPasswordChange(id);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("\n\nGüncelleme isteği geldi");
        return userService.updateUser(id, user);
    }
}

