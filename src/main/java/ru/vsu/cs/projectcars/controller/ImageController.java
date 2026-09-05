package ru.vsu.cs.projectcars.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.projectcars.service.FileStorageService;

@RestController
@RequestMapping("/uploads")
public class ImageController {

    private final FileStorageService fileStorageService;

    public ImageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{carId}/{filename:.+}")
    public ResponseEntity<?> image(@PathVariable Integer carId, @PathVariable String filename) {
        FileStorageService.StoredImage image = fileStorageService.getImage("/uploads/" + carId + "/" + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .body(image.resource());
    }
}