package com.example.ticketing.repository;

import com.example.ticketing.model.Seat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") 
    })
    @Query("SELECT s FROM Seat s WHERE s.eventId = :eventId AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableSeatsForLocking(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT s FROM Seat s WHERE s.status = 'RESERVED' AND s.lockedUntil < CURRENT_TIMESTAMP")
    List<Seat> findExpiredSeats();
}
