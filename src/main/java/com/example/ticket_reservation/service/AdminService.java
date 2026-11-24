package com.example.ticket_reservation.service;

import com.example.ticket_reservation.dto.StatsResponse;

import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.model.ReservationStatus;

import com.example.ticket_reservation.repository.UserRepository;
import com.example.ticket_reservation.repository.EventRepository;
import com.example.ticket_reservation.repository.ReservationRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public AdminService(
            UserRepository userRepository,
            EventRepository eventRepository,
            ReservationRepository reservationRepository) {

        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
    }

    public StatsResponse getStatistics() {

        long totalUsers = userRepository.count();

        long totalEvents = eventRepository.count();

        List<Reservation> confirmedReservations =
                reservationRepository.findByStatus(
                        ReservationStatus.CONFIRMED
                );

        long ticketsSold = confirmedReservations.stream()
                .mapToLong(Reservation::getSeats)
                .sum();

        BigDecimal totalRevenue = confirmedReservations.stream()
                .map(Reservation::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return new StatsResponse(
                totalUsers,
                totalEvents,
                ticketsSold,
                totalRevenue
        );
    }
}