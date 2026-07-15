# ConnectHub

A real-time chat and social-connection app: register, log in, find people by email, send/accept/reject friend requests, and chat live with online/offline presence — built on a Spring Boot backend with a vanilla HTML/CSS/JS frontend.

---

## Features

- **Auth** — register and log in with JWT-based sessions
- **Find & add friends** — send a friend request by exact email address
- **Friend requests** — view pending requests, accept or reject them
- **Real-time chat** — live messaging over WebSocket (STOMP/SockJS), with REST fallback if the socket drops
- **Message history** — past conversations load from the database
- **Online/offline presence** — lightweight heartbeat-based presence indicator per friend
- **Responsive UI** — works on desktop and mobile (slide-in chat view on small screens)

---

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.5
- Spring Security + JWT (`io.jsonwebtoken`)
- Spring Data JPA (Hibernate) + MySQL
- Spring WebSocket (STOMP over SockJS)
- Lombok

**Frontend**
- Single-file HTML/CSS/JS (no build step)
- [SockJS](https://github.com/sockjs/sockjs-client) + [Stomp.js](https://stomp-js.github.io/) for WebSocket chat
- Served via Nginx in production (see `ConnectHubFrontEnd/Dockerfile`)

**Infra**
- Docker / Docker Compose for local multi-container setup
- Deployed on [Render](https://render.com) (backend web service + MySQL + static/Nginx frontend)

---

## Project Structure

```
ConnectHUB/
├── src/main/java/ConnectHub/
│   ├── Config/            # WebSocket, CORS, security beans
│   ├── Controller/        # REST controllers (auth, users, friends, messages)
│   ├── Dto/                # Request/response payloads
│   ├── Entity/            # JPA entities (User, FriendRequest, Message)
│   ├── Exception/         # Global exception handler
│   ├── Repository/        # Spring Data repositories
│   ├── Security/          # JWT util, auth filter, WebSocket auth interceptor
│   ├── Service/           # Business logic
│   └── Main.java
├── src/main/resources/
│   └── application.properties
├── docker-compose.yml
├── Dockerfile              # backend
└── ConnectHubFrontEnd/
    ├── index.html          # the entire frontend
    └── Dockerfile           # nginx wrapper
```

---

## Prerequisites

- Java 21+
- Maven
- MySQL 8+ (or use the provided `docker-compose.yml`)
- A modern browser (for the frontend — no build tooling required)

---

## Environment Variables (Backend)

Set these wherever the backend runs (locally via `.env`/IDE run config, or in your hosting provider's dashboard):

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL | `jdbc:mysql://localhost:3306/connecthub` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `yourpassword` |
| `PORT` | Port the server binds to (defaults to `8080`) | `8080` |
| `ALLOWED_ORIGINS` | Comma-separated list of frontend origins allowed for CORS + WebSocket handshakes | `https://your-frontend.onrender.com,http://localhost:3000` |

`application.properties` should reference all of these with safe local defaults:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=${PORT:8080}

allowed.origins=${ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000}
```

> **Important:** `ALLOWED_ORIGINS` must exactly match the URL your frontend is actually served from (scheme, subdomain, no trailing slash) or every API call and WebSocket connection will be blocked by CORS. This is the #1 cause of "it works locally but not when deployed."

---

## Running Locally

### Option A — Docker Compose (recommended, spins up MySQL + backend + frontend together)

```bash
docker-compose up --build
```

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- MySQL: exposed on `localhost:3307`

### Option B — Manual

1. Start MySQL and create a `connecthub` database.
2. Set the environment variables above (or hardcode local values in `application.properties` for quick testing).
3. Build and run the backend:
   ```bash
   mvn clean package -DskipTests
   java -jar target/connect-hub.jar
   ```
4. Open `ConnectHubFrontEnd/index.html` directly in a browser, or serve it with any static file server:
   ```bash
   npx serve ConnectHubFrontEnd
   ```
5. In the app, click the ⚙ settings icon and point the frontend at your backend URL (e.g. `http://localhost:8080`).

---

## API Reference

Base path: `/api`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/auth/register` | Create an account (`username`, `email`, `password`) | No |
| `POST` | `/auth/login` | Log in, returns `{ jwt, id }` | No |
| `GET` | `/user/profile` | Get the logged-in user's profile | Yes |
| `POST` | `/friends/request/{email}` | Send a friend request to a user by email | Yes |
| `GET` | `/friends/pending` | List incoming pending friend requests | Yes |
| `POST` | `/friends/accept/{id}` | Accept a pending friend request (FriendRequest ID) | Yes |
| `POST` | `/friends/reject/{id}` | Reject a pending friend request (FriendRequest ID) | Yes |
| `GET` | `/friends/friendlist` | List accepted friends | Yes |
| `GET` | `/message/history/{friendId}` | Get chat history with a friend | Yes |
| `POST` | `/message/send/{friendId}` | Send a message via REST (fallback if WebSocket is down) | Yes |

Authenticated requests use `Authorization: Bearer <jwt>`.

### WebSocket / Real-time Chat

- Endpoint: `/chat` (SockJS)
- STOMP `CONNECT` header: `Authorization: Bearer <jwt>`
- Subscribe: `/user/queue/messages` — receive incoming chat messages
- Send: `/app/send` with body `{ senderEmail, receiverEmail, content }`
- Presence: clients publish/subscribe to `/topic/presence` with `{ email, ts }` heartbeats to approximate online/offline status (no dedicated backend presence tracking exists — this is a broker-level broadcast trick, not guaranteed-accurate)

---

## Deployment Notes (Render)

1. Deploy MySQL (Render MySQL, PlanetScale, or any managed instance) and note its connection details.
2. Deploy the backend as a **Web Service**, set the environment variables listed above (especially `ALLOWED_ORIGINS` pointing at your frontend's real Render URL).
3. Deploy the frontend as a **Static Site** (or the provided Nginx Dockerfile), and use the app's ⚙ settings to point it at the backend's Render URL.
4. Verify the WebSocket handshake succeeds: open DevTools → Network → filter "WS" → look for a `101 Switching Protocols` on `/chat`. If it fails, it's almost always `ALLOWED_ORIGINS` not matching the frontend's exact URL.

---

## Known Limitations / Things to Improve

- **JWT expiry is 10 minutes** (`AuthUtil`) — fine for testing, too short for real use. Consider extending this and/or adding refresh tokens.
- **Timestamps** are stored/serialized as timezone-less `LocalDateTime`. The frontend assumes the server clock is UTC and converts accordingly; if you ever run the backend in a non-UTC environment, this will need to change to `Instant`/`OffsetDateTime` on the backend instead.
- **No dedicated presence system** — online/offline status is approximated via a shared WebSocket heartbeat topic rather than true server-tracked session state.
- **No search-by-username** — adding a friend requires knowing their exact email address; there's no user directory/search endpoint.
- **JWT secret is hardcoded** in `AuthUtil` — move this to an environment variable before using this in anything beyond a personal project.

---


