# AI-Powered Smart Recruitment Engine

An AI-assisted backend system that helps recruiters screen candidates faster by automatically parsing resumes and scoring candidates against job descriptions using Google's Gemini API. The system extracts skills, experience, education, projects, and certifications from resumes, then generates a match score, confidence level, and detailed reasoning for each candidate against a specific job — while keeping the final hiring decision entirely with the human recruiter.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security + JWT (jjwt 0.13.0) |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| AI | Google Gemini API (gemini-3.6-flash) |
| PDF Parsing | Apache PDFBox |

---

## Architecture Overview

```
Client (Postman)
      │  HTTPS + JWT
      ▼
Controller Layer  → HTTP routing, request/response DTOs
      ▼
Service Layer     → business logic, orchestration
      ▼
   ┌──┴───────────────┐
   ▼                  ▼
Repository Layer   AI Integration Layer (interface + Gemini impl)
   ▼                  ▼
  MySQL           Gemini API
```

Layered monolith (Controller → Service → Repository), with AI-specific logic isolated behind `ResumeParsingService` / `AIScoringService` interfaces — so the LLM provider can be swapped later by changing only the implementation class, never the business logic that calls it.

---

## Core Features

- JWT-based authentication with role-based access (`ADMIN`, `RECRUITER`)
- Admin-managed recruiter account creation
- Candidate CRUD with AI-processing status tracking (`UPLOADED → PARSED → SCORING_PENDING → SCORED / FAILED`)
- Job Description CRUD with lifecycle status (`DRAFT → OPEN → CLOSED`)
- PDF resume upload (10MB limit) with automatic text extraction
- AI-powered resume parsing (skills, experience, education, projects, certifications) via Gemini
- Manual override of AI-extracted candidate fields (human-in-the-loop)
- Application entity linking Candidates to Job Descriptions (many-to-many via join entity)
- AI-powered candidate scoring per Application: match %, confidence (HIGH/MEDIUM/LOW), matching/missing skills, strengths, weaknesses, summary
- Ranked candidate list per job
- Recruiter dashboard: total candidates, pending AI analysis, recent uploads
- Search, filter, and sort over candidates (skill, education, experience, name, latest)
- Centralized global exception handling with consistent error responses

---

## Prerequisites

- JDK 21
- Maven (or use the included `mvnw`/`mvnw.cmd` wrapper — no separate install needed)
- MySQL Server (running locally or accessible remotely)
- A free Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey)

---

## Setup Instructions

### 1. Clone and configure

```bash
git clone <repository-url>
cd smart-recruitment-engine
```

### 2. Create the database

```sql
CREATE DATABASE smart_recruitment_engine;
```

### 3. Configure environment variables

Copy `application.properties.example` to `application.properties` under `src/main/resources/`, then set the following (either directly in the file for local development, or as environment variables — recommended):

| Variable | Description |
|---|---|
| `spring.datasource.username` | MySQL username |
| `spring.datasource.password` | MySQL password |
| `jwt.secret` | A long, random string (256-bit minimum) used to sign JWTs |
| `gemini.api-key` | Your Gemini API key from Google AI Studio |

> **Never commit real secrets to source control.** `application.properties` is git-ignored; use `application.properties.example` as the template.

### 4. Run the application

```bash
./mvnw clean compile      # verify the build
./mvnw spring-boot:run    # start the app
```

The app starts on `http://localhost:8080`. On first run, `RoleSeeder` automatically creates the `ADMIN` and `RECRUITER` roles.

### 5. Create your first admin user

Since there's no public registration endpoint (by design — accounts are admin-provisioned), insert the first admin manually:

```sql
INSERT INTO users (name, email, password_hash, role_id, is_active, created_at, updated_at)
VALUES ('Admin', 'admin@test.com', '<bcrypt-hash-of-your-password>',
        (SELECT id FROM roles WHERE name = 'ADMIN'), true, NOW(), NOW());
```

Generate a BCrypt hash using any online BCrypt generator, or a short one-off Java snippet with `new BCryptPasswordEncoder().encode("yourpassword")`.

---

## API Reference

All endpoints except `/api/auth/login` require a `Authorization: Bearer <token>` header. Admin-only endpoints are marked accordingly.

### Auth

