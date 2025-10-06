package com.example.backend.repo;

import com.example.backend.model.JobDescription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobRepository extends MongoRepository<JobDescription, String> {
    List<JobDescription> findByUserId(String userId);
}
