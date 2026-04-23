package com.example.ticketing.service;

import com.example.ticketing.model.Event;
import com.example.ticketing.model.Seat;
import com.example.ticketing.model.TicketReservation;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.SeatRepository;
import com.example.ticketing.repository.TicketReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public TicketReservation reserveTicket(Long eventId, String userEmail, int quantity) {
        // High Concurrency Lock:
        // Use pessimistic locking with SKIP LOCKED to safely grab requested seats.
        // This ensures two users don't get the same seat and prevents database deadlocks.
        
        List<Seat> availableSeats = seatRepository.findAvailableSeatsForLocking(eventId, PageRequest.of(0, quantity));
        
        if (availableSeats.size() < quantity) {
            throw new RuntimeException("Sorry, not enough seats available or event not found!");
        }

        TicketReservation reservation = new TicketReservation();
        reservation.setEventId(eventId);
        reservation.setUserEmail(userEmail);
        reservation.setQuantity(quantity);
        // Reservation is now pending until user completes payment within the 10 min window
        reservation.setStatus("PENDING_PAYMENT");
        reservation.setReservationTime(LocalDateTime.now());
        reservation = reservationRepository.save(reservation);

        LocalDateTime lockExpiration = LocalDateTime.now().plusMinutes(10);
        for (Seat seat : availableSeats) {
            seat.setStatus("RESERVED");
            seat.setLockedUntil(lockExpiration);
            seat.setReservationId(reservation.getId());
            seatRepository.save(seat);
        }

        // Keep the total count updated for quick frontend reads
        eventRepository.decrementAvailableSeats(eventId, quantity);

        return reservation;
    }
}
