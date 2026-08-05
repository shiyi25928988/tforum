<div align="center">

<img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white">
<img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?logo=springboot&logoColor=white">
<img alt="Vue" src="https://img.shields.io/badge/Vue-3.5.13-4FC08D?logo=vuedotjs&logoColor=white">
<img alt="License" src="https://img.shields.io/badge/License-MIT-blue?logo=opensourceinitiative&logoColor=white">
<img alt="Docker" src="https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white">

</div>

# tForum

> A modern developer community forum with AI capabilities — articles, discussions, book corner, skills sharing, full-text search, RAG-powered Q&A, third-party app SSO integration, and out-of-the-box Docker full-stack deployment.

<p align="center">
🇨🇳 <a href="./README.md">简体中文</a> | 🇺🇸 <a href="./README.en.md">English</a>
</p>

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [API Overview](#api-overview)
- [Third-Party App Integration](#third-party-app-integration)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Frontend (User-facing)

| Module | Description |
|--------|-------------|
| Home | Article list + search + popular tags, Markdown publishing |
| Discussion | Categorized topics with replies |
| Book Corner | PDF upload & sharing with SHA-256 deduplication |
| Skills | Skill sharing with icon / attachment / Git links |
| Markdown Editor | Fullscreen editor with syntax highlighting, image upload, AI writing assist |
| AI Assistant | Floating AI chat drawer with streaming SSE, RAG knowledge base Q&A |
| User System | Register / login / profile, Sa-Token authentication |
| Global Search | Lucene + IK analyzer full-text fuzzy search |
| Dynamic Navbar | Admin-configurable item visibility and external links |
| MinIO File Proxy | All file downloads proxied through backend, no direct MinIO access needed |

### Admin Console

| Module | Description |
|--------|-------------|
| Dashboard | User / article / discussion / book count statistics |
| User Management | List + disable / enable |
| Content Management | View and delete articles, discussions, books, skills |
| Navbar Management | Configure item visibility / order, add custom items (external links) |
| Vector DB Management | Article vectorization / deletion, Milvus collection management |
| Tags / Discussion Groups | CRUD |

### Third-Party App Integration

| Capability | Description |
|------------|-------------|
| Token Placeholder | External navbar links support `{token}` placeholder, auto-replaced with current user's token on click |
| Token Verification API | `GET /api/v1/user/verifyToken?token=xxx` for third-party apps to verify tokens and retrieve user info (SSO) |

> [!NOTE]
> For the complete SSO integration flow and code examples, see [Third-Party App Integration Guide](docs/第三方应用集成方案.md).

---

## Tech Stack

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Runtime |
| Spring Boot | 3.5.7 | Core framework |
| Spring Cloud | 2025.0.0 | Microservices |
| MyBatis-Plus | 3.5.14 | ORM |
| Sa-Token | 1.44.0 | Authentication |
| Spring AI | 1.0.3 | AI chat / RAG / MCP |
| Milvus | 2.6.2 | Vector database |
| Apache Lucene | 10.3.2 | Full-text search |
| MinIO | 8.6.0 | Object storage |
| IK Analyzer | 5.1.0 | Chinese tokenizer |

### Frontend

| Technology | Purpose |
|------------|---------|
| Vue 3 + Composition API | UI framework |
| TypeScript | Type safety |
| Vite | Build tool |
| Element Plus | UI component library |
| Pinia | State management |
| Vue Router | Routing |
| md-editor-v3 | Markdown editor |
| marked | Markdown rendering |
| Axios | HTTP client |
| ECharts | Charts |
| Mermaid | Diagram rendering |

---

## Project Structure

```
tforum/
├── common/          # Common module (AI services, utilities, aspects, HTTP wrapper)
├── forum/           # Main application (Controller, Service, Table, frontend)
│   ├── src/main/java/.../controller/
│   │   ├── ai/          # AI chat
│   │   ├── admin/       # Admin console
│   │   ├── article/     # Articles & tags
│   │   ├── book/        # Book corner
│   │   ├── discussion/  # Discussion categories
│   │   ├── markdown/    # Markdown docs
│   │   ├── oss/         # File management
│   │   ├── search/      # Global search
│   │   └── user/        # User management
│   └── frontend/        # Vue 3 frontend project
├── markdown/        # Markdown module (Entity, parsing utilities)
├── mcp/             # MCP server (RAG, SSH, email tools)
├── oss/             # Object storage module (MinIO wrapper)
├── search/          # Search module (Lucene index & query)
├── user/            # User module (entity, converter, auth config)
├── docs/            # Documentation (third-party integration guide)
├── docker/          # Docker Compose (Milvus, etc.)
└── docker-compose-all.yml  # Full-stack one-click deployment
```

---

## Quick Start

### Option 1: Docker One-Click Deployment (Recommended)

> [!IMPORTANT]
> Build the backend jar before deployment.

```bash
# 1. Build backend
cd forum && mvn package -DskipTests && cd ..

# 2. Start all services (MySQL + Redis + MinIO + Milvus + backend + frontend)
docker-compose -f docker-compose-all.yml up -d

# 3. View logs
docker-compose -f docker-compose-all.yml logs -f
```

<details>
<summary>📁 Offline Image Export / Import</summary>

```bash
# Export images (online environment)
save_images.bat          # Windows
# Or manually: docker save -o <name>.tar <image>

# Import images (offline environment)
cd images && ./load_image.sh   # macOS / Linux
```

</details>

Visit `http://localhost` after startup.

### Option 2: Local Development

<details>
<summary>🔧 Expand local development steps</summary>

#### Prerequisites

- JDK 21
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis
- MinIO (optional, required for file upload)
- Milvus (optional, required for AI RAG)

#### 1. Initialize Database

```sql
CREATE DATABASE tforum DEFAULT CHARACTER SET utf8mb4;
```

On startup, `db/schema.sql` and `db/data.sql` execute automatically to create tables and seed data.

#### 2. Start Backend

```bash
mvn clean compile -pl forum -am
cd forum
mvn spring-boot:run
```

Default port: `8081`

#### 3. Start Frontend

```bash
cd forum/frontend
npm install
npm run dev
```

Default port: `3000`, API requests proxy to `localhost:8081`

</details>

### Access URLs

| URL | Description |
|-----|-------------|
| `http://localhost` | Frontend (Docker deployment) |
| `http://localhost/admin` | Admin console |
| `http://localhost:8081/swagger-ui.html` | Swagger API docs |
| `http://localhost:9001` | MinIO console (minioadmin / minioadmin) |

### Default Account

| Account | Password | Role |
|---------|----------|------|
| admin | admin123 | Administrator |

> [!WARNING]
> Change the default admin password in production.

---

## API Overview

| Path | Description |
|------|-------------|
| `/api/v1/user/*` | User (register, login, info, token verification) |
| `/api/v1/article/*` | Articles (CRUD, search, like, tags) |
| `/api/v1/forum/post/*` | Forum posts (CRUD, list) |
| `/api/v1/forum/comment/*` | Comments |
| `/api/v1/markdown/*` | Markdown documents |
| `/api/v1/book/*` | Books (upload, download) |
| `/api/v1/skill/*` | Skills (publish, download) |
| `/api/v1/nav/*` | Navbar (public list) |
| `/api/v1/oss/*` | File management (upload, proxied download) |
| `/api/v1/search` | Global search |
| `/api/v1/ai/*` | AI chat (simple, streaming, RAG, MCP) |
| `/api/v1/vector/*` | Vector storage & retrieval |
| `/api/v1/milvus/*` | Milvus collection management |
| `/api/v1/admin/*` | Admin console (incl. navbar management) |

---

## Third-Party App Integration

tForum supports SSO integration with third-party apps via navbar external links + token placeholder + token verification API.

**Flow**: Admin configures external link `https://app.com/sso?token={token}` -> User click auto-replaces placeholder with real token -> Third-party app calls `GET /api/v1/user/verifyToken?token=xxx` to verify and retrieve user info.

Full tutorial: **[Third-Party App Integration Guide](docs/第三方应用集成方案.md)**.

---

## Contributing

1. Fork this repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'feat: add your feature'`
4. Push the branch: `git push origin feature/your-feature`
5. Submit a Pull Request

---

## License

This project is licensed under the [MIT License](LICENSE).

Copyright (c) 2026 shiyi
