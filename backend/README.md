# Backend (Spring Boot) - ResumeMatch Pro

Environment variables (copy .env.example to .env):
- MONGODB_URL (default: mongodb://localhost:27017)
- MONGODB_DB (default: resumematch_pro)
- JWT_SECRET (REQUIRED, no default; use a long random string)
- JWT_EXP_MINUTES (default: 120)
- FILE_STORAGE_DIR (default: ./storage)
- CORS_ALLOWED_ORIGINS (default: http://localhost:3000)

Security note:
- JWT_SECRET must be provided. Without it, the app will fail to start as expected for security.

Run locally:
1) Start MongoDB (local or remote) accessible by MONGODB_URL and MONGODB_DB.
2) Set environment vars (create backend/.env or export in shell).
3) From backend directory:
   ./gradlew bootRun

API:
- Base Path: /
- Auth: /auth/register, /auth/login
- Business: /api/**
- Swagger UI: /swagger-ui.html
- OpenAPI: /api-docs
- Health: /health returns "OK"

CORS:
- Controlled by CORS_ALLOWED_ORIGINS (default http://localhost:3000). For previews, include your frontend preview URL as well (comma-separated).

File storage:
- FILE_STORAGE_DIR points to a directory for uploads (default ./storage). The app creates it if missing.

Integration with frontend:
- Frontend should set REACT_APP_API_BASE (default fallback is http://localhost:3001 in frontend code).
- After login, frontend must set the Authorization header via setAuthToken(token) from src/api/client.js.

Preview troubleshooting:
- Backend fails to start:
  - Ensure backend/.env exists and JWT_SECRET is set (>=32 characters recommended).
  - Verify Mongo is reachable via MONGODB_URL and MONGODB_DB (this backend uses MongoDB, not MySQL).
- Frontend cannot call API:
  - Ensure REACT_APP_API_BASE points to the backend URL.
  - Add the frontend origin to CORS_ALLOWED_ORIGINS in backend/.env if you see CORS errors.
- Database preview mismatch:
  - If another container advertises MySQL, ignore it for this backend; use a MongoDB instance instead.
