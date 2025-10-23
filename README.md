# User Management API

A robust REST API built with Spring Boot for user registration, JWT authentication, and role-based access control.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Getting Started](#-getting-started)
- [JWT Authentication](#-jwt-authentication)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#️-database-schema)
- [Configuration](#️-configuration)
- [Project Structure](#-project-structure)
- [Key Features Explained](#-key-features-explained)
- [Additional Documentation](#-additional-documentation)

## ✨ Features

- **JWT Authentication**: Complete token-based authentication system
- **Role-Based Access Control**: Manage user roles (ADMIN, USER, MANAGER)
- **User Management**: CRUD operations with complete validation
- **Data Validation**: Input validation with custom error responses
- **Entity Auditing**: Automatic tracking of creation and modification timestamps
- **Password Encryption**: Secure password storage using BCrypt
- **MapStruct Integration**: Efficient DTO-Entity mapping
- **Database Seeding**: Auto-populate database with sample data in dev profile
- **Global Exception Handling**: Centralized error handling with meaningful responses
- **Stateless Sessions**: JWT enables scalability without server-side sessions
- **AOP Integration**: Aspect-Oriented Programming for logging, auditing, performance monitoring, and security tracking

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Web
  - Spring AOP
- **MySQL** / PostgreSQL
- **JWT (JSON Web Tokens)** - JJWT 0.12.6
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Object mapping
- **AspectJ** - Aspect-Oriented Programming
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

## 🔐 JWT Authentication

### Authentication Flow

1. **Login**: User sends credentials to `/api/auth/login`
2. **Token**: Server generates a JWT containing username and role
3. **Token Usage**: Client includes token in `Authorization: Bearer <token>` header for subsequent requests
4. **Validation**: Server validates token automatically on each request

### Login Example

**Request:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN",
  "userId": 1
}
```

### Using the Token

```bash
GET /api/users
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

The token contains:
- Username
- User role
- Issue date
- Expiration date (24 hours by default)

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/api/auth/login` | Login | Public |
| GET | `/api/auth/validate` | Validate token | Requires token |

### Users

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| POST | `/api/users` | Create user (registration) | Public |
| GET | `/api/users` | List all users | ADMIN |
| GET | `/api/users/{id}` | Get user by ID | Authenticated |
| GET | `/api/users/email/{email}` | Find by email | Authenticated |
| GET | `/api/users/username/{username}` | Find by username | Authenticated |
| GET | `/api/users/name/{name}` | Find by name | Authenticated |
| GET | `/api/users/role/{roleName}` | Find by role | Authenticated |
| PUT | `/api/users/{id}` | Update user | ADMIN |
| PATCH | `/api/users/self/{id}` | Update own profile | Authenticated |
| DELETE | `/api/users/{id}` | Delete user | ADMIN |

### Roles

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/roles` | List all roles | ADMIN |
| GET | `/api/roles/{idOrName}` | Get role by ID or name | ADMIN |
| GET | `/api/roles/name/{roleName}` | Get role by name | ADMIN |
| GET | `/api/roles/id/{id}` | Get role by ID | ADMIN |
| POST | `/api/roles` | Create new role | ADMIN |
| PATCH | `/api/roles/{id}` | Update role | ADMIN |
| DELETE | `/api/roles/{id}` | Delete role | ADMIN |

### User Registration Example

```bash
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123",
  "roleId": 2
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

### Error Response

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

**Test users:**
- Username: `admin` / Password: `admin123` (Role: ADMIN)
- Username: `user1` / Password: `password1` (Role: USER)
- Username: `manager1` / Password: `password1` (Role: MANAGER)

### JWT Configuration

```properties
jwt.secret=<your-secret-key>
jwt.expiration=86400000  # 24 hours in milliseconds
```

**⚠️ IMPORTANT**: For production:
- Generate a secure secret key (minimum 512 bits)
- Use environment variables instead of hardcoding the secret

### Security

Role-based access control configuration:
- `/api/auth/**` - Public (no authentication)
- `/api/users` POST - Public (registration)
- `/api/users` GET, PUT, DELETE - ADMIN only
- `/api/roles/**` - ADMIN only
- Other endpoints - Authenticated user

## 📁 Project Structure

```
src/main/java/com/antonia/dev/userapi/
├── aspect/              # AOP aspects for cross-cutting concerns
│   ├── LoggingAspect.java
│   ├── AuditAspect.java
│   ├── PerformanceAspect.java
│   └── SecurityAuditAspect.java
├── config/              # Application configuration
│   ├── AppConfig.java
│   └── DatabaseSeeder.java
├── controller/          # REST controllers
│   ├── AuthController.java
│   ├── UserController.java
│   └── RoleController.java
├── dto/                 # Data Transfer Objects
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── ValidationResponse.java
│   ├── common/
│   │   ├── DeleteResponse.java
│   │   └── ErrorResponse.java
│   ├── role/
│   │   └── RoleDTO.java
│   └── user/
│       ├── CreateUserRequest.java
│       ├── UserDTO.java
│       └── UserUpdateSelfRequest.java
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
├── security/            # Security and JWT
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   └── SecurityConfig.java
├── service/             # Business logic
│   ├── role/
│   │   ├── RoleService.java
│   │   └── RoleServiceImpl.java
│   └── user/
│       ├── UserService.java
│       └── UserServiceImpl.java
└── UserManagementApiApplication.java
```

## 🔍 Key Features Explained

### JWT Integration

- **JwtUtil**: Generates and validates JWT tokens with role included
- **JwtAuthenticationFilter**: Intercepts requests and validates tokens automatically
- **CustomUserDetailsService**: Loads users from database for Spring Security
- **Stateless Tokens**: No server-side state stored, fully scalable

### AOP Integration

Aspect-Oriented Programming for cross-cutting concerns without modifying business logic:

#### LoggingAspect
- Automatic logging of all service and controller method executions
- Tracks method parameters, execution time, and results
- Separate logging for different application layers

#### AuditAspect
- Complete audit trail for user operations (create, update, delete)
- Complete audit trail for role operations (create, update, delete) - ADMIN only
- Records who performed each operation and when
- Tracks failed operations for security analysis
- Includes user role in audit logs for role operations

#### PerformanceAspect
- Monitors execution time for all layers (repository, service, controller)
- Detects slow queries (> 1 second) and services (> 2 seconds)
- Automatic alerts for performance issues
- Helps identify bottlenecks proactively

#### SecurityAuditAspect
- Tracks successful and failed login attempts
- Records authentication failures with reasons (invalid credentials, account disabled, etc.)
- Monitors token validation attempts
- Audits password change attempts
- Enables detection of brute force attacks

**Benefits:**
- Clean separation of concerns
- No code duplication
- Centralized logging and auditing
- Easy to maintain and extend
- Production-ready audit logs in `logs/userapi-audit.log`

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
- Spring Security enabled
- JWT authentication fully functional
- Role-based authorization
- Passwords encrypted with BCrypt
- Tokens with 24-hour expiration

**Production Recommendations**:
- Generate secure JWT secret (use `openssl rand -base64 64`)
- Use environment variables for secrets
- Enable HTTPS
- Configure CORS properly
- Add rate limiting
- Implement token rotation
- Use secret managers (AWS Secrets Manager, Azure Key Vault, etc.)

## 📝 HTTP Status Codes

| Code | Meaning | When it occurs |
|------|---------|----------------|
| 200 | OK | Successful request |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | Invalid or expired token |
| 403 | Forbidden | No permissions for resource |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Server error |

## 🧪 Testing

### Basic Scenario

```bash
# 1. Register user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123",
    "roleId": 2
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'

# 3. Use received token in requests
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer <TOKEN_HERE>"
```

## 📊 Project Features

- ✅ Complete RESTful API
- ✅ JWT Authentication
- ✅ Role-based Authorization
- ✅ Full CRUD for users and roles
- ✅ Data validation
- ✅ Global error handling
- ✅ Entity auditing
- ✅ Database seeding for development
- ✅ BCrypt encrypted passwords
- ✅ Stateless sessions
- ✅ Layered architecture (Controller → Service → Repository)
- ✅ AOP for logging, auditing, and performance monitoring
- ✅ Complete security audit trail
- ✅ Automatic performance bottleneck detection

## 👤 Author

**Antonia**

## 📝 Additional Documentation

For more detailed information, see:
- [AOP Implementation Guide](AOP_IMPLEMENTATION.md) - Complete guide to Aspect-Oriented Programming implementation
- [JWT Setup Guide](JWT_SETUP.md)
- [API Testing Examples](API_TESTING_EXAMPLES.md)
- [Production Configuration](PRODUCCION_CONFIG.md)

## 📜 License

This project is available for educational and portfolio purposes.

---

**Note**: This is a portfolio project. For production use, additional security measures and configurations are required.
