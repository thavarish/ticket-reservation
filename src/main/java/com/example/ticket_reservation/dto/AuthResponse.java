package com.example.ticket_reservation.dto;

import com.example.ticket_reservation.model.Role;

public record AuthResponse(

        String token,

        String tokenType,

        long expiresInMs,

        String username,

        String fullName,

        Role role

) {}