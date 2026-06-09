package ru.vsu.cs.projectcars.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.projectcars.model.CarImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public CarImage saveImage(Integer carId, MultipartFile file, int sortOrder) {
        if (file.isEmpty()) return null;
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files allowed, got: " + contentType);
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
            if (ext.contains("/") || ext.contains("\\") || ext.length() > 10) ext = "";
        }
        String filename = UUID.randomUUID() + ext;
        Path carDir = uploadPath.resolve(String.valueOf(carId));
        try {
            Files.createDirectories(carDir);
            Path target = carDir.resolve(filename).normalize();
            if (!target.startsWith(uploadPath)) {
                throw new SecurityException("Path traversal detected");
            }
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        String webPath = "/uploads/" + carId + "/" + filename;
        CarImage img = new CarImage();
        img.setFilePath(webPath);
        img.setSortOrder(sortOrder);
        img.setPrimary(sortOrder == 0);
        return img;
    }

    public void deleteImage(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        Path resolved = uploadPath.resolve(filePath.startsWith("/uploads/") ? filePath.substring(9) : filePath).normalize();
        if (!resolved.startsWith(uploadPath)) return;
        try {
            Files.deleteIfExists(resolved);
        } catch (IOException ignored) {}
    }

    public void deleteImages(List<CarImage> images) {
        if (images == null) return;
        images.forEach(img -> deleteImage(img.getFilePath()));
    }
}
