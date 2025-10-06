package com.example.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Record of an uploaded file (resume or job document).
 */
@Document(collection = "uploads")
public class Upload {

    @Id
    private String id;

    private String userId;
    private String filename;
    private String originalFilename;
    private String contentType;
    private String kind; // resume|job
    private String storagePath;

    @CreatedDate
    private Instant createdAt = Instant.now();

    public Upload() {}

    public Upload(String userId, String filename, String originalFilename, String contentType, String kind, String storagePath) {
        this.userId = userId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.kind = kind;
        this.storagePath = storagePath;
    }

    // getters and setters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFilename() { return filename; }
    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public String getKind() { return kind; }
    public String getStoragePath() { return storagePath; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setKind(String kind) { this.kind = kind; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
