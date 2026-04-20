package com.example.ticketing.repository;

import com.example.ticketing.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Atomic update to handle high concurrency. 
    // This prevents double booking by ensuring we only decrement if availableSeats >= quantity.
    @Modifying
    @Query("UPDATE Event e SET e.availableSeats = e.availableSeats - :quantity WHERE e.id = :eventId AND e.availableSeats >= :quantity")
    int decrementAvailableSeats(@Param("eventId") Long eventId, @Param("quantity") int quantity);
}
