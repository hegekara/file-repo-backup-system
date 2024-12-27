package com.filesystem.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    public ResponseEntity<String> uploadFile(String entityType, Long userId, MultipartFile file);

    public ResponseEntity<Resource> downloadFile(String entityType, Long id, String fileName);

    public ResponseEntity<String> deleteFile(String entityType, Long userId, String fileName);

    public ResponseEntity<List<String>> listFiles(String entityType, Long id);

    public ResponseEntity<String> openFile( String entityType, Long id, String fileName);

    public ResponseEntity<String> shareFile(String entityType, Long id, String fileName, Long teamId);

    public ResponseEntity<List<String>> getRepo(String path);
}
