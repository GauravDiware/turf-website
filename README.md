# 🏟️ BookMyTurf — Turf Booking Backend

BookMyTurf is a **Spring Boot REST API backend** for a sports turf booking platform.  
The application allows users to register, log in, browse turf-related services, and manage booking-related operations through secure REST APIs.

The project is designed with a scalable backend architecture using **Java, Spring Boot, Spring Data JPA, PostgreSQL, and REST APIs**.

---

## 🚀 Project Overview

BookMyTurf aims to simplify the process of discovering and booking sports facilities such as:

- 🏏 Cricket
- ⚽ Football
- 🏸 Badminton
- 🎾 Tennis
- 🏀 Basketball
- 🏓 Pickleball
- 🏊 Swimming

The backend provides APIs that can be consumed by a web or mobile frontend.

---

## ✨ Features

### 👤 User Management
- User registration
- User login
- User authentication
- Role-based user management
- Password encryption using BCrypt
- UUID-based user identification

### 🏟️ Turf & Sports Management
- Sports category management
- Turf-related API structure
- City-based turf discovery
- Support for multiple sports

### 📅 Booking System
- Booking-oriented backend architecture
- User-specific booking operations
- Booking validation and business logic
- Designed for future payment gateway integration

### 🔐 Security
- Spring Security
- BCrypt password encryption
- Role-based authorization
- Protected REST endpoints

### 🗄️ Database
- PostgreSQL
- Spring Data JPA
- Hibernate ORM
- Entity relationships
- Repository-based data access

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 17 | Backend development |
| Spring Boot | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Maven | Dependency management |
| REST API | Client-server communication |
| Git & GitHub | Version control |

---

## 🏗️ Architecture

The application follows a layered architecture:

```text
Client
   │
   ▼
REST Controller
   │
   ▼
Service Layer
   │
   ▼
Repository Layer
   │
   ▼
PostgreSQL Database
