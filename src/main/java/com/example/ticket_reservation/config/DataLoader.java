package com.example.ticket_reservation.config;

import com.example.ticket_reservation.model.AppUser;
import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Reservation;
import com.example.ticket_reservation.model.Role;

import com.example.ticket_reservation.repository.UserRepository;
import com.example.ticket_reservation.repository.EventRepository;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(
            UserRepository userRepository,
            EventRepository eventRepository,
            PasswordEncoder passwordEncoder,
            MongoTemplate mongoTemplate) {

        return args -> {

            // Create the reservations collection if it does not exist yet
            if (!mongoTemplate.collectionExists(Reservation.class)) {
                mongoTemplate.createCollection(Reservation.class);
            }

            // Create ADMIN account
            if (!userRepository.existsByUsername("admin")) {

                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("System Administrator");
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            // Create ORGANIZER account
            if (!userRepository.existsByUsername("organizer")) {

                AppUser organizer = new AppUser();
                organizer.setUsername("organizer");
                organizer.setPassword(passwordEncoder.encode("organizer123"));
                organizer.setFullName("Event Organizer");
                organizer.setRole(Role.ORGANIZER);

                userRepository.save(organizer);
            }

            // Create CUSTOMER account
            if (!userRepository.existsByUsername("customer")) {

                AppUser customer = new AppUser();
                customer.setUsername("customer");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setFullName("Test Customer");
                customer.setRole(Role.CUSTOMER);

                userRepository.save(customer);
            }

            // Create sample event
            if (eventRepository.count() == 0) {

                Event event = new Event();
                event.setName("Sample Music Concert");
                event.setDescription("Sample event for Ticket Reservation Lab");
                event.setVenue("Colombo");
                event.setEventDate(LocalDateTime.now().plusDays(30));
                event.setTotalSeats(100);
                event.setAvailableSeats(100);
                event.setTicketPrice(new BigDecimal("1000"));
                event.setOrganizerUsername("organizer");

                eventRepository.save(event);
            }
        };
    }
}