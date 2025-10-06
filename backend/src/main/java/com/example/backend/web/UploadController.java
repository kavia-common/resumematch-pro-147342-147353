package com.example.backend.web;

import com.example.backend.model.Upload;
import com.example.backend.repo.UploadRepository;
import com.example.backend.service.JDParserService;
import com.example.backend.service.ResumeParserService;
import com.example.backend.service.StorageService;
import com.example.backend.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

/**
 * Upload endpoints for resumes and job descriptions. Triggers parsing and persistence.
 */
@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "File uploads for resumes and job descriptions")
public class UploadController {

    private final StorageService storage;
    private final UploadRepository uploads;
    private final ResumeParserService resumeParser;
    private final JDParserService jdParser;
    private final Environment env;

    public UploadController(StorageService storage, UploadRepository uploads, ResumeParserService resumeParser, JDParserService jdParser, Environment env) {
        this.storage = storage;
        this.uploads = uploads;
        this.resumeParser = resumeParser;
        this.jdParser = jdParser;
        this.env = env;
    }

    private boolean noMongoActive() {
        String[] profiles = env.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(p -> p.equalsIgnoreCase("no-mongo"));
    }

    // PUBLIC_INTERFACE
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload resume", description = "Upload a resume file to be parsed.")
    public ApiResponse<Object> uploadResume(@RequestPart("file") MultipartFile file, Authentication auth) throws IOException {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        String userEmail = auth.getName();
        StorageService.StoredFile sf = storage.store(file, "resumes");
        Upload up = uploads.save(new Upload(userEmail, sf.filename(), file.getOriginalFilename(), file.getContentType(), "resume", sf.fullPath()));
        var resume = resumeParser.parseAndPersist(userEmail, up.getId(), sf.fullPath());
        return ApiResponse.ok(resume);
    }

    // PUBLIC_INTERFACE
    @PostMapping(value = "/job", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload job description", description = "Upload a job description file to be parsed.")
    public ApiResponse<Object> uploadJob(@RequestPart("file") MultipartFile file, Authentication auth) throws IOException {
        if (noMongoActive()) {
            return ApiResponse.fail("Service Unavailable: persistence disabled under 'no-mongo' profile");
        }
        String userEmail = auth.getName();
        StorageService.StoredFile sf = storage.store(file, "jobs");
        Upload up = uploads.save(new Upload(userEmail, sf.filename(), file.getOriginalFilename(), file.getContentType(), "job", sf.fullPath()));
        var job = jdParser.parseAndPersist(userEmail, up.getId(), sf.fullPath());
        return ApiResponse.ok(job);
    }
}
