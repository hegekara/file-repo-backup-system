package com.filesystem.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileController {

    public ResponseEntity<String> uploadFile(String entityType, Long userId, MultipartFile file);

    public ResponseEntity<Resource> downloadFile(String entityType, Long id, String fileName);

    public ResponseEntity<String> deleteFile(String entityType, Long userId, String fileName);
}
