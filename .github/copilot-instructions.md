# Copilot Instructions for projetoback

## Project Overview
- This is a Spring Boot REST API for managing users, clients, technicians, specialties, and regions.
- Main entry: `ProjetobackApplication.java`.
- Follows standard layered architecture: controller → service → repository → model.
- Security is enforced via JWT and BCrypt; endpoints are protected and require authentication.

## Directory Structure
- `controller/`: REST endpoints for each domain (Usuário, Cliente, Técnico, Especialidade, Região).
- `service/`: Business logic, validation, and cross-entity operations.
- `repository/`: JPA repositories for persistence.
- `model/`: JPA entities.
- `security/`: JWT, password encryption, and authentication config.
- `config/`: Spring and CORS configuration.
- `dto/`: Data Transfer Objects for requests/responses.
- `exceptions/`: Custom exception handling.
- `docs/`: API documentation and frontend integration examples.

## Developer Workflows
- **Run locally:** `mvn spring-boot:run`
- **Run tests:** `mvn test`
- **Build JAR:** `mvn clean package`
- **Default port:** 8083
- **Initialize default data:**
  - `POST /especialidade/initialize`
  - `POST /regiao/initialize`

## Patterns & Conventions
- Use DTOs for all input/output in controllers; never expose entities directly.
- Exception handling is centralized in `exceptions/`.
- Passwords are always encrypted with BCrypt.
- JWT tokens are required for all protected endpoints.
- CPF validation is enforced for clients and technicians.
- Status fields (e.g., `statusTecnico`) are used for soft deletes and filtering.
- All repositories extend `JpaRepository<Entity, Long>` and use method naming conventions for queries.

## Integration Points
- SQL Server is the default database (see `application.properties`).
- Frontend integration examples for Flutter and React in `docs/examples/`.
- Password reset flows are implemented for both web and mobile clients.

## Key Files
- `application.properties`: Database and security config.
- `API_DOCUMENTATION.md`: Endpoint details and usage.
- `controller/`, `service/`, `repository/`, `model/`: Core business logic and data flow.
- `security/`: JWT and authentication setup.

## Example: Adding a New Entity
1. Create JPA entity in `model/`.
2. Create repository in `repository/`.
3. Create service in `service/`.
4. Create controller in `controller/`.
5. Add DTOs and exception handling as needed.

---
If any section is unclear or missing, please provide feedback to improve these instructions.
