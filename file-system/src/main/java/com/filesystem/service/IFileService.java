package com.filesystem.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    public ResponseEntity<String> uploadFile(String entityType, Long userId, MultipartFile file);

    public ResponseEntity<String> deleteFile(String entityType, Long userId, String fileName);
}
