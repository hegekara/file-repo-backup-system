package com.filesystem.service.impl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filesystem.entities.Team;
import com.filesystem.entities.user.User;
import com.filesystem.repositories.ITeamRepository;
import com.filesystem.repositories.IUserRepository;
import com.filesystem.service.IFileService;

@Service
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final Path rootLocation = Paths.get("repos");

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ITeamRepository teamRepository;



    @Override
    public ResponseEntity<String> uploadFile(String entityType, Long id, MultipartFile file) {
        try {
            System.out.println("upload service başladı");
    
            // Dosya boyutunu al
            long fileSizeInBytes = file.getSize();
            double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
    
            if ("users".equalsIgnoreCase(entityType)) {
                Optional<User> optionalUser = userRepository.findById(id);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
    
                    // Kullanıcının mevcut depolama kullanımını hesapla
                    double currentStorageUsage = calculateUserStorageUsage(user);
                    System.out.println("\n\nmevcut alan: "+ currentStorageUsage);
    
                    if (currentStorageUsage + fileSizeInMB > user.getStorageLimit()) {
                        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                                             .body("Upload failed: Storage limit exceeded. Current usage: " +
                                                   currentStorageUsage + " MB, Limit: " + user.getStorageLimit() + " MB.");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                         .body("User not found with ID: " + id);
                }
            }
    
            // Dosyayı yükle
            Path directory = resolveEntityDirectory(id, entityType);
            Files.createDirectories(directory);
            Path filePath = directory.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
            logger.info("File uploaded successfully to: {}", filePath);
            return ResponseEntity.ok("File uploaded successfully: " + filePath.toString());
        } catch (IOException e) {
            logger.error("Error uploading file for entityType={} and id={}: {}", entityType, id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error uploading file: " + e.getMessage());
        }
    }


    private double calculateUserStorageUsage(User user) {
        Path userDirectory = Paths.get(user.getRepoPath());
        if (!Files.exists(userDirectory)) {
            System.out.println(("Dosya bulunamadı"));
            return 0.0; // Kullanıcının henüz bir dosyası yok
        }
    
        try {
            // Kullanıcının depolama dizinindeki tüm dosyaların boyutunu hesapla
            System.out.println("Dosya bulundu hesaplanıyor");
            return Files.walk(userDirectory)
                        .filter(Files::isRegularFile)
                        .mapToLong(file -> {
                            try {
                                return Files.size(file);
                            } catch (IOException e) {
                                logger.error("Error calculating size for file: {}", file, e);
                                return 0L;
                            }
                        })
                        .sum() / (1024.0 * 1024.0); // Byte -> MB dönüşümü
        } catch (IOException e) {
            logger.error("Error calculating storage usage for user: {}", user.getUsername(), e);
            return 0.0; // Hata durumunda 0 döner
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String entityType, Long id, String fileName) {
        logger.info("File downloading started : entity : {} entity-id : {} file-name: {}", entityType, id, fileName);
        try {
            Path directory = resolveEntityDirectory(id, entityType);
            Path filePath = directory.resolve(fileName);

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                logger.warn("File not found for entityType={}, id={}, fileName={}", entityType, id, fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(null); // Dosya bulunamadı
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("File exists but cannot be read for entityType={}, id={}, fileName={}", entityType, id, fileName);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(null); // Dosya okunamıyor
            }

            logger.info("File downloaded successfully: {}", filePath);
            return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                                .body(resource);
        } catch (IOException e) {
            logger.error("Error downloading file for entityType={}, id={}, fileName={}: {}", entityType, id, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
        }
    }

    @Override
    public ResponseEntity<String> deleteFile(String entityType, Long id, String fileName ) {
        try {
            Path directory = resolveEntityDirectory(id, entityType);
            Path filePath = directory.resolve(fileName);

            Files.deleteIfExists(filePath);
            logger.info("File deleted successfully: {}", filePath);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            logger.error("Error deleting file for entityType={}, id={}, fileName={}: {}", entityType, id, fileName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error deleting file: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<String>> listFiles(String entityType, Long id) {
        try {
            Path directory = resolveEntityDirectory(id, entityType);

            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                logger.warn("Directory not found for entityType={}, id={}", entityType, id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(Collections.emptyList()); // Klasör bulunamadı
            }

            List<String> fileNames = Files.list(directory)
                                        .filter(Files::isRegularFile)
                                        .map(path -> path.getFileName().toString())
                                        .collect(Collectors.toList());

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            logger.error("Error listing files for entityType={}, id={}: {}", entityType, id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.emptyList());
        }
    }

    @Override
    public ResponseEntity<String> openFile(String entityType, Long id, String fileName) {
        
        // Resolve the directory and file path
        Path directory = resolveEntityDirectory(id, entityType);
    
        if (directory == null || !Files.exists(directory) || !Files.isDirectory(directory)) {
            System.out.println("The specified directory does not exist or is invalid: " + directory);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Directory not found");
        }
    
        Path filePath = directory.resolve(fileName).toAbsolutePath(); // Resolve to absolute path
        System.out.println("Absolute Path: " + filePath);
    
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            System.out.println("File not found: " + filePath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("File not found");
        }
    
        File file = filePath.toFile();
    
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop environment is not supported.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Desktop environment is not supported. File path: " + filePath);
        }
    
        Desktop desktop = Desktop.getDesktop();
    
        try {
            desktop.open(file); // Open the file using the default application
            System.out.println("File opened successfully: " + filePath);
            return ResponseEntity.ok("File opened: " + fileName);
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error opening file");
        }
    }

    @Override
    public ResponseEntity<String> shareFile(String entityType, Long id, String fileName, Long teamId) {
        try {
            Path sourceDirectory = resolveEntityDirectory(id, entityType);
            Path targetDirectory = resolveEntityDirectory(teamId, "teams");

            if (sourceDirectory == null || !Files.exists(sourceDirectory) || !Files.isDirectory(sourceDirectory)) {
                System.out.println("The specified directory does not exist or is invalid: " + sourceDirectory);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Source directory not found");
            }

            if (targetDirectory == null || !Files.exists(targetDirectory) || !Files.isDirectory(targetDirectory)) {
                System.out.println("The target directory does not exist or is invalid: " + targetDirectory);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Target directory not found");
            }

            Path sourceFilePath = sourceDirectory.resolve(fileName);
            Path targetFilePath = targetDirectory.resolve(fileName);

            // Dosyayı kopyala
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File shared successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error copying file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error sharing file");
        }
    }
    

    private Path resolveEntityDirectory(Long id, String entityType) {
        if("users".equals(entityType)){
            Optional<User> optional = userRepository.findById(id);
            if(optional.isPresent()){
                String username = optional.get().getUsername();
                Path directory = rootLocation.resolve(entityType + "/" + username);
                System.out.println("\n\npath: " + directory);
                logger.debug("Resolved directory for entityType={}, id={}: {}", entityType, id, directory);
                return directory;
            }
        }
        if("teams".equals(entityType)){
            Optional<Team> optional = teamRepository.findById(id);
            if(optional.isPresent()){
                String teamName = optional.get().getName();
                Path directory = rootLocation.resolve(entityType + "/" + teamName);
                System.out.println("\n\npath: " + directory);
                logger.debug("Resolved directory for entityType={}, id={}: {}", entityType, id, directory);
                return directory;
            }
        }
        return null;
    }

    @Override
    public ResponseEntity<List<String>> getRepo(String path) {
        try {

            Path directory = Paths.get(path);

            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                logger.warn("Directory not found for {}", path);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(Collections.emptyList()); // Klasör bulunamadı
            }

            List<String> fileNames = Files.list(directory)
                                        .filter(Files::isRegularFile)
                                        .map(a -> a.getFileName().toString())
                                        .collect(Collectors.toList());

            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            logger.error("Error listing files for {}", path, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.emptyList());
        }
    }


    @Override
    public ResponseEntity<Resource> downloadLogs(){
    String logsDirectory = "logs";
        Path zipFilePath = Paths.get("logs.zip");

        try {
            FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            File fileToZip = new File(logsDirectory);
            zipFile(fileToZip, fileToZip.getName(), zipOut);

            Resource resource = new UrlResource(zipFilePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("Created ZIP file exists but could not be read: {}", zipFilePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            if (!fileToZip.exists() || !fileToZip.isDirectory()) {
                logger.error("Logs directory does not exist or is not a directory: {}", logsDirectory);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(null);
            }

            logger.info("Logs directory zipped successfully: {}", zipFilePath);
            return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"logs.zip\"")
                            .body(resource);

        } catch (IOException e) {
            logger.error("Error occurred while zipping logs directory", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }   
}