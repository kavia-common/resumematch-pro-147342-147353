package com.example.backend.web;

import com.example.backend.model.Resume;
import com.example.backend.repo.ResumeRepository;
import com.example.backend.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * CRUD endpoints for resumes (limited basic operations).
 */
@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Resume resources")
public class ResumeController {

    private final ResumeRepository resumes;
    private final Environment env;

    public ResumeController(ResumeRepository resumes, Environment env) {
        this.resumes = resumes;
        this.env = env;
    }

    private boolean noMongoActive() {
        String[] profiles = env.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(p -> p.equalsIgnoreCase("no-mongo"));
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(summary = "List resumes", description = "List resumes for current user")
    public ApiResponse<List<Resume>> list(Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        return ApiResponse.ok(resumes.findByUserId(auth.getName()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/{id}")
    @Operation(summary = "Get resume", description = "Get a specific resume by id")
    public ApiResponse<Resume> get(@PathVariable String id, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        Resume r = resumes.findById(id).orElseThrow();
        if (!r.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        return ApiResponse.ok(r);
    }

    // PUBLIC_INTERFACE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume", description = "Delete a specific resume")
    public ApiResponse<Boolean> delete(@PathVariable String id, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        Resume r = resumes.findById(id).orElseThrow();
        if (!r.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        resumes.deleteById(id);
        return ApiResponse.ok(true);
    }

    public record PatchResumeRequest(@NotBlank String fullText) {}

    // PUBLIC_INTERFACE
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Patch resume", description = "Update resume text")
    public ApiResponse<Resume> patch(@PathVariable String id, @RequestBody PatchResumeRequest req, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        Resume r = resumes.findById(id).orElseThrow();
        if (!r.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        r.setFullText(req.fullText());
        return ApiResponse.ok(resumes.save(r));
    }
}
