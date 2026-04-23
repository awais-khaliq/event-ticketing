# TicketHive: High-Concurrency Ticketing Backend

A Spring Boot backend application built to handle high-traffic, concurrent booking scenarios where thousands of users attempt to reserve the same seats simultaneously.

## Project Overview

Standard CRUD applications often struggle under heavy concurrent load, leading to race conditions like oversold inventory. This project tackles the problem head-on using database-level pessimistic locking.

Instead of a basic "buy instantly" flow, this system mimics real-world enterprise ticketing platforms (like Ticketmaster). When a user selects a ticket, the system locks specific seats for 10 minutes, giving the user a checkout window. If payment isn't completed within that time, a background worker automatically releases the seats back into the available pool.

## Key Features

* **Pessimistic Seat Locking:** Uses Postgres/H2 row-level locks via `FOR UPDATE SKIP LOCKED` to safely grab available seats without deadlocks.
* **Asynchronous Expiration Workers:** Scheduled Spring tasks automatically detect and release expired reservations to keep inventory accurate.
* **Zero-Config Execution:** Uses an in-memory H2 database with pre-populated dummy data so you can clone and run it immediately.

## Technical Architecture

The core logic defers concurrency control to the database engine. In Spring Data JPA, this is achieved via the `@Lock(LockModeType.PESSIMISTIC_WRITE)` annotation alongside `SKIP LOCKED` query hints:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints({
    @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") // SKIP LOCKED
})
@Query("SELECT s FROM Seat s WHERE s.eventId = :eventId AND s.status = 'AVAILABLE'")
List<Seat> findAvailableSeatsForLocking(@Param("eventId") Long eventId, Pageable pageable);
```

This ensures that under heavy load, two concurrent threads will never read the exact same seat record, completely eliminating double-booking errors.

## How to Run Locally

Prerequisites: Java 17 and Maven.

1. Clone the repository and navigate to the project root:
   ```bash
   git clone <your-repo-url>
   cd event-ticketing
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. Access the service at `http://localhost:8080`.

## Future Enhancements
* Database transition from H2 to PostgreSQL via Docker Compose.
* Implementation of a message broker queue (RabbitMQ) to manage traffic spikes instead of direct database hits.
* JWT authentication for secure user integration.