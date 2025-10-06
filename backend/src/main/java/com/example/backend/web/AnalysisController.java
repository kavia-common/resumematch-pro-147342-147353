package com.example.backend.web;

import com.example.backend.model.Analysis;
import com.example.backend.model.Match;
import com.example.backend.repo.MatchRepository;
import com.example.backend.service.AnalysisService;
import com.example.backend.service.MatchingService;
import com.example.backend.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Analysis and matching endpoints.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Analysis", description = "Analysis and matching")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final MatchingService matchingService;
    private final MatchRepository matches;

    public AnalysisController(AnalysisService analysisService, MatchingService matchingService, MatchRepository matches) {
        this.analysisService = analysisService;
        this.matchingService = matchingService;
        this.matches = matches;
    }

    // PUBLIC_INTERFACE
    @PostMapping("/analysis/resume/{resumeId}")
    @Operation(summary = "Analyze resume", description = "Produce ATS-style analysis for a resume id.")
    public ApiResponse<Analysis> analyzeResume(@PathVariable String resumeId) {
        return ApiResponse.ok(analysisService.analyzeResume(resumeId));
    }

    // PUBLIC_INTERFACE
    @PostMapping("/analysis/match")
    @Operation(summary = "Match resume and job", description = "Compute matching score and persist match result.")
    public ApiResponse<Match> match(@RequestParam String resumeId, @RequestParam String jobId, Authentication auth) {
        Analysis a = analysisService.matchResumeToJob(resumeId, jobId);
        Match m = matchingService.createMatch(auth.getName(), a);
        return ApiResponse.ok(m);
    }

    // PUBLIC_INTERFACE
    @GetMapping("/matches")
    @Operation(summary = "List matches", description = "List match results for current user")
    public ApiResponse<List<Match>> list(Authentication auth) {
        return ApiResponse.ok(matches.findByUserId(auth.getName()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/matches/{id}")
    @Operation(summary = "Get match", description = "Get a specific match by id")
    public ApiResponse<Match> get(@PathVariable String id, Authentication auth) {
        Match m = matches.findById(id).orElseThrow();
        if (!m.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        return ApiResponse.ok(m);
    }
}
