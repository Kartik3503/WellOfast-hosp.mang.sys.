# 🏥 Birth Certificate Generator

A Java Spring Boot application for hospital-authorized birth certificate generation with PDF output and MongoDB authentication.

## Features

- **Hospital Authentication** — Secure login/registration via MongoDB with BCrypt password encryption
- **Birth Certificate Form** — Comprehensive form covering child, parent, doctor, and informant details
- **PDF Generation** — Professional government-style PDF certificates using iText 8
- **Certificate History** — View, search, and download all previously issued certificates
- **Hospital Dashboard** — Stats overview with recent activity
- **Responsive Design** — Premium dark-themed UI that works on all devices

## Tech Stack

| Component | Technology |
|-----------|------------|
| Backend | Java 17+ / Spring Boot 3.2 |
| Frontend | Thymeleaf + Vanilla CSS |
| Database | MongoDB |
| Security | Spring Security (BCrypt) |
| PDF Engine | iText 8 |
| Build | Maven |

## Prerequisites

1. **Java 17+** installed (Java 20 detected on this system)
2. **MongoDB** running on `localhost:27017`
   - Download: https://www.mongodb.com/try/download/community
   - Or use Docker: `docker run -d -p 27017:27017 --name mongodb mongo:latest`
3. **Maven** (or use the included `mvnw.cmd` wrapper)

## Quick Start

### 1. Start MongoDB
```bash
# If installed locally
mongod

# Or with Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 2. Build & Run
```bash
# Using Maven wrapper (no Maven install needed)
mvnw.cmd spring-boot:run

# Or with Maven installed
mvn spring-boot:run
```

### 3. Access the Application
Open your browser: **http://localhost:8080**
And for the live access of the web application access the link:https://wellofast.onrender.com 

### Demo Account
A demo hospital account is auto-created on first run:
- **Username:** `admin`
- **Password:** `admin123`

## Project Structure

```
src/main/java/com/birthcertificate/
├── BirthCertificateApplication.java    # Main entry point
├── config/
│   ├── SecurityConfig.java             # Spring Security setup
│   └── DataInitializer.java            # Demo data seeder
├── model/
│   ├── Hospital.java                   # Hospital entity
│   └── BirthCertificate.java           # Certificate entity
├── repository/
│   ├── HospitalRepository.java         # Hospital MongoDB queries
│   └── BirthCertificateRepository.java # Certificate MongoDB queries
├── service/
│   ├── CustomUserDetailsService.java   # Auth service
│   ├── HospitalService.java            # Hospital business logic
│   ├── BirthCertificateService.java    # Certificate business logic
│   └── PdfGeneratorService.java        # PDF generation
└── controller/
    ├── AuthController.java             # Login/Register routes
    ├── DashboardController.java        # Dashboard & list routes
    └── CertificateController.java      # Certificate CRUD & PDF download
```

## MongoDB Configuration

Default connection: `mongodb://localhost:27017/birth_certificate_db`

To change, edit `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb://your-host:27017/your-db-name
```
