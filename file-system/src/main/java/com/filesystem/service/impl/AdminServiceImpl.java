package com.filesystem.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.filesystem.constants.PasswordStatus;
import com.filesystem.entities.LoginRequest;
import com.filesystem.entities.PasswordChangeRequest;
import com.filesystem.entities.Response;
import com.filesystem.entities.user.Admin;
import com.filesystem.repositories.IAdminRepository;
import com.filesystem.repositories.IPasswordChangeRequestRepository;
import com.filesystem.security.JwtUtil;
import com.filesystem.service.IAdminService;


@Service
public class AdminServiceImpl implements IAdminService{

    @Autowired
    private IAdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IPasswordChangeRequestRepository passwordChangeRequestRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<Admin> createAdmin(Admin admin) {
        logger.info("Admin creation process started.");
        try {
            // Şifreyi şifrele
            String encryptedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(encryptedPassword);
            admin.setStartingDate(LocalDate.now());

            // Kullanıcıyı kaydet
            Admin savedAdmin = adminRepository.save(admin);

            logger.info("Admin successfully created");
            return ResponseEntity.ok(savedAdmin);
        } catch (Exception e) {
            logger.error("Error occurred while creating admin: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Response> login(LoginRequest loginRequest) {
        logger.info("Login process started: {}", loginRequest.getUsername());

        try {
            Admin admin = adminRepository.findByUsername(loginRequest.getUsername());
            if (admin != null) {
                if (passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                    String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());
                    logger.info("Login successful: {}", admin.getUsername());
                    return ResponseEntity.ok().body(new Response(admin, token, "Login succsesful!"));
                }

                logger.warn("Failed login: {}", loginRequest.getUsername());
            } else {
                logger.error("Admin not found: {}", loginRequest.getUsername());
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error occurred during login: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Admin> getAdminById(Long id) {
        logger.info("Admin retrieval process started. User ID: {}", id);
        try {
            Optional<Admin> optional = adminRepository.findById(id);
            if (optional.isPresent()) {
                logger.info("Admin found: {}", id);
                return ResponseEntity.ok(optional.get());
            } else {
                logger.error("Admin not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving admin: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Admin>> getAllAdmins() {
        logger.info("Fetching all admins process started.");
        try {
            List<Admin> adminList = adminRepository.findAll();
            logger.info("Total {} admins found.", adminList.size());
            return ResponseEntity.ok(adminList);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all admins: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Admin> updateAdmin(Long id, Admin admin) {
        logger.info("Admin update process started. Admin ID: {}", id);
        try {
            Optional<Admin> optional = adminRepository.findById(id);
            if (optional.isPresent()) {
                Admin existingAdmin = optional.get();
                existingAdmin.setUsername(admin.getUsername());
                existingAdmin.setFirstName(admin.getFirstName());
                existingAdmin.setLastName(admin.getLastName());
                existingAdmin.setRole(admin.getRole());
                existingAdmin.setPassword(admin.getPassword());

                Admin updatedAdmin = adminRepository.save(existingAdmin);

                logger.info("Admin successfully updated. Admin ID: {}", id);
                return ResponseEntity.ok(updatedAdmin);
            } else {
                logger.error("Admin not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating admin: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteAdmin(Long id) {
        logger.info("Admin deletion process started. Admin ID: {}", id);
        try {
            if (adminRepository.existsById(id)) {
                adminRepository.deleteById(id);
                logger.info("Admin successfully deleted. Admin ID: {}", id);
                return ResponseEntity.ok().build();
            } else {
                logger.error("Admin not found: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting admin: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }


    @Override
    public ResponseEntity<List<PasswordChangeRequest>> getPasswordRequests() {
        List<PasswordChangeRequest> waitingRequests = passwordChangeRequestRepository.findByStatus(PasswordStatus.WAITING);
    
        if (waitingRequests != null && !waitingRequests.isEmpty()) {
            return ResponseEntity.ok().body(waitingRequests);
        }
    
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> approvePasswordChange(Long requestId) {
        logger.info("Password change approval process started. Request ID: {}", requestId);
        try {
            Optional<PasswordChangeRequest> requestOptional = passwordChangeRequestRepository.findById(requestId);
            if (requestOptional.isPresent()) {
                PasswordChangeRequest request = requestOptional.get();

                request.setStatus(PasswordStatus.ACCEPTED);
                passwordChangeRequestRepository.save(request);

                logger.info("Password change approved. User: {}", request.getUser().getUsername());
                return ResponseEntity.ok().build();
            } else {
                logger.error("Password change request not found: {}", requestId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while approving password change: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }


    @Override
    public ResponseEntity<Void> rejectPasswordChange(Long requestId) {
        logger.info("Password change reject process started. Request ID: {}", requestId);
        try {
            Optional<PasswordChangeRequest> requestOptional = passwordChangeRequestRepository.findById(requestId);
            if (requestOptional.isPresent()) {
                PasswordChangeRequest request = requestOptional.get();

                request.setStatus(PasswordStatus.REJECTED);
                passwordChangeRequestRepository.save(request);

                logger.info("Password change rejected. User: {}", request.getUser().getUsername());
                return ResponseEntity.ok().build();
            } else {
                logger.error("Password change request not found: {}", requestId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while rejecting password change: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
