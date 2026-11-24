package com.example.ticket_reservation.controller;

import com.example.ticket_reservation.dto.StatsResponse;
import com.example.ticket_reservation.dto.UserResponse;

import com.example.ticket_reservation.service.AdminService;
import com.example.ticket_reservation.service.AuthService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(
            AdminService adminService,
            AuthService authService) {

        this.adminService = adminService;
        this.authService = authService;
    }

    // View all users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                authService.getAllUsers()
        );
    }

    // View system statistics
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStatistics() {

        return ResponseEntity.ok(
                adminService.getStatistics()
        );
    }
}