package com.example.ticket_reservation.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventRequest(

        @NotBlank(message = "Event name is required")
        String name,

        String description,

        @NotBlank(message = "Venue is required")
        String venue,

        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        LocalDateTime eventDate,

        @Min(value = 1, message = "Total seats must be at least 1")
        int totalSeats,

        @NotNull(message = "Ticket price is required")
        @DecimalMin(value = "0.0", message = "Ticket price cannot be negative")
        BigDecimal ticketPrice

) {}