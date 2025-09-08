# Project Planner Beta
A simple spring boot project to manage project scheduling.

## Features
- create, update, and delete projects
- create, update and delete tasks
- assign dependencies between tasks
- automatically calculates project duration and sort tasks based on dependencies

## Tech Stack
- Backend: Java 17 Spring boot 3
- Database: H2/ Mysql / PostgreSQL (can configure in application.properties)
- Api Testing: Postman (sample request included in '/docs')

## Build and Run
./mvnw clean install
/.mvn spring-boot:run

The application will start on 
http://localhost:8080

Access database
http://localhost:8080/h2-console
