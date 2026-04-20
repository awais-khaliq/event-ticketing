# High-Concurrency Event Ticketing System

A fast, robust Java Spring Boot application simulating a high-concurrency ticket booking platform (like Ticketmaster or Eventbrite). Instead of typical library systems, this project dives into handling race conditions natively on the database layer and comes with a beautiful, bright, responsive UI out-of-the-box.

## Features

- **High-Concurrency Seat Booking**: Utilizes atomic row-level SQL updates instead of read-then-write transactions. This eliminates the race conditions where two users buy the last ticket at the exact same millisecond. 
- **Bright & Modern UI**: A completely custom, vanilla CSS/JS frontend packed directly into the Spring architecture (`src/main/resources/static`). Glassmorphism, CSS blob animations, and live polling.
- **In-Memory Database**: Uses H2 database so that anyone cloning this repository can run it immediately without needing to set up Docker or PostgreSQL.
- **Real-Time Simulation**: The frontend polls the backend and visually shakes red when tickets are running critically low.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3, Spring Web, Spring Data JPA
- **Database**: H2 (In-memory, easily swappable to PostgreSQL)
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (Fetch API)

## How to Run

1. Ensure you have **Java 17** and **Maven** installed.
2. Clone this repository.
3. Open your terminal in the root directory and run:

```bash
mvn spring-boot:run
```

4. Open your browser and navigate to: `http://localhost:8080`.
(You will instantly see the vibrant booking UI and can start clicking!)

## Architecture & Code Highlights (for Technical Reviewers)

### 1. Concurrency Handling
Look at `EventRepository.java`. Instead of retrieving the event, subtracting seats in Java, and saving it (which causes over-booking under load), we employ an atomic `@Modifying` query:
```java
@Modifying
@Query("UPDATE Event e SET e.availableSeats = e.availableSeats - :quantity WHERE e.id = :eventId AND e.availableSeats >= :quantity")
int decrementAvailableSeats(@Param("eventId") Long eventId, @Param("quantity") int quantity);
```
If the database engine (which inherently handles its own row-locks during updates) updates 0 rows, we safely throw an exception in `EventService.java` without a single ticket being double sold.

### 2. Live Polling
Look at `script.js`. We run `fetchEvents()` via `setInterval` to demonstrate live ticket count updates across multiple connected clients without configuring full STOMP web sockets.

## Future Enhancements
- Swap H2 for PostgreSQL and run via `docker-compose.yml`.
- Add RabbitMQ to place users in a "virtual queue" when traffic spikes heavily.
- Add Spring Security for JWT authentication.
