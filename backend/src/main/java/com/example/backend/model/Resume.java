package com.example.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Parsed resume entity.
 */
@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;

    private String userId;
    private String uploadId;

    @TextIndexed
    private String fullText;

    private List<String> skills;
    private List<String> keywords;

    @CreatedDate
    private Instant createdAt = Instant.now();

    // getters and setters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getUploadId() { return uploadId; }
    public String getFullText() { return fullText; }
    public List<String> getSkills() { return skills; }
    public List<String> getKeywords() { return keywords; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUploadId(String uploadId) { this.uploadId = uploadId; }
    public void setFullText(String fullText) { this.fullText = fullText; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
