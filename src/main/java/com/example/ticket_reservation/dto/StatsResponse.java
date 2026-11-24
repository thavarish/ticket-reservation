package com.example.ticket_reservation.dto;

import java.math.BigDecimal;

public record StatsResponse(

        long totalUsers,

        long totalEvents,

        long ticketsSold,

        BigDecimal totalRevenue

) {}