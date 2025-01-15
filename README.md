## 1. Application Overview
A backend application designed for managing user accounts, memberships, and facility reservations within a club or similar institution. The application provides secure authentication, RESTful APIs for user and administrator operations, and seamless integration with PostgreSQL for database management. Built using **Spring Boot** and **JPA**.

## 2. Installation Instructions

- **Java 17**
- **Maven**
- **PostgreSQL**

### 2.1. Download and Run the PostgreSQL Server

### 2.2. Create a Database for the Application

### 2.3. Configure the Application

In the application configuration, set your database username and password.

#### In `application.properties`:
```properties
# Database connection settings
spring.datasource.driver-classname=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/clubsystem
spring.datasource.username=clubsystem (your username)
spring.datasource.password=postgres (your password)
```

## 3. Development Experience

Technologies we worked with and what we learned:

1. **Spring Boot**
   - Used for building RESTful APIs and server-side application development.

2. **Spring Security**
   - Implemented secure authentication, including admin role management.

3. **JPA/Hibernate**
   - Used for ORM (Object-Relational Mapping) to handle database interactions, create entities, and define operations and queries.

4. **PostgreSQL**
   - Managed data storage and executed SQL queries to handle database schema changes.

5. **Lombok**
   - Utilized to simplify code writing by automatically generating getters, setters, and constructors.

6. **Maven**
   - Managed project dependencies and built the application.

7. **REST API**
   - Developed RESTful endpoints to handle client requests and server responses.

8. **DTO (Data Transfer Objects)**
   - Created objects to transfer data between different layers of the application.

9. **Scheduled Tasks (Spring @Scheduled)**
   - Set up periodic tasks to run at scheduled intervals.

10. **Named Queries (JPA)**
    - Created reusable static queries for database operations.

11. **Criteria API**
    - Utilized for building dynamic queries in the database.

12. **Spring Transactions (@Transactional)**
    - Managed transactions to ensure proper data persistence.

13. **Validation (Bean Validation)**
    - Implemented data validation to enforce business rules and ensure data integrity.

14. **Custom Exceptions**
    - Developed custom exceptions to handle specific error scenarios.

15. **@NamedQuery and @OrderBy**
    - Used to simplify and structure database access.

16. **Integration Testing**
    - Performed tests in an isolated environment to verify application functionality.

17. **Git**
    - Managed version control and collaborated using Git.
