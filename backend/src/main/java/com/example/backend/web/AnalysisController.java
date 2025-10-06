package com.example.backend.web;

import com.example.backend.model.Analysis;
import com.example.backend.model.Match;
import com.example.backend.repo.MatchRepository;
import com.example.backend.service.AnalysisService;
import com.example.backend.service.MatchingService;
import com.example.backend.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    private final Environment env;

    public AnalysisController(AnalysisService analysisService, MatchingService matchingService, MatchRepository matches, Environment env) {
        this.analysisService = analysisService;
        this.matchingService = matchingService;
        this.matches = matches;
        this.env = env;
    }

    private boolean noMongoActive() {
        String[] profiles = env.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(p -> p.equalsIgnoreCase("no-mongo"));
    }

    // PUBLIC_INTERFACE
    @PostMapping("/analysis/resume/{resumeId}")
    @Operation(summary = "Analyze resume", description = "Produce ATS-style analysis for a resume id.")
    public ApiResponse<Analysis> analyzeResume(@PathVariable String resumeId) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        return ApiResponse.ok(analysisService.analyzeResume(resumeId));
    }

    // PUBLIC_INTERFACE
    @PostMapping("/analysis/match")
    @Operation(summary = "Match resume and job", description = "Compute matching score and persist match result.")
    public ApiResponse<Match> match(@RequestParam String resumeId, @RequestParam String jobId, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        Analysis a = analysisService.matchResumeToJob(resumeId, jobId);
        Match m = matchingService.createMatch(auth.getName(), a);
        return ApiResponse.ok(m);
    }

    // PUBLIC_INTERFACE
    @GetMapping("/matches")
    @Operation(summary = "List matches", description = "List match results for current user")
    public ApiResponse<List<Match>> list(Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        return ApiResponse.ok(matches.findByUserId(auth.getName()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/matches/{id}")
    @Operation(summary = "Get match", description = "Get a specific match by id")
    public ApiResponse<Match> get(@PathVariable String id, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        Match m = matches.findById(id).orElseThrow();
        if (!m.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        return ApiResponse.ok(m);
    }
}
