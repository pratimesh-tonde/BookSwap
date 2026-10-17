package com.bookexchange.controller;

import com.bookexchange.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Book image upload. Files are stored in the "uploads" folder and served from /uploads/{name}. */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw ApiException.badRequest("Please choose an image");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        String ext = dot >= 0 ? original.substring(dot + 1).toLowerCase() : "";
        String type = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(ext) || type == null || !type.startsWith("image/")) {
            throw ApiException.badRequest("Only JPG, PNG, WEBP or GIF images are allowed");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        String name = UUID.randomUUID() + "." + ext;   // random name: no path tricks possible
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return Map.of("url", "/uploads/" + name);
    }
}
