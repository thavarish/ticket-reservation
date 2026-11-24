package com.example.ticket_reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(

        String id,

        String name,

        String description,

        String venue,

        LocalDateTime eventDate,

        int totalSeats,

        int availableSeats,

        BigDecimal ticketPrice,

        String organizerUsername

) {}