
package com.example.ticket_reservation.controller;

import com.example.ticket_reservation.dto.ReservationRequest;
import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.model.Role;
import com.example.ticket_reservation.service.ReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Customer books tickets
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Reservation> bookTickets(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        reservationService.bookTickets(
                                request,
                                authentication.getName()
                        )
                );
    }

    // Customer views own reservations
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Reservation>> getMyReservations(
            Authentication authentication) {

        return ResponseEntity.ok(
                reservationService.getMyReservations(
                        authentication.getName()
                )
        );
    }

    // ADMIN views all; ORGANIZER views own event reservations
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<List<Reservation>> getReservations(
            Authentication authentication) {

        Role role = getUserRole(authentication);

        return ResponseEntity.ok(
                reservationService.getManagedReservations(
                        authentication.getName(),
                        role
                )
        );
    }

    // Customer cancels own reservation; ADMIN cancels any
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable String id,
            Authentication authentication) {

        Role role = getUserRole(authentication);

        reservationService.cancelReservation(
                id,
                authentication.getName(),
                role
        );

        return ResponseEntity.noContent().build();
    }

    private Role getUserRole(Authentication authentication) {

        return Role.valueOf(
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "")
        );
    }
}
