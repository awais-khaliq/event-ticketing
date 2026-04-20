package com.example.ticketing.repository;

import com.example.ticketing.model.TicketReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketReservationRepository extends JpaRepository<TicketReservation, Long> {
}
