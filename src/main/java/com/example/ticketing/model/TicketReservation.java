package com.example.ticketing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ticket_reservations")
public class TicketReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    
    private String userEmail;
    
    private Integer quantity;
    
    private String status; // e.g. PENDING, CONFIRMED, FAILED
    
    private LocalDateTime reservationTime;
}
