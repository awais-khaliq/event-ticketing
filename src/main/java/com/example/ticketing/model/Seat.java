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
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    
    private String seatNumber; // e.g., A1, A2
    
    // AVAILABLE, RESERVED, SOLD
    private String status = "AVAILABLE";
    
    private LocalDateTime lockedUntil;
    
    private Long reservationId;
}
