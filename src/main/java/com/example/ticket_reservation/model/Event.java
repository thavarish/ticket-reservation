package com.example.ticket_reservation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String name;

    private String description;

    private String venue;

    private LocalDateTime eventDate;

    private int totalSeats;

    private int availableSeats;

    private BigDecimal ticketPrice;

    private String organizerUsername;
}