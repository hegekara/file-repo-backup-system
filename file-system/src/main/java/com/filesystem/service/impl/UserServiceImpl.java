package com.filesystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<User> createUser(User user) {
        logger.info("Kullanıcı oluşturma işlemi başladı.");
        try {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            User savedUser = userRepository.save(user);
            logger.info("Kullanıcı başarıyla oluşturuldu: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Kullanıcı oluşturulurken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest) {
        logger.info("Giriş işlemi başladı: {}", loginRequest.getUsername());

        try {
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if (user != null) {
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                    logger.info("Giriş başarılı: {}", user.getUsername());
                    return ResponseEntity.ok(token);
                }

                logger.warn("Hatalı şifre ile giriş denemesi: {}", loginRequest.getUsername());
            } else {
                logger.error("Kullanıcı bulunamadı: {}", loginRequest.getUsername());
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Giriş işlemi sırasında hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> updateUsername(Long id, String newUsername) {
        logger.info("Kullanıcı adı güncelleme işlemi başladı. Kullanıcı ID: {}", id);
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                User user = optional.get();
                user.setUsername(newUsername);
                User updatedUser = userRepository.save(user);

                logger.info("Kullanıcı adı başarıyla güncellendi. Kullanıcı ID: {}", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("Kullanıcı bulunamadı: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Kullanıcı adı güncellenirken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> requestPasswordChange(Long userId, String newPassword) {
        logger.info("Şifre değişikliği talebi oluşturma işlemi başladı. Kullanıcı ID: {}", userId);
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                PasswordChangeRequest request = new PasswordChangeRequest();
                request.setUser(user.get());
                request.setNewPassword(passwordEncoder.encode(newPassword));
                passwordChangeRequestRepository.save(request);

                logger.info("Şifre değişikliği talebi başarıyla oluşturuldu. Kullanıcı ID: {}", userId);
                return ResponseEntity.ok().build();
            } else {
                logger.error("Kullanıcı bulunamadı: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Şifre değişikliği talebi oluşturulurken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> approvePasswordChange(Long requestId) {
        logger.info("Şifre değişikliği onaylama işlemi başladı. Talep ID: {}", requestId);
        try {
            Optional<PasswordChangeRequest> requestOptional = passwordChangeRequestRepository.findById(requestId);
            if (requestOptional.isPresent()) {
                PasswordChangeRequest request = requestOptional.get();
                User user = request.getUser();
                user.setPassword(request.getNewPassword());

                userRepository.save(user);
                passwordChangeRequestRepository.delete(request);

                logger.info("Şifre değişikliği onaylandı. Kullanıcı ID: {}", user.getId());
                return ResponseEntity.ok().build();
            } else {
                logger.error("Şifre değişikliği talebi bulunamadı: {}", requestId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Şifre değişikliği onaylanırken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> getUserById(Long id) {
        logger.info("Kullanıcı bilgisi alma işlemi başladı. Kullanıcı ID: {}", id);
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isPresent()) {
                logger.info("Kullanıcı bulundu: {}", id);
                return ResponseEntity.ok(optional.get());
            } else {
                logger.error("Kullanıcı bulunamadı: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Kullanıcı bilgisi alınırken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Tüm kullanıcıları alma işlemi başladı.");
        try {
            List<User> userList = userRepository.findAll();
            logger.info("Toplam {} kullanıcı bulundu.", userList.size());
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            logger.error("Tüm kullanıcılar alınırken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<User> updateUser(Long id, User user) {
        logger.info("Kullanıcı güncelleme işlemi başladı. Kullanıcı ID: {}", id);
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

                logger.info("Kullanıcı başarıyla güncellendi. Kullanıcı ID: {}", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("Kullanıcı bulunamadı: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Kullanıcı güncellenirken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        logger.info("Kullanıcı silme işlemi başladı. Kullanıcı ID: {}", id);
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                logger.info("Kullanıcı başarıyla silindi. Kullanıcı ID: {}", id);
                return ResponseEntity.ok().build();
            } else {
                logger.error("Kullanıcı bulunamadı: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Kullanıcı silinirken hata oluştu: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
