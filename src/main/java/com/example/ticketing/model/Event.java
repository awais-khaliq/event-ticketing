package com.example.ticketing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;
    
    private Integer totalSeats;
    
    private Integer availableSeats;
    
    private BigDecimal price;

    private LocalDateTime date;
    
    // We can also include an @Version for JPA Optimistic Locking, 
    // but we will use an atomic query as it's cleaner to demo without exceptions.
}
