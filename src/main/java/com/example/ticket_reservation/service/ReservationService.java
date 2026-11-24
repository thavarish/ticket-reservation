
package com.example.ticket_reservation.service;

import com.example.ticket_reservation.dto.ReservationRequest;
import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.model.ReservationStatus;
import com.example.ticket_reservation.model.Role;
import com.example.ticket_reservation.repository.EventRepository;
import com.example.ticket_reservation.repository.ReservationRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            EventRepository eventRepository) {

        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
    }

    // Customer books tickets
    public Reservation bookTickets(
            ReservationRequest request,
            String customerUsername) {

        Event event = eventRepository.findById(request.eventId())
        .orElseThrow(() ->
                new IllegalArgumentException("Event not found"));

        int seats = request.seats();

        if (seats <= 0) {
            throw new IllegalArgumentException(
                    "Number of seats must be greater than zero");
        }

        if (event.getAvailableSeats() < seats) {
            throw new IllegalArgumentException(
                    "Not enough seats available");
        }

        // Calculate total amount
        BigDecimal amount = event.getTicketPrice()
                .multiply(BigDecimal.valueOf(seats));

        // Update available seats
        event.setAvailableSeats(
                event.getAvailableSeats() - seats);

        eventRepository.save(event);

        // Create reservation
        Reservation reservation = new Reservation();

        reservation.setReservationCode(
                UUID.randomUUID().toString());

        reservation.setCustomerUsername(customerUsername);
        reservation.setEventId(event.getId());
        reservation.setSeats(seats);
        reservation.setAmount(amount);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setCreatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    // Customer views own reservations
    public List<Reservation> getMyReservations(
            String customerUsername) {

        return reservationRepository
                .findByCustomerUsername(customerUsername);
    }

    // ADMIN views all; ORGANIZER views own event reservations
    public List<Reservation> getManagedReservations(
            String username,
            Role role) {

        if (role == Role.ADMIN) {
            return reservationRepository.findAll();
        }

        if (role == Role.ORGANIZER) {

            List<Event> events =
                    eventRepository.findByOrganizerUsername(username);

            List<Reservation> reservations = new ArrayList<>();

            for (Event event : events) {
                reservations.addAll(
                        reservationRepository.findByEventId(
                                event.getId()));
            }

            return reservations;
        }

        throw new IllegalArgumentException(
                "You are not authorized to view these reservations");
    }

    // Customer cancels own reservation; ADMIN cancels any
    public void cancelReservation(
            String reservationId,
            String username,
            Role role) {

        Reservation reservation =
                reservationRepository.findById(reservationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found"));

        if (role == Role.CUSTOMER &&
                !reservation.getCustomerUsername().equals(username)) {

            throw new IllegalArgumentException(
                    "You cannot cancel another customer's reservation");
        }

        if (role != Role.ADMIN && role != Role.CUSTOMER) {
            throw new IllegalArgumentException(
                    "You are not authorized to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Reservation is already cancelled");
        }

        Event event = eventRepository.findById(
                reservation.getEventId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        // Restore available seats
        event.setAvailableSeats(
                event.getAvailableSeats() + reservation.getSeats());

        eventRepository.save(event);

        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);

        reservationRepository.save(reservation);
    }
}
