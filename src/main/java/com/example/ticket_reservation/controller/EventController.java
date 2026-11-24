package com.example.ticket_reservation.controller;

import com.example.ticket_reservation.dto.EventRequest;
import com.example.ticket_reservation.dto.EventResponse;

import com.example.ticket_reservation.model.Role;

import com.example.ticket_reservation.service.EventService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // View all events
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    // View one event
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable String id) {

        return ResponseEntity.ok(
                eventService.getEventById(id)
        );
    }

    // Create event
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        eventService.createEvent(
                                request,
                                authentication.getName()
                        )
                );
    }

    // Update event
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody EventRequest request,
            Authentication authentication) {

        Role role = getUserRole(authentication);

        return ResponseEntity.ok(
                eventService.updateEvent(
                        id,
                        request,
                        authentication.getName(),
                        role
                )
        );
    }

    // Delete event - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable String id) {

        eventService.deleteEvent(id);

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