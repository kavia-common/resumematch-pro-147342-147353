Environment setup and preview troubleshooting

Overview
- Backend is Spring Boot and uses MongoDB. It does NOT use MySQL.
- Frontend is React and calls the backend via REACT_APP_API_BASE.
- Database service in this workspace currently advertises MySQL ports, but this project expects MongoDB; run Mongo locally or via a managed instance.

Backend (Spring Boot)
1) Copy backend/.env.example to backend/.env
2) Set JWT_SECRET to a strong random string (>=32 chars). This is required.
3) Ensure MongoDB is reachable:
   - Local: start Mongo listening on 27017 and keep defaults
   - Remote: set MONGODB_URL and MONGODB_DB accordingly
4) If frontend runs at a different origin (e.g., preview URL), add it to CORS_ALLOWED_ORIGINS (comma-separated)
5) Start backend:
   - From backend directory: ./gradlew bootRun
   - Health: GET /health returns "OK"
   - Swagger UI: /swagger-ui.html
   - OpenAPI: /api-docs

Frontend (React)
1) Ensure a .env is present in react_frontend with:
   REACT_APP_API_BASE=http://localhost:3001
   - Or set to your backend preview URL if different.
2) The frontend must set Authorization header after login:
   - setAuthToken(token) (from src/api/client.js)
3) Start frontend:
   - From react_frontend directory: npm start

MongoDB notes
- Variables: MONGODB_URL, MONGODB_DB
- If Mongo is not available at startup, backend will try to connect via Spring Data Mongo.
- For previews without Mongo, either:
  - provide a reachable Mongo instance, or
  - run backend with a test profile that stubs persistence (not included by default)

Common issues and fixes
- Backend preview fails at startup:
  - Ensure backend/.env exists and JWT_SECRET is set.
  - Ensure MONGODB_URL is reachable and points to a MongoDB instance (not MySQL).
  - If CORS errors from frontend, add the frontend URL to CORS_ALLOWED_ORIGINS in backend/.env.
- Frontend cannot reach API:
  - Ensure REACT_APP_API_BASE is configured to the backend URL.
  - Check that /health returns OK from backend URL.
- Database preview mismatch:
  - The 'mongodb_database' container description mentions MySQL; this project uses MongoDB.
  - Use your own Mongo instance. Update MONGODB_URL accordingly.
