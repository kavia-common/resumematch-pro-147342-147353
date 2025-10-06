# resumematch-pro-147342-147353

This repository contains the Spring Boot backend for ResumeMatch Pro along with other containers in the workspace.

Backend quick start:
- Copy backend/.env.example to backend/.env and set values (JWT_SECRET is required).
- If you don't have MongoDB for previews, leave SPRING_PROFILES_ACTIVE=no-mongo in backend/.env to start without Mongo.
- Otherwise, ensure MongoDB is running and accessible per MONGODB_URL/MONGODB_DB.
- From backend directory, run:
  ./gradlew bootRun

Required environment variables (backend):
- MONGODB_URL: Mongo connection string (default mongodb://localhost:27017)
- MONGODB_DB: Database name (default resumematch_pro)
- JWT_SECRET: REQUIRED, no default; strong random string for signing tokens
- JWT_EXP_MINUTES: Token lifetime in minutes (default 120)
- FILE_STORAGE_DIR: Base folder for uploaded files (default ./storage)
- CORS_ALLOWED_ORIGINS: Allowed origins for browser clients (default http://localhost:3000)
- SPRING_PROFILES_ACTIVE: optional; set to no-mongo to boot without Mongo

API base URL (dev): http://localhost:3001 by default if configured to run on 3001 (adjust to your setup).
Swagger UI: /swagger-ui.html
OpenAPI JSON: /api-docs