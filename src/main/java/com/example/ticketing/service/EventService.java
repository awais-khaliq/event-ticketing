package com.example.ticketing.service;

import com.example.ticketing.model.Event;
import com.example.ticketing.model.TicketReservation;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.TicketReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TicketReservationRepository reservationRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public TicketReservation reserveTicket(Long eventId, String userEmail, int quantity) {
        // High Concurrency highlight:
        // Instead of fetching the event, checking seats, and saving (which causes race conditions),
        // we execute a single atomic SQL UPDATE.
        
        int rowsUpdated = eventRepository.decrementAvailableSeats(eventId, quantity);
        
        if (rowsUpdated == 0) {
            throw new RuntimeException("Sorry, not enough seats available or event not found!");
        }

        // If the update succeeded, we know we safely secured the seats.
        TicketReservation reservation = new TicketReservation();
        reservation.setEventId(eventId);
        reservation.setUserEmail(userEmail);
        reservation.setQuantity(quantity);
        reservation.setStatus("CONFIRMED");
        reservation.setReservationTime(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }
}
