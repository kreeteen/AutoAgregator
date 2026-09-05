package ru.vsu.cs.projectcars.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.projectcars.model.CarImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public FileStorageService(MinioClient minioClient,
                              @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize MinIO bucket: " + bucket, e);
        }
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
        String objectName = carId + "/" + filename;
        try {
            try (InputStream input = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(input, file.getSize(), -1)
                        .contentType(contentType)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image to MinIO", e);
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
        String objectName = filePath.startsWith("/uploads/") ? filePath.substring(9) : filePath;
        if (objectName.contains("..") || objectName.startsWith("/")) return;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception ignored) {}
    }

    public StoredImage getImage(String filePath) {
        String objectName = filePath != null && filePath.startsWith("/uploads/")
                ? filePath.substring(9) : filePath;
        if (objectName == null || objectName.isBlank() || objectName.contains("..") || objectName.startsWith("/")) {
            throw new IllegalArgumentException("Invalid image path");
        }
        try {
            StatObjectResponse metadata = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket).object(objectName).build());
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket).object(objectName).build());
            return new StoredImage(new InputStreamResource(stream), metadata.contentType());
        } catch (Exception e) {
            throw new IllegalArgumentException("Image not found", e);
        }
    }

    public void deleteImages(List<CarImage> images) {
        if (images == null) return;
        images.forEach(img -> deleteImage(img.getFilePath()));
    }

    public record StoredImage(Resource resource, String contentType) {}
}
