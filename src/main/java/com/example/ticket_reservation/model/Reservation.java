package com.example.ticket_reservation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;

    private String reservationCode;

    private String customerUsername;

    private String eventId;

    private int seats;

    private BigDecimal amount;

    private ReservationStatus status;

    private LocalDateTime createdAt;
}