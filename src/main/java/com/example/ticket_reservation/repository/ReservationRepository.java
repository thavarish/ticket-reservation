package com.example.ticket_reservation.repository;

import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.model.ReservationStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservationRepository
        extends MongoRepository<Reservation, String> {

    List<Reservation> findByCustomerUsername(String username);

    List<Reservation> findByEventId(String eventId);

    List<Reservation> findByStatus(ReservationStatus status);

    boolean existsByEventId(String eventId);

    long countByStatus(ReservationStatus status);
}