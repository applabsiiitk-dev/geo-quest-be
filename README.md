# How to Play GeoQuest

1. **Register & Login:** Sign in with your Google account (@iiitkottayam.ac.in only), then register your profile.
2. **Create or Join a Team:** Form a team (max 4 members) or join an existing one.
3. **Start a Session:** Begin a 2-hour competition session for your team. Each session gets a unique, randomly shuffled set of questions (one per location/tier). No two teams get the same question at a location; assignment is independent and random.
4. **Unlock Questions:** Use your GPS to unlock questions near campus markers. Markers may be locked if both alternates are taken.
5. **Answer Questions:** Submit answers for unlocked questions. On correct answer, receive points and a riddle/clue for the next location. The riddle is always the description of the next question assigned to your session.
6. **Score & Leaderboard:** Team scores are updated and shown on the public leaderboard.
7. **Session End:** Session ends after 2 hours or when all questions are answered.

## Game Logic Highlights

- **Random Question Assignment:** At session start, questions are shuffled and assigned randomly per team. Each team gets a unique question at each location and tier.
- **Riddle-Based Hints & Progression:** Each question's description is a riddle/clue for the next location. After a correct answer, the next question's description is sent as a hint, guiding the team to the next marker.
- **Difficulty Tiers:** Easy (score 0–99): 10 pts; Medium (score 100–249): 20–25 pts; Hard (score 250+): 40–50 pts.
- **GPS Proximity:** Questions are unlocked based on proximity to campus markers. Radius tiers: Indoor (15m), Gate (10m), Open (5m).
- **Team & Session Management:** Teams must be active and not full to join. Only one active session per team.
- **Rate Limiting & Cooldowns:** Unlock requests are rate-limited. Answer submissions may trigger cooldowns for locations.

## Logical Flaws & Edge Cases

- **Marker Locking:** If both alternates at a location are occupied, the marker is locked. Teams may be unable to progress if too many markers are locked.
- **Session Expiry:** Sessions are auto-completed on expiry, but edge cases may occur if answers are submitted near expiry.
- **GPS Spoofing:** The game relies on GPS proximity; spoofing may allow unfair unlocking.
- **Team Size Enforcement:** Team size is enforced, but edge cases may occur if members leave mid-session.
- **Rate Limiting:** Rate limiter is in-memory; may not scale horizontally (should use Redis for distributed deployments).
- **Answer Attempts:** Multiple wrong attempts are tracked, but no explicit limit is enforced (could add attempt limits).
- **Concurrent Sessions:** Only one active session per team, but concurrent session starts may cause race conditions.
- **Question Assignment:** Unique shuffle per session, but if all questions at a location are taken, teams may be blocked.

## Randomized Question Trail

- Each session now receives a unique, randomly shuffled sequence of questions (question trail) when the game begins.
- The trail is stored in the Session entity as `questionTrail` (List<String>) and progress is tracked with `currentTrailIndex`.
- This replaces the previous "one question per location" constraint, allowing each team to follow a unique path.
- The trail is generated using the `generateRandomTrail()` method in `SessionService`, which fetches all question IDs from the database and shuffles them.

---

**For best experience, play as a team, use campus locations, and follow the riddles to progress!**

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

## Environment Configuration

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
