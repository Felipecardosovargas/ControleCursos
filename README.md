<p align="center">
  <img src="https://d9hhrg4mnvzow.cloudfront.net/lp.3035tech.com/96c1669d-logo-teach-horiz-branco_1000000000000000000028.png" alt="3035tech Logo" width="150"/>
</p>

# ControleCursos — Course Management System

**Project developed as part of the full-stack training program by 3035tech.**

## Overview

This project is a **robust, scalable, and maintainable backend system** for managing courses, students, and enrollments in a language and skills school. Built with **Java**, **JPA**, and **PostgreSQL**, it aims to replace manual and error-prone administrative processes with a clean, professional, and extensible solution.

Designed with clear separation of concerns and architectural best practices, this project avoids the pitfalls of a “Big Ball of Mud” by applying modularization and layered design patterns — delivering real business value with maintainable code and extensibility in mind.

## Current Features

- Full CRUD operations for **Students**, **Courses**, and **Enrollments**
- **Relational mappings** using JPA annotations (`@OneToMany`, `@ManyToOne`)
- Clean separation into layers: **Controller**, **Service**, **Repository**, and **DTOs**
- Custom exception handling for validation and domain rules
- Simple console-based user interface enabling menu-driven interaction
- PostgreSQL persistence with automated schema generation via JPA
- Search functionality (e.g., find student by email, search courses by name)
- Enrollment listing with relational data display (student and course names)

## Architecture & Design

- **Layered Architecture** (Controller → Service → Repository → Database)
- Domain-driven design principles with clearly defined entity models
- Use of Data Transfer Objects (DTOs) to decouple API from domain entities
- Exception handling tailored to domain constraints (`EntityNotFoundException`, `ValidationException`)
- Avoidance of anti-patterns by keeping each layer focused on its responsibility
- Emphasis on delivering business value through clean, maintainable, and testable code

## Bonus Challenge (Optional)

Implemented an advanced **Engagement Report** for each course, displaying:

- Total number of enrolled students
- Average age of students per course
- Number of students enrolled in the last 30 days

## Technologies Used

- **Java 17+**
- **JPA / Hibernate**
- **PostgreSQL**
- **Maven** for dependency management
- **Jackson** for JSON serialization (in utilities)
- Basic console interaction with `Scanner`

## Future Improvements and Roadmap

- **Implement user cardinality and roles** for better user management and permissions, reorganizing domain models accordingly
- **Migrate backend to Spring Boot** to leverage modern dependency injection, security, REST APIs, and better modularization
- **Develop and release an SDK** for API consumption, compatible with multiple programming languages and frameworks, potentially containerized with Docker for ease of deployment
- **Build a modern frontend** using:
  - **Angular + Sparta UI** for a consistent and accessible UI component library, or
  - **React + Tailwind CSS + shadcn/ui** for a flexible, highly customizable interface with modern styling and components
- Implement OAuth2 and JWT-based authentication and authorization
- Add API documentation with Swagger / OpenAPI for seamless integration
- Introduce automated testing, CI/CD pipelines, and container orchestration for production readiness

## Getting Started

### Prerequisites

- Java 17 or later installed
- PostgreSQL database setup
- Maven installed

### Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/Felipecardosovargas/ControleCursos.git
   cd ControleCursos
