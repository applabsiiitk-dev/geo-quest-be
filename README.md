# GeoQuest Event Management Backend
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Tests](https://img.shields.io/badge/Tests-0%20passed-success)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## Project Overview

A robust Spring Boot backend for managing location-based quiz events, teams, sessions, and answers. Designed for real-time gameplay, secure authentication, and scalable event operations.

## Tech Stack

- Java 21 (Spring Boot)
- Spring Security (OAuth2, JWT)
- Google OAuth2 integration
- JPA/Hibernate (PostgreSQL or NeonDB)
- RESTful API
- Docker support

## Key Features

- Google OAuth2 authentication
- JWT-based session management
- Event CRUD operations (create, update, delete, list)
- Team and session management
- Real-time leaderboard
- Location-based question unlocking
- Per-marker cooldown logic
- Role-based access control (admin/user)

## Quick Start

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/fl4nk3r-h/geo-quest-be
   cd geo_quest
   ```

2. Set up environment variables:
   - `app.jwt.secret` (JWT signing key)
   - `app.google.client-id` (Google OAuth2 client ID)
   - Database credentials in .env file
3. Build and run:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Access API at `http://localhost:8080`

---


## Security & Google OAuth2 Flow

### SecurityFilterChain Configuration

- The backend uses Spring Security's `SecurityFilterChain` to enforce stateless authentication and role-based access control.
- CORS is enabled for cross-origin requests (Flutter/web clients).
- Public endpoints (e.g., `/api/auth/google`, `/api/leaderboard`, `/api/spawn-locations`) are accessible without authentication.
- Admin endpoints (e.g., `/api/questions` POST/DELETE) require `ROLE_ADMIN`.
- All other endpoints require a valid JWT in the `Authorization: Bearer <token>` header.
- The custom `JwtAuthenticationFilter` is added before the default authentication filter to parse and validate JWTs.

### Google OAuth2 User Attribute Extraction

- Users sign in via Google OAuth2 on the client (Flutter/web).
- The client sends the Google ID token to the backend (`POST /api/auth/google`).
- The backend verifies the token using Google's libraries and checks the email domain (must be `@iiitkottayam.ac.in`).
- User attributes extracted:
  - `uid`: Google unique user ID
  - `email`: User's email address
- If valid, the backend issues its own JWT containing these attributes.

### Session & JWT Management

- Sessions are stateless; no server-side session storage is used.
- JWTs are generated with UID and email, signed with a secret, and have a 150-minute expiry.
- The client stores the JWT and includes it in all subsequent requests.
- On each request, the `JwtAuthenticationFilter` parses the JWT, extracts user info, and sets the Spring Security context.
- Logout is handled client-side by discarding the JWT.

---

## API Endpoint Table: Event Management

| Method | URL                                 | Required Role   |
|--------|--------------------------------------|----------------|
| POST   | /api/teams                          | ROLE_USER      |
| GET    | /api/teams                          | Public         |
| GET    | /api/teams/{teamId}                 | Public         |
| POST   | /api/teams/{teamId}/join            | ROLE_USER      |
| DELETE | /api/teams/{teamId}/members/{memberId} | ROLE_USER (member/creator) |
| POST   | /api/sessions/start                 | ROLE_USER (team member) |
| GET    | /api/sessions/{sessionId}           | ROLE_USER (team member) |
| GET    | /api/sessions/{sessionId}/remaining-time | ROLE_USER (team member) |
| POST   | /api/questions                      | ROLE_ADMIN     |
| DELETE | /api/questions/{questionId}         | ROLE_ADMIN     |
| POST   | /api/questions/unlock               | ROLE_USER      |
| GET    | /api/questions/{questionId}         | ROLE_USER      |

---

##  Environment Configuration

The application requires a `.env` file in the root directory. Ensure the following variables are defined to match `application.properties`:

| Variable | Description | Source |
| :--- | :--- | :--- |
| `GOOGLE_CLIENT_ID` | OAuth2 Client ID | Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | OAuth2 Client Secret | Google Cloud Console |
| `DB_URL` | JDBC Connection String | PostgreSQL/MySQL |
| `DATABASE_USERNAME` | Database Username | Your Database Credentials |
| `DATABASE_PASSWORD` | Database Password | Your Database Credentials |
| `JWT_SECRET` | HS256 Signing Key | Random 32-char string |


## Upcoming Fixes

- **Overbooking Prevention:** Ensure only one active session per team; use optimistic locking or database constraints to prevent race conditions.
- **Transaction Handling:** Apply `@Transactional` to team join/leave and session creation for atomicity.
- **Input Validation & Rate Limiting:** Validate all inputs and enforce rate limits on answer submissions and session creation to prevent abuse.

---

## License

MIT/Apache-2.0 (see LICENSE)

## Maintainers

- AppLabs-IIITK
- Contact: <applabsiiitk@gmail.com>

## Authors
- fl4nk3r

## Acknowledgements
- Inspired by various open-source Spring Boot projects and best practices in REST API design and security.
- Thanks to the IIIT Kottayam community for feedback and testing.
- Google for their OAuth2 libraries and documentation.
- PostgreSQL and NeonDB for their robust database solutions.
- Docker for simplifying deployment and environment management.
