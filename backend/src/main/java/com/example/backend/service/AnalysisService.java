package com.example.backend.service;

import com.example.backend.model.Analysis;
import com.example.backend.model.JobDescription;
import com.example.backend.model.Resume;
import com.example.backend.repo.AnalysisRepository;
import com.example.backend.repo.JobRepository;
import com.example.backend.repo.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Heuristic ATS-style analysis and overlap scoring.
 */
@Service
public class AnalysisService {

    private final ResumeRepository resumes;
    private final JobRepository jobs;
    private final AnalysisRepository analyses;

    public AnalysisService(ResumeRepository resumes, JobRepository jobs, AnalysisRepository analyses) {
        this.resumes = resumes;
        this.jobs = jobs;
        this.analyses = analyses;
    }

    // PUBLIC_INTERFACE
    public Analysis analyzeResume(String resumeId) {
        Resume r = resumes.findById(resumeId).orElseThrow();
        double diversity = Math.min(1.0, (r.getKeywords() != null ? r.getKeywords().size() : 0) / 50.0);
        double skills = Math.min(1.0, (r.getSkills() != null ? r.getSkills().size() : 0) / 20.0);
        double score = 0.4 * diversity + 0.6 * skills;

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("keywordCount", r.getKeywords() == null ? 0 : r.getKeywords().size());
        details.put("skillCount", r.getSkills() == null ? 0 : r.getSkills().size());
        details.put("suggestion", "Add more quantified achievements and align skills with target roles.");

        Analysis a = new Analysis();
        a.setType("resume");
        a.setResumeId(resumeId);
        a.setScore(Math.round(score * 100.0) / 100.0);
        a.setDetails(details);
        return analyses.save(a);
    }

    // PUBLIC_INTERFACE
    public Analysis matchResumeToJob(String resumeId, String jobId) {
        Resume r = resumes.findById(resumeId).orElseThrow();
        JobDescription j = jobs.findById(jobId).orElseThrow();

        Set<String> rSkills = new HashSet<>(Optional.ofNullable(r.getSkills()).orElse(List.of()));
        Set<String> jSkills = new HashSet<>(Optional.ofNullable(j.getSkills()).orElse(List.of()));
        Set<String> rKw = new HashSet<>(Optional.ofNullable(r.getKeywords()).orElse(List.of()));
        Set<String> jKw = new HashSet<>(Optional.ofNullable(j.getKeywords()).orElse(List.of()));

        double skillOverlap = jSkills.isEmpty() ? 0 : (double) intersectionSize(rSkills, jSkills) / jSkills.size();
        double keywordOverlap = jKw.isEmpty() ? 0 : (double) intersectionSize(rKw, jKw) / jKw.size();
        double score = 0.7 * skillOverlap + 0.3 * keywordOverlap;

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("skillOverlap", round2(skillOverlap));
        details.put("keywordOverlap", round2(keywordOverlap));
        details.put("missingSkills", difference(jSkills, rSkills));
        details.put("extraSkills", difference(rSkills, jSkills));

        Analysis a = new Analysis();
        a.setType("match");
        a.setResumeId(resumeId);
        a.setJobId(jobId);
        a.setScore(round2(score));
        a.setDetails(details);
        return analyses.save(a);
    }

    private int intersectionSize(Set<String> a, Set<String> b) {
        Set<String> x = new HashSet<>(a);
        x.retainAll(b);
        return x.size();
    }

    private List<String> difference(Set<String> a, Set<String> b) {
        Set<String> x = new HashSet<>(a);
        x.removeAll(b);
        return new ArrayList<>(x);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
