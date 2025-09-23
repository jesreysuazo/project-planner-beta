# Project Planner Beta v3

A full-stack project scheduling app built with **Spring Boot** and **Angular**. Easily manage projects, tasks, and dependencies with a responsive UI and backend logic.

---

## Features

- Create, update, and delete **projects**
- Create, update, and delete **tasks**
- Assign **dependencies** between tasks
- Automatically calculate **project duration**
- Sort tasks based on **dependency hierarchy**
- Interactive **Angular frontend** for seamless user experience

---

## Tech Stack

| Layer       | Technology                          |
|-------------|--------------------------------------|
| Backend     | Java 17, Spring Boot 3               |
| Frontend    | Angular 16+                          |
| Database    | H2 / MySQL / PostgreSQL *(configurable via `application.properties`)*
| API Testing | Postman *(sample requests in `backend/docs`)*

---

## Build and Run

### Backend (Spring Boot)

```bash
./mvnw clean install
./mvnw spring-boot:run

Runs at: http://localhost:8080
Access H2 Console: http://localhost:8080/h2-console

### Frontend (Angular)
```bash
cd frontend  # or wherever your Angular app is located
npm install
ng serve

Runs at: http://localhost:4200
