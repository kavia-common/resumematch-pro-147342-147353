package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entrypoint.
 * To start without MongoDB, set SPRING_PROFILES_ACTIVE=no-mongo (loads application-no-mongo.properties).
 */
@SpringBootApplication
public class backendApplication {

    public static void main(String[] args) {
        SpringApplication.run(backendApplication.class, args);
    }
}
