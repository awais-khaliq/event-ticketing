package com.example.ticketing.controller;

import com.example.ticketing.model.Event;
import com.example.ticketing.model.TicketReservation;
import com.example.ticketing.service.EventService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/{eventId}/reserve")
    public ResponseEntity<?> reserveTicket(
            @PathVariable Long eventId, 
            @RequestBody ReservationRequest request) {
        
        try {
            TicketReservation reservation = eventService.reserveTicket(eventId, request.getEmail(), request.getQuantity());
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // DTO for incoming request
    @Data
    static class ReservationRequest {
        private String email;
        private int quantity;
    }

    // DTO for error messages
    @Data
    static class ErrorResponse {
        private final String message;
    }
}
