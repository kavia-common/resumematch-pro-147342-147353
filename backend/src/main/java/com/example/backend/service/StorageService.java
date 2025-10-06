package com.example.backend.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Local disk storage implementation. Paths are normalized and sandboxed under baseDir.
 */
@Service
public class StorageService {

    private final Path baseDir;

    public StorageService(@Value("${app.storage.dir}") String baseDir) throws IOException {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        Files.createDirectories(this.baseDir);
    }

    // PUBLIC_INTERFACE
    public StoredFile store(MultipartFile file, String kind) throws IOException {
        String ext = StringUtils.hasText(FilenameUtils.getExtension(file.getOriginalFilename()))
                ? "." + FilenameUtils.getExtension(file.getOriginalFilename()) : "";
        String safeName = UUID.randomUUID() + ext;
        Path target = this.baseDir.resolve(kind).resolve(safeName).normalize();
        if (!target.startsWith(this.baseDir)) {
            throw new IOException("Unsafe storage path");
        }
        Files.createDirectories(target.getParent());
        file.transferTo(target);
        return new StoredFile(safeName, target.toString(), file.getOriginalFilename(), file.getContentType());
    }

    // PUBLIC_INTERFACE
    public Resource loadAsResource(String path) {
        return new FileSystemResource(path);
    }

    public record StoredFile(String filename, String fullPath, String originalFilename, String contentType) {}
}
