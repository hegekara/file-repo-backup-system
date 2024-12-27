package com.filesystem.controller.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        System.out.println("\n\nupload controller başladı");

        return fileService.uploadFile(entityType, id, file);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String entityType, 
            @RequestParam Long id, 
            @RequestParam String fileName) {
        return fileService.downloadFile(entityType, id, fileName);
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

    @Override
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(
        @RequestParam String entityType,
        @RequestParam Long id) {
        System.out.println("Received request to list files for entityType= "+entityType+" and id= "+ id);
    
        if (entityType == null || entityType.isEmpty() || id == null) {
            System.out.println("invalid parameters");
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    
        return fileService.listFiles(entityType, id);
    }

    @Override
    @GetMapping("/open/{entityType}/{id}/{fileName}")
    public ResponseEntity<String> openFile(
        @PathVariable String entityType,
        @PathVariable Long id,
        @PathVariable String fileName
    ){
        return fileService.openFile(entityType, id, fileName);
    }

    @Override
    @PostMapping("/share/{entityType}/{id}/{fileName}/to/{teamId}")
    public ResponseEntity<String> shareFile(
        @PathVariable String entityType,
        @PathVariable Long id,
        @PathVariable String fileName,
        @PathVariable Long teamId) {
        
        return fileService.shareFile(entityType, id, fileName, teamId);
    }
    
    @Override
    @GetMapping("/getRepo")
    public ResponseEntity<List<String>> getRepo(
        @RequestParam String path) {
    
        if (path.equals(null)) {
            System.out.println("invalid parameters");
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    
        return fileService.getRepo(path);
    }

    @Override
    @GetMapping("/download-logs")
    public ResponseEntity<Resource> downloadLogs() {
        return fileService.downloadLogs();
    }
}