#### `POST /api/auth/login`
**Request:**
```json
{
  "email": "admin@test.com",
  "password": "password123"
}
```
**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@test.com",
  "role": "ADMIN"
}
```

---

### Admin — Recruiter Management *(ADMIN only)*

#### `POST /api/admin/recruiters`
**Request:**
```json
{
  "name": "Jane Recruiter",
  "email": "jane@test.com",
  "password": "recruiter123",
  "role": "RECRUITER"
}
```
**Response `201 Created`:**
```json
{
  "id": 2,
  "name": "Jane Recruiter",
  "email": "jane@test.com",
  "role": "RECRUITER",
  "active": true,
  "createdAt": "2026-07-28T10:00:00"
}
```

#### `GET /api/admin/recruiters`
Returns a list of all users (same shape as above).

#### `PATCH /api/admin/recruiters/{id}/deactivate`
Deactivates a recruiter account (soft delete — preserves audit trail).

---

### Job Descriptions

#### `POST /api/jobs`
**Request:**
```json
{
  "title": "Backend Java Developer",
  "description": "Building and maintaining REST APIs using Spring Boot.",
  "requiredSkills": "Java, Spring Boot, MySQL, REST APIs"
}
```
**Response `201 Created`:** JobDescription object with `status: "DRAFT"`.

#### `GET /api/jobs/{id}` / `GET /api/jobs`
Retrieve a single job or all jobs.

#### `PUT /api/jobs/{id}`
Update job content (blocked if job is `CLOSED`).

#### `PATCH /api/jobs/{id}/status`
**Request:** `{ "status": "OPEN" }`
Valid transitions: `DRAFT → OPEN/CLOSED`, `OPEN → CLOSED`. `CLOSED` is terminal.

#### `DELETE /api/jobs/{id}`
Returns `204 No Content`.

---

### Candidates

#### `POST /api/candidates`
**Request:**
```json
{
  "name": "Ankit Sharma",
  "email": "ankit@example.com",
  "phone": "9999999999"
}
```
**Response `201 Created`:** Candidate object with `status: "UPLOADED"`.

#### `GET /api/candidates/{id}` / `GET /api/candidates`
Retrieve a single candidate or all candidates.

#### `GET /api/candidates?skill=java&education=btech&minExperience=2&sortBy=experience`
Search/filter/sort. `sortBy` accepts: `name` (default), `experience`, `latest`.

#### `PUT /api/candidates/{id}`
Update basic candidate info (name, email, phone).

#### `PATCH /api/candidates/{id}/fields`
Manually override AI-extracted fields.
**Request:**
```json
{
  "skills": "Java, Spring Boot, Docker",
  "experienceYears": 2.5,
  "education": "B.Tech Computer Science",
  "projects": "...",
  "certifications": "..."
}
```

#### `DELETE /api/candidates/{id}`
Returns `204 No Content`.

---

### Resume

#### `POST /api/candidates/{candidateId}/resume`
`multipart/form-data`, key: `file` (PDF only, max 10MB).

**Response `200 OK`:**
```json
{
  "candidateId": 1,
  "status": "PARSED",
  "message": "Resume uploaded and parsed successfully"
}
```
Triggers Gemini-based extraction of skills, experience, education, projects, and certifications directly into the Candidate record.

---

### Applications

#### `POST /api/applications`
**Request:**
```json
{ "candidateId": 1, "jobDescriptionId": 1 }
```
Requires the target job to have `status: "OPEN"`. Enforces one Application per Candidate–Job pair.

#### `GET /api/applications/{id}`
Returns the Application, including nested AI score if scoring has been run.

#### `POST /api/applications/{id}/score`
Triggers Gemini-based AI scoring.
**Response `200 OK`:**
```json
{
  "id": 1,
  "candidateId": 1,
  "candidateName": "Ankit",
  "jobDescriptionId": 1,
  "jobTitle": "Backend Java Developer",
  "status": "SCORED",
  "aiScore": {
    "matchPercentage": 88.0,
    "confidence": "HIGH",
    "matchingSkills": "Java, Spring Boot, MySQL, REST APIs",
    "missingSkills": "None",
    "strengths": "100% match on core technical stack...",
    "weaknesses": "Zero years of full-time professional experience...",
    "summary": "The candidate strongly aligns with all technical skill requirements..."
  }
}
```

#### `GET /api/jobs/{jobId}/applications`
Returns all Applications for a job, ranked by match score descending.

---

### Dashboard

#### `GET /api/dashboard/summary`
```json
{
  "totalCandidates": 12,
  "pendingAiAnalysis": 3,
  "recentUploads": [ /* last 10 candidates */ ]
}
```

---

## Error Response Format

All errors share a consistent shape:

```json
{
  "timestamp": "2026-07-28T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Candidate not found with id: 999",
  "fieldErrors": null
}
```

| Status | Meaning |
|---|---|
| `400` | Validation failure or invalid file upload |
| `401` | Missing/invalid JWT, or bad login credentials |
| `403` | Authenticated but not authorized (wrong role) |
| `404` | Resource not found |
| `405` | HTTP method not supported for the endpoint |
| `409` | Duplicate resource or invalid state transition |
| `502` | Gemini API call failed, timed out, or returned malformed data |
| `500` | Unexpected internal error |

---

## Known Limitations (By Design — MVP Scope)

- `ddl-auto=update` is used for schema management — appropriate for development, not production (no versioned migrations; use Flyway/Liquibase for production).
- No refresh tokens — JWT expires after 4 hours; re-login is required after expiry.
- No candidate self-service portal — this is a recruiter-facing screening tool only.
- Skills are stored as free text (AI-compared via prompt, not normalized/joined) — a deliberate trade-off since matching logic lives in the Gemini prompt, not SQL.
- Resume re-upload replaces the existing resume; no version history is kept.

---

## Postman Collection

A Postman collection covering all endpoints above is available at `postman/SmartRecruitmentEngine.postman_collection.json`.

---

## Project Structure

```
com.recruitment.engine
├── config/        → Security, Gemini client, Role seeding
├── controller/     → REST controllers
├── service/        → Business logic
│   └── ai/           → Gemini integration (interface + implementation)
├── repository/      → Spring Data JPA repositories
├── entity/          → JPA entities + enums
├── dto/
│   ├── request/       → Incoming DTOs
│   └── response/      → Outgoing DTOs
├── security/        → JWT filter, JwtService, UserDetailsService
├── exception/        → Custom exceptions + GlobalExceptionHandler
└── util/             → PDF text extraction
```
