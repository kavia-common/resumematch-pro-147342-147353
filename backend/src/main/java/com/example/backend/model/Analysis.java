package com.example.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * Analysis result for a resume or match scoring.
 */
@Document(collection = "analyses")
public class Analysis {

    @Id
    private String id;

    private String type; // resume|match
    private String resumeId;
    private String jobId;

    private double score;
    private Map<String, Object> details;

    @CreatedDate
    private Instant createdAt = Instant.now();

    // getters and setters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getResumeId() { return resumeId; }
    public String getJobId() { return jobId; }
    public double getScore() { return score; }
    public Map<String, Object> getDetails() { return details; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setScore(double score) { this.score = score; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
