# User Management API

A robust REST API built with Spring Boot for user registration, authentication, and role-based access control.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#️-database-schema)
- [Configuration](#️-configuration)
- [Project Structure](#-project-structure)

## ✨ Features

- **User Management**: CRUD operations for users with validation
- **Role-Based Access Control**: Manage user roles (ADMIN, USER, MANAGER)
- **Data Validation**: Input validation with custom error responses
- **Entity Auditing**: Automatic tracking of creation and modification timestamps
- **Password Encryption**: Secure password storage using BCrypt
- **MapStruct Integration**: Efficient DTO-Entity mapping
- **Database Seeding**: Auto-populate database with sample data in dev profile
- **Global Exception Handling**: Centralized error handling with meaningful responses

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Web
- **MySQL** / PostgreSQL
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Object mapping
- **Maven** - Dependency management

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd userapi
   ```

2. **Configure the database**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE db_users;
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/db_users
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080/api`

## 📡 API Endpoints

### Users

| Method | Endpoint                         | Description          |
|--------|----------------------------------|----------------------|
| GET | `/api/users`                     | Get all users        |
| GET | `/api/users/{id}`                | Get user by ID       |
| GET | `/api/users/email/{email}`       | Get user by email    |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/name/{name}`         | Get users by name    |
| GET | `/api/users/role/{roleName}`     | Get users by role    |
| POST | `/api/users`                     | Create a new user    |
| PATCH | `/api/users/{id}`                | Update user (admin)  |
| PATCH | `/api/users/self/{id}`           | Update own profile   |
| DELETE | `/api/users/{id}`                | Delete user          |

### Roles

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/roles` | Get all roles |
| GET | `/api/roles/{idOrName}` | Get role by ID or name |
| GET | `/api/roles/name/{roleName}` | Get role by name |
| GET | `/api/roles/id/{id}` | Get role by ID |
| POST | `/api/roles` | Create a new role |
| PATCH | `/api/roles/{id}` | Update role |
| DELETE | `/api/roles/{id}` | Delete role |

### Request/Response Examples

#### Create User
```bash
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123",
  "roleName": "USER"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "roleName": "USER"
}
```

#### Error Response
```json
{
  "errors": [
    {
      "field": "email",
      "message": "Email already exists"
    }
  ]
}
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Roles Table
```sql
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255)
);
```

## ⚙️ Configuration

### Profiles

The application supports different profiles:

- **dev**: Development profile with database seeding
- **prod**: Production profile (configure as needed)

Activate a profile:
```properties
spring.profiles.active=dev
```

### Database Seeding (Dev Profile)

When running in `dev` profile, the database is automatically seeded with:
- 3 default roles: ADMIN, USER, MANAGER
- 10 sample users with different roles

### Security Configuration

Currently configured with:
- CSRF disabled
- All endpoints permit all (configure as needed for production)

**⚠️ Note**: Update `SecurityConfig.java` for production deployment.

## 📁 Project Structure

```
src/main/java/com/antonia/dev/userapi/
├── controller/          # REST controllers
│   ├── UserController.java
│   └── RoleController.java
├── dto/                 # Data Transfer Objects
│   ├── UserDTO.java
│   ├── RoleDTO.java
│   ├── CreateUserRequest.java
│   ├── UserUpdateSelfRequest.java
│   ├── DeleteResponse.java
│   └── ErrorResponse.java
├── entity/              # JPA entities
│   ├── User.java
│   ├── Role.java
│   └── Audit.java
├── exception/           # Custom exceptions
│   ├── UserNotFoundException.java
│   ├── RoleNotFoundException.java
│   └── UserValidationException.java
├── handler/             # Global exception handler
│   └── GlobalExceptionHandler.java
├── mapper/              # MapStruct mappers
│   ├── UserMapper.java
│   └── RoleMapper.java
├── repository/          # Data access layer
│   ├── UserRepository.java
│   └── RoleRepository.java
├── service/             # Business logic
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   ├── RoleService.java
│   └── RoleServiceImpl.java
├── DatabaseSeeder.java  # Database initialization
└── SecurityConfig.java  # Security configuration
```

## 🔍 Key Features Explained

### MapStruct Integration
Automatic DTO-Entity mapping with custom configurations:
- User to UserDTO with role name extraction
- Efficient mapping without boilerplate code

### Query Optimization
- Uses `JOIN FETCH` to prevent N+1 query problems
- Custom JPQL queries for complex operations

### Validation
- Bean Validation (Jakarta Validation)
- Custom validation in service layer
- Unique email and username constraints

### Exception Handling
- Global exception handler for consistent error responses
- Custom exceptions for domain-specific errors
- Field-level error reporting

## 🔐 Security Notes

**Current Configuration (Development)**:
- Basic Spring Security enabled
- All endpoints are publicly accessible
- Passwords encrypted with BCrypt

**Production Recommendations**:
- Implement JWT authentication
- Add role-based authorization
- Enable HTTPS
- Configure CORS properly
- Add rate limiting

## 📝 License

This project is available for educational and portfolio purposes.

## 👤 Author

**Antonia**

---

**Note**: This is a portfolio project. For production use, additional security measures and configurations are required.

