package ru.vsu.cs.projectcars.config;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

@Component
public class MinioLocalMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MinioLocalMigration.class);

    private final MinioClient minioClient;
    private final String bucket;
    private final Path localDir;
    private final boolean enabled;

    public MinioLocalMigration(MinioClient minioClient,
                               @Value("${minio.bucket}") String bucket,
                               @Value("${minio.local-dir}") String localDir,
                               @Value("${minio.migrate-local.enabled:false}") boolean enabled) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.localDir = Paths.get(localDir).toAbsolutePath().normalize();
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;
        if (!Files.isDirectory(localDir)) {
            log.warn("Local image directory does not exist: {}", localDir);
            return;
        }

        int uploaded = 0;
        int skipped = 0;
        try (Stream<Path> files = Files.walk(localDir)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                String objectName = localDir.relativize(file).toString().replace('\\', '/');
                if (!isImage(file)) continue;
                try {
                    minioClient.statObject(StatObjectArgs.builder()
                            .bucket(bucket).object(objectName).build());
                    skipped++;
                    continue;
                } catch (Exception ignored) {
                    // The object is absent and needs to be copied.
                }

                String contentType = Files.probeContentType(file);
                if (contentType == null) contentType = "application/octet-stream";
                try (InputStream input = Files.newInputStream(file)) {
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(input, Files.size(file), -1)
                            .contentType(contentType)
                            .build());
                    uploaded++;
                }
            }
            log.info("MinIO local migration complete: uploaded={}, skipped={}, bucket={}",
                    uploaded, skipped, bucket);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to migrate local images to MinIO", e);
        }
    }

    private boolean isImage(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                || name.endsWith(".gif") || name.endsWith(".webp") || name.endsWith(".bmp");
    }
}