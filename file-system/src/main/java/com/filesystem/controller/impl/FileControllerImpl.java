package com.filesystem.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.filesystem.controller.IFileController;
import com.filesystem.service.IFileService;

@RestController
@RequestMapping("/rest/api/files")
public class FileControllerImpl implements IFileController {

    @Autowired
    private IFileService fileService;

    @Override
    @PostMapping("/upload/{entityType}/{id}")
    public ResponseEntity<String> uploadFile(
        @PathVariable String entityType,
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file
    ) {
        return fileService.uploadFile(entityType, id, file );
    }

    @Override
    @DeleteMapping("/delete/{entityType}/{id}/{fileName}")
    public ResponseEntity<String> deleteFile(
        @PathVariable String entityType,
        @PathVariable Long id,
        @PathVariable String fileName
    ) {
        return fileService.deleteFile(entityType, id, fileName);
    }
}
