package com.example.ticket_reservation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservationRequest(

        @NotBlank(message = "Event ID is required")
        String eventId,

        @Min(value = 1, message = "Minimum 1 seat required")
        @Max(value = 10, message = "Maximum 10 seats allowed")
        int seats

) {}