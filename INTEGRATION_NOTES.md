# Integration Notes - ResumeMatch Pro

Overview:
- Backend (Spring Boot) reads configuration from environment variables with sensible defaults.
- Frontend (React) reads REACT_APP_API_BASE for the backend base URL, with a fallback to http://localhost:3001.
- MongoDB variables are provided via templates and expected by the backend.

Backend environment:
- MONGODB_URL (default: mongodb://localhost:27017)
- MONGODB_DB (default: resumematch_pro)
- JWT_SECRET (REQUIRED; no default)
- JWT_EXP_MINUTES (default: 120)
- FILE_STORAGE_DIR (default: ./storage)
- CORS_ALLOWED_ORIGINS (default: http://localhost:3000)

Frontend environment:
- REACT_APP_API_BASE (recommended: http://localhost:3001)

MongoDB:
- Template variables: MONGODB_URL, MONGODB_DB
- Backend expects these variables; if not set, defaults are used.

Token handling in frontend:
- Import { setAuthToken } from src/api/client.js and call setAuthToken(token) after login/register.
- Clear with setAuthToken(null) when logging out.

Local dev quick start:
1) Start MongoDB locally on 27017 or configure MONGODB_URL to a reachable Mongo instance (the backend uses MongoDB, not MySQL).
2) Backend:
   - Copy backend/.env.example to backend/.env and set JWT_SECRET (>=32 chars).
   - Optionally set CORS_ALLOWED_ORIGINS to include your frontend origin(s).
   - Run: ./gradlew bootRun
   - Verify: GET /health should return "OK"; Swagger UI at /swagger-ui.html; OpenAPI at /api-docs
3) Frontend:
   - Copy react_frontend/.env.example to react_frontend/.env and set REACT_APP_API_BASE (or rely on fallback).
   - Run: npm start
