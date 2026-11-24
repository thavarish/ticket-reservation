package com.example.ticket_reservation.dto;

import com.example.ticket_reservation.model.Role;

public record UserResponse(

        String id,

        String username,

        String fullName,

        Role role

) {}