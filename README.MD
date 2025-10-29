# LetsPlay Project

## Overview

LetsPlay is a Java Spring Boot REST API project implementing user and product management with authentication and authorization powered by JWT. It uses MongoDB for data persistence. The project supports typical operations such as user registration, login, profile management, and CRUD operations for products. It employs role-based access control with roles "USER" and "ADMIN" for secure endpoint access.

***

## Features

- **User Authentication**: JWT-based login and role assignment.
- **Role-Based Authorization**: Protect endpoints by roles USER or ADMIN.
- **User Management**: Admin can create, update, promote, and delete users.
- **Product Management**: Authenticated users can create, update, delete their products; public can view products.
- **Validation and Exception Handling**: Input validation and centralized error handling.
- **Security**: Stateless sessions, JWT filter, strong password encoding.
- **CORS Support**: Configured for secure, restricted front-end access.

***

## Endpoints
[API Documentation](API-Documentation.md)


### Authentication

- `POST /api/auth/login` — User login and JWT token retrieval
- `POST /api/auth/register` — User self-registration (if implemented)

### Users

- `GET /api/users` — ADMIN only: list all users
- `GET /api/users/{id}` — Get user by ID (ADMIN or self depending on config)
- `GET /api/users/me` — Authenticated user’s own profile
- `POST /api/users/create` — ADMIN only: create new user
- `PUT /api/users/update/{id}` — Authenticated USER/ADMIN: update user profile or admin edits any user
- `PUT /api/users/promote/{id}` — ADMIN only: promote user roles
- `DELETE /api/users/{id}` — ADMIN only: delete user

### Products

- `GET /api/products` — Public: list all products
- `GET /api/products/{id}` — Public: get product by ID
- `GET /api/users/{userId}/products` — Public: get all products for a user
- `POST /api/products` — Authenticated USER/ADMIN: create product
- `PUT /api/products/{id}` — Authenticated USER/ADMIN: update product (own or admin)
- `DELETE /api/products/{id}` — Authenticated USER/ADMIN: delete product (own or admin)

***

## Technology Stack

- Java 17+
- Spring Boot 3.x
- Spring Security with JWT
- MongoDB and Spring Data MongoDB
- Jakarta Validation API (`@Valid`, `@NotBlank`, etc.)
- Maven or Gradle build tool

***

## Setup Instructions

1. **Prerequisites**:
    - Java 17+ installed
    - MongoDB server running locally or remotely

2. **Clone the repository**:
   ```
   git clone https://github.com/01-edu/public.git
   cd public/subjects/java/projects/lets-play
   ```

3. **Configure MongoDB**  
   Adjust MongoDB connection settings in `application.properties` or `application.yml` file.

4. **Build and Run**
   ```
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

5. **Testing**  
   Run tests:
   ```
   ./mvnw test
   ```

6. **API Usage**
    - Use Postman
    - Register/login to obtain JWT token.
    - Include `Authorization: Bearer <token>` header for protected operations.

***

## Security Configuration Highlights

- Stateless JWT filter applied before username/password filter.
- Role-based endpoint protection via `hasAnyRole` rules.
- Public access to authentication and product browsing endpoints.
- CORS restricted to `https://localhost:8443`.
- Passwords hashed with BCrypt encoder.

***

## Project Structure Highlights

- `auth` package: JWT utilities and filters.
- `controller` package: RestControllers with endpoint mappings and role checks.
- `model` package: MongoDB entities with validation.
- `service` package: Business logic, validation, and repository interactions.
- `repository` package: Spring Data MongoDB repositories.
- `request` package defines Data Transfer Objects (DTOs) for incoming API request payloads with validation.
- `response` package defines DTOs shaping the API response objects.
- `exception` package: Custom exceptions and a global exception handler returning standardized JSON error responses.
- `config` package contains Spring Boot configuration classes including security and CORS setup.
***

## Notes

- Carefully maintain synchronization between your SecurityConfig roles and JWT authorities.
- Maintain strong validation in service methods to prevent 500 errors.
- Customize exceptions via `GlobalExceptionHandler` for consistent API error responses.

***
