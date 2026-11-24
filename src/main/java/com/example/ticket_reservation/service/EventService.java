package com.example.ticket_reservation.service;

import com.example.ticket_reservation.dto.EventRequest;
import com.example.ticket_reservation.dto.EventResponse;

import com.example.ticket_reservation.exception.ApiException;

import com.example.ticket_reservation.model.AppUser;
import com.example.ticket_reservation.model.Event;
import com.example.ticket_reservation.model.Role;

import com.example.ticket_reservation.repository.EventRepository;
import com.example.ticket_reservation.repository.ReservationRepository;
import com.example.ticket_reservation.repository.UserRepository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public EventService(
            EventRepository eventRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            MongoTemplate mongoTemplate) {

        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public EventResponse createEvent(
            EventRequest request,
            String username) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        Event event = new Event();

        event.setName(request.name());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setEventDate(request.eventDate());
        event.setTotalSeats(request.totalSeats());
        event.setAvailableSeats(request.totalSeats());
        event.setTicketPrice(request.ticketPrice());

        event.setOrganizerUsername(
                user.getUsername()
        );

        return convertToResponse(
                eventRepository.save(event)
        );
    }

    public List<EventResponse> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public EventResponse getEventById(String id) {

        Event event = findEvent(id);

        return convertToResponse(event);
    }

    public List<EventResponse> getEventsByOrganizer(
            String username) {

        return eventRepository.findByOrganizerUsername(username)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public EventResponse updateEvent(
            String id,
            EventRequest request,
            String username,
            Role role) {

        Event event = findEvent(id);

        if (role == Role.ORGANIZER &&
                !event.getOrganizerUsername().equals(username)) {

            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own events"
            );
        }

        int bookedSeats =
                event.getTotalSeats() - event.getAvailableSeats();

        if (request.totalSeats() < bookedSeats) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Total seats cannot be less than already booked seats"
            );
        }

        event.setName(request.name());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setEventDate(request.eventDate());
        event.setTotalSeats(request.totalSeats());

        event.setAvailableSeats(
                request.totalSeats() - bookedSeats
        );

        event.setTicketPrice(request.ticketPrice());

        return convertToResponse(
                eventRepository.save(event)
        );
    }

    public void deleteEvent(String id) {

        Event event = findEvent(id);

        if (reservationRepository.existsByEventId(id)) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Cannot delete an event with reservation records"
            );
        }

        eventRepository.delete(event);
    }

    // Atomically reduce available seats
    public void reserveSeats(String eventId, int seats) {

        Query query = new Query();

        query.addCriteria(
                Criteria.where("_id").is(eventId)
                        .and("availableSeats").gte(seats)
        );

        Update update = new Update()
                .inc("availableSeats", -seats);

        Event updatedEvent = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Event.class
        );

        if (updatedEvent == null) {

            if (!eventRepository.existsById(eventId)) {

                throw new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Event not found"
                );
            }

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Not enough available seats"
            );
        }
    }

    // Release seats when a reservation is cancelled
    public void releaseSeats(String eventId, int seats) {

        Query query = new Query(
                Criteria.where("_id").is(eventId)
                        .and("availableSeats").lte(
                                Integer.MAX_VALUE - seats
                        )
        );

        Update update = new Update()
                .inc("availableSeats", seats);

        Event updatedEvent = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Event.class
        );

        if (updatedEvent == null) {

            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Unable to release seats"
            );
        }
    }

    private Event findEvent(String id) {

        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                HttpStatus.NOT_FOUND,
                                "Event not found"
                        )
                );
    }

    private EventResponse convertToResponse(Event event) {

        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getVenue(),
                event.getEventDate(),
                event.getTotalSeats(),
                event.getAvailableSeats(),
                event.getTicketPrice(),
                event.getOrganizerUsername()
        );
    }
}