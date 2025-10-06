package com.example.backend.web;

import com.example.backend.model.JobDescription;
import com.example.backend.repo.JobRepository;
import com.example.backend.service.JDParserService;
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
 * CRUD endpoints for job descriptions including raw text POST.
 */
@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "Job description resources")
public class JobController {

    private final JobRepository jobs;
    private final JDParserService jdParserService;
    private final Environment env;

    public JobController(JobRepository jobs, JDParserService jdParserService, Environment env) {
        this.jobs = jobs;
        this.jdParserService = jdParserService;
        this.env = env;
    }

    private boolean noMongoActive() {
        String[] profiles = env.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(p -> p.equalsIgnoreCase("no-mongo"));
    }

    public record CreateJobTextRequest(@NotBlank String title, @NotBlank String text) {}

    // PUBLIC_INTERFACE
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create job from text", description = "Create a job description by submitting raw text.")
    public ApiResponse<JobDescription> createFromText(@RequestBody CreateJobTextRequest req, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        JobDescription jd = jdParserService.persistFromRawText(auth.getName(), req.title(), req.text());
        return ApiResponse.ok(jd);
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(summary = "List jobs", description = "List jobs for current user")
    public ApiResponse<List<JobDescription>> list(Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        return ApiResponse.ok(jobs.findByUserId(auth.getName()));
    }

    // PUBLIC_INTERFACE
    @GetMapping("/{id}")
    @Operation(summary = "Get job", description = "Get a specific job by id")
    public ApiResponse<JobDescription> get(@PathVariable String id, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        JobDescription j = jobs.findById(id).orElseThrow();
        if (!j.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        return ApiResponse.ok(j);
    }

    // PUBLIC_INTERFACE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job", description = "Delete a specific job by id")
    public ApiResponse<Boolean> delete(@PathVariable String id, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        JobDescription j = jobs.findById(id).orElseThrow();
        if (!j.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        jobs.deleteById(id);
        return ApiResponse.ok(true);
    }

    public record PatchJobRequest(@NotBlank String fullText) {}

    // PUBLIC_INTERFACE
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Patch job", description = "Update job description text")
    public ApiResponse<JobDescription> patch(@PathVariable String id, @RequestBody PatchJobRequest req, Authentication auth) {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        JobDescription j = jobs.findById(id).orElseThrow();
        if (!j.getUserId().equals(auth.getName())) throw new RuntimeException("Forbidden");
        j.setFullText(req.fullText());
        return ApiResponse.ok(jobs.save(j));
    }
}
