package com.example.backend.repo;

import com.example.backend.model.Upload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UploadRepository extends MongoRepository<Upload, String> {
    List<Upload> findByUserId(String userId);
}
