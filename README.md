# Team Leave Calendar

## How the Application Works
This web application manages team leave requests and calculates weekly on call rotations. The frontend is built with JavaScript, HTML, and CSS, communicating asynchronously with a Spring Boot Java API. Data is stored in a local SQLite database. 

## Tech Stack
- Backend: Java 25, Spring Boot 4, Maven
- Database: SQLite
- Frontend: JavaScript, HTML, CSS, Bootstrap

## Setup Instructions and Docker Setup

1. Ensure Docker Desktop is running on your machine.
2. Open a terminal in the root folder of the project.
3. Build and start the container by running:

```bash
docker compose up --build
```

4. Access the application in your browser at:

http://localhost:8080

---

## API Documentation

Interactive Swagger UI documentation is available locally once the container is running:

http://localhost:8080/swagger-ui/index.html

---

## API Endpoints

### Members
- GET /api/members : Get all team members

### Leave Requests
- GET /api/leaves : Get all leave requests (supports ?memberId= and ?status= filters)
- POST /api/leaves : Create a new leave request
- PATCH /api/leaves/{id}/status : Update leave request status
- GET /api/leaves/{id} : Get a specific leave request
- DELETE /api/leaves/{id} : Delete a leave request

### On Call Schedule
- GET /api/oncall?weeks=8 : Get the on call schedule

---

## Assumptions

- Overlapping leave validation only applies to active requests. Requests marked as REJECTED are ignored during conflict checks.
- Users can submit leave requests for dates in the past to allow for historical record keeping.
- The rotation schedule is calculated mathematically from a fixed anchor date rather than saving weekly assignments in the database.
- The application uses a fixed list of seeded team members. User authentication is not required.

---

## Optional Improvements Added

- Month View Calendar: A CSS Grid based calendar tab to visualize leaves and on call assignments.
- Advanced Filtering: Users can filter leave requests by team member or status directly from the API.
- Approval Workflow: UI actions allow for approving or rejecting requests dynamically without full page reloads.
- Docker Setup: Containerized environment with local volume mapping for SQLite persistence.
- REST API Documentation: Integrated Springdoc OpenAPI for interactive endpoint testing.
- Automated Tests: Unit tests are implemented for the core service layer logic to verify mathematical accuracy and validation rules.

---

## Features Not Completed

- Reason Display: The reason field is captured in the form and successfully saved to the database via the service layer, but it is currently not rendered in the frontend UI list.
- Automated Replacement Suggestions: The UI highlights schedule conflicts, but manual reassignment of on call duties is required.
- Comments on Leave Requests: Threaded discussion features were omitted to prioritize core requirements.
