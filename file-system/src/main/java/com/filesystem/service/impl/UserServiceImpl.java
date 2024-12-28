package com.filesystem.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.filesystem.constants.PasswordStatus;
import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.PasswordChangeRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.User;
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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<User> createUser(User user) {
        logger.info("User creation process started.");
        try {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            // Kullanıcıya repoPath belirle
            String repoPath = "repos/users/" + user.getUsername();
            user.setRepoPath(repoPath);

            user.setStorageLimit(50.0);

            // Kullanıcıyı kaydet
            User savedUser = userRepository.save(user);

            // Repo klasörünü oluştur
            Path repoDirectory = Paths.get(repoPath);
            Files.createDirectories(repoDirectory);

            logger.info("User successfully created with repo path: {}", repoPath);
            return ResponseEntity.ok(savedUser);
        } catch (IOException e) {
            logger.error("Error creating directory for user repo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Response> login(LoginRequest loginRequest) {
        logger.info("Login process started: {}", loginRequest.getUsername());

        try {
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if (user != null) {
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                    logger.info("Login successful: {}", user.getUsername());
                    return ResponseEntity.ok().body(new Response(user, token, "Login succsesful!"));
                }

                logger.warn("Failed login: {}", loginRequest.getUsername());
            } else {
                logger.error("User not found: {}", loginRequest.getUsername());
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error occurred during login: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> updateUsername(Long id, String newUsername) {
        logger.info("Username update process started. User ID: {}", id);
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                User user = optional.get();
                user.setUsername(newUsername);
                User updatedUser = userRepository.save(user);

                logger.info("Username successfully updated. User ID: {}", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("User not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating username: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> requestPasswordChange(Long userId) {
        
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                logger.info("Password change request process started. Username: {}", user.get().getUsername());
                Optional<PasswordChangeRequest> reqCheck = passwordChangeRequestRepository.findByUserId(userId);
                if(reqCheck.isPresent()){
                    return ResponseEntity.badRequest().build();
                }
                PasswordChangeRequest request = new PasswordChangeRequest();
                request.setUser(user.get());
                request.setStatus(PasswordStatus.WAITING);
                passwordChangeRequestRepository.save(request);

                logger.info("Password change request successfully created. Username: {}", user.get().getUsername());
                return ResponseEntity.ok().build();
            } else {
                logger.error("User not found: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating password change request: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> getUserById(Long id) {
        logger.info("User retrieval process started. User ID: {}", id);
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                logger.info("User found: {}", id);
                return ResponseEntity.ok(optional.get());
            } else {
                logger.error("User not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users process started.");
        try {
            List<User> userList = userRepository.findAll();
            logger.info("Total {} users found.", userList.size());
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, User user) {
        logger.info("User update process started. User ID: {}", id);
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                User existingUser = optional.get();
                existingUser.setUsername(user.getUsername());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());

                User updatedUser = userRepository.save(existingUser);

                logger.info("User successfully updated. User ID: {}", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("User not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<PasswordChangeRequest> checkPasswordStatus(Long userId) {
        try {
            Optional<PasswordChangeRequest> passwordRequest = passwordChangeRequestRepository.findByUserId(userId);

            if (passwordRequest.isPresent()) {
                return ResponseEntity.ok(passwordRequest.get());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }

    @Override
    public ResponseEntity<Void> changePassword(Long id, String oldPassword, String newPassword) {
        Optional<User> optional = userRepository.findById(id);
    
        if (optional.isPresent()) {

            User user = optional.get();
    
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                Optional<PasswordChangeRequest> request = passwordChangeRequestRepository.findByUserId(id);
                if(request.isPresent()){
                    passwordChangeRequestRepository.delete(request.get());
                }
    
                return ResponseEntity.ok().build();
            } else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
