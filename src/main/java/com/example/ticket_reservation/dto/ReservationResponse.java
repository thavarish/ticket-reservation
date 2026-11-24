package com.example.ticket_reservation.dto;

import com.example.ticket_reservation.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(

        String id,

        String reservationCode,

        String customerUsername,

        String eventId,

        int seats,

        BigDecimal amount,

        ReservationStatus status,

        LocalDateTime createdAt

) {}