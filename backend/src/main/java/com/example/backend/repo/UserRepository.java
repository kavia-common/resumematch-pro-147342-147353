package com.example.backend.repo;

import com.example.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@org.springframework.context.annotation.Profile("!no-mongo")
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
