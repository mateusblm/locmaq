# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

- **Build:** `./mvnw clean package` (or `./mvnw clean package -DskipTests`)
- **Run:** `./mvnw spring-boot:run`
- **Run tests:** `./mvnw test`
- **Run single test:** `./mvnw test -Dtest=TestClassName#methodName`

Requires Java 21 and MySQL 8.0 running on `localhost:3306` with database `locmaq` (user: `root`, password: `toor`).

## Architecture

Spring Boot 3.4.4 application for equipment rental management (locação de máquinas). Layered architecture:

**Controllers** (`controllers/`) → **Services** (`services/`) → **Repositories** (`repositories/`) → **Models** (`models/`)

Base package: `io.github.mateusbm.locmaq`

### Domain Model

- **Usuario** — users with roles: PLANEJADOR, LOGISTICA, GESTOR
- **Cliente** / **Dono** — lessees and equipment owners (validated CPF/CNPJ)
- **Equipamento** — rental machines, linked to a Cliente and a Dono
- **ContratoLocacao** — rental contracts with period conflict detection via custom JPQL
- **Orcamento** — dual budgets (CLIENTE and DONO types) with margin/discount calculation
- **BoletimMedicao** — measurement bulletins that can be signed (immutable once signed)
- **ActionLog** — audit trail for all state-changing operations

### Key Patterns

- **Constructor injection** everywhere (no `@Autowired` on fields)
- **DTOs** in `dto/` for API responses (denormalized views)
- **ValidadorUtil** — static validation for CPF, CNPJ, bank account, email, discount rules
- **Dual budget system** — creating an Orcamento generates both client and owner quotes
- **Period conflict detection** — `ContratoLocacaoRepository` has a custom `@Query` to prevent overlapping rentals
- **ActionLogService** — all services log mutations for audit compliance

### Security

- Spring Security with `CustomUserDetailsService`, BCrypt passwords
- Default admin created on startup: username `admin`, password `root`, role GESTOR
- CSRF disabled (JSON API)
- Public paths: `/index.html`, `/css/**`, `/js/**`, `/imgs/**`, `/html/**`

### Frontend

Static HTML/CSS/JS served from `src/main/resources/static/`. Views in `static/html/`, one per role dashboard plus CRUD pages. No frontend build step.

## Language & Conventions

- All code, comments, variable names, and API responses are in **Brazilian Portuguese**
- Commit messages use semantic prefixes: `refactor:`, `fix:`, `feature:`
- `@Transactional` on state-changing service methods
- Services throw `ResponseStatusException` for error handling
