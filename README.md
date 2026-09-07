# Real-Time Chat App 💬

> A real-time chat application built with Spring Boot WebSocket (STOMP), Spring Security JWT authentication, React + TypeScript frontend, online presence tracking, message history (PostgreSQL), and Docker Compose deployment.

![Java](https://img.shields.io/badge/Java-17-orange.svg) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green.svg) ![React](https://img.shields.io/badge/React-18.x-blue.svg) ![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-green.svg)

## Features

- Real-time messaging via Spring Boot WebSocket (STOMP protocol)
- JWT authentication with Spring Security
- Online presence tracking (who is online)
- Message history persisted in PostgreSQL
- Public chat room + private direct messages
- Typing indicators
- User join/leave notifications
- React + TypeScript frontend with responsive UI (Tailwind CSS)
- Docker Compose for one-command deployment
- Swagger/OpenAPI API documentation

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Spring WebSocket (STOMP), Spring Security, JPA |
| Frontend | React 18, TypeScript, Vite, Tailwind CSS, SockJS + StompJS |
| Database | PostgreSQL 16 |
| Auth | JWT (jjwt) |
| Real-time | WebSocket (STOMP over SockJS) |
| Deploy | Docker Compose |

## Quick Start

### Docker Compose

```bash
git clone https://github.com/shivamkumarhub-web/realtime-chat-app.git
cd realtime-chat-app
docker-compose up --build
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- WebSocket endpoint: ws://localhost:8080/ws
- Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432

### Run Separately (dev mode)

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## Project Structure

```
realtime-chat-app/
├── backend/
│   ├── src/main/java/com/shivam/chat/
│   │   ├── config/          # Security, WebSocket, OpenAPI config
│   │   ├── controller/      # REST + WebSocket controllers
│   │   ├── dto/             # Message, Auth DTOs
│   │   ├── entity/          # User, Message JPA entities
│   │   ├── exception/       # Global exception handler
│   │   ├── repository/     # JPA repositories
│   │   ├── security/       # JWT provider, filter, UserDetailsService
│   │   └── service/         # Auth, Message, Presence services
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/     # Chat UI components
│   │   ├── pages/          # Login, Register, Chat pages
│   │   ├── api/            # REST + WebSocket client
│   │   └── context/       # Auth context
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── README.md
```

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /api/auth/register | Register | No |
| POST | /api/auth/login | Login | No |
| GET | /api/messages | Get message history | Yes |
| GET | /api/messages/room/{room} | Get messages by room | Yes |
| GET | /api/users/online | Get online users | Yes |

### WebSocket (STOMP)

| Destination | Description |
|------------|-------------|
| /app/chat.sendMessage | Send a message to a room |
| /app/chat.addUser | User joins a room |
| /topic/room/{roomId} | Subscribe to room messages |
| /topic/presence | Subscribe to presence updates |
| /user/queue/reply | Private messages |

## License

MIT