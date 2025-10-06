package com.example.backend.repo;

import com.example.backend.model.Analysis;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisRepository extends MongoRepository<Analysis, String> {
}
