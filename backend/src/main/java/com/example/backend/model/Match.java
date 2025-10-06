package com.example.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Match entity linking a resume and job with score.
 */
@Document(collection = "matches")
public class Match {

    @Id
    private String id;

    private String userId;
    private String resumeId;
    private String jobId;
    private double score;
    private String analysisId;

    @CreatedDate
    private Instant createdAt = Instant.now();

    // getters/setters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getResumeId() { return resumeId; }
    public String getJobId() { return jobId; }
    public double getScore() { return score; }
    public String getAnalysisId() { return analysisId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public void setScore(double score) { this.score = score; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
