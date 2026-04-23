package com.example.ticketing.service;

import com.example.ticketing.model.Seat;
import com.example.ticketing.model.TicketReservation;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.SeatRepository;
import com.example.ticketing.repository.TicketReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketExpirationWorker {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final TicketReservationRepository reservationRepository;

    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
    @Transactional
    public void releaseExpiredSeats() {
        List<Seat> expiredSeats = seatRepository.findExpiredSeats();

        for (Seat seat : expiredSeats) {
            // Revert seat to available
            seat.setStatus("AVAILABLE");
            seat.setLockedUntil(null);
            
            if (seat.getReservationId() != null) {
                // Mark the reservation as expired
                reservationRepository.findById(seat.getReservationId()).ifPresent(reservation -> {
                    if (!"EXPIRED".equals(reservation.getStatus()) && !"CONFIRMED".equals(reservation.getStatus())) {
                        reservation.setStatus("EXPIRED");
                        reservationRepository.save(reservation);
                    }
                });
                seat.setReservationId(null);
            }

            seatRepository.save(seat);

            // Increment event available seats to keep frontend updated
            eventRepository.incrementAvailableSeats(seat.getEventId(), 1);
        }
    }
}
