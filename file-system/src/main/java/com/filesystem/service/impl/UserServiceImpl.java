package com.filesystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.PasswordChangeRequest;
import com.filesystem.entities.User;
import com.filesystem.repositories.IPasswordChangeRequestRepository;
import com.filesystem.repositories.IUserRepository;
import com.filesystem.security.JwtUtil;
import com.filesystem.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IPasswordChangeRequestRepository passwordChangeRequestRepository;

    @Override
    public ResponseEntity<User> createUser(User user) {
        try {
            System.out.println("\n\na");
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if(user != null){
            if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<User> updateUsername(Long id, String newUsername) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setUsername(newUsername);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> requestPasswordChange(Long userId, String newPassword) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                PasswordChangeRequest request = new PasswordChangeRequest();
                request.setUser(user.get());
                request.setNewPassword(passwordEncoder.encode(newPassword));
                passwordChangeRequestRepository.save(request);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> approvePasswordChange(Long requestId) {
        Optional<PasswordChangeRequest> requestOptional = passwordChangeRequestRepository.findById(requestId);
        if (requestOptional.isPresent()) {
            PasswordChangeRequest request = requestOptional.get();
            User user = request.getUser();
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            passwordChangeRequestRepository.delete(request);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<User> getUserById(Long id) {
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                return ResponseEntity.ok(optional.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> userList = userRepository.findAll();
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, User user) {
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                User existingUser = optional.get();
                existingUser.setUsername(user.getUsername());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setRole(user.getRole());
                existingUser.setPassword(user.getPassword());

                User updatedUser = userRepository.save(existingUser);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
