package com.example.ticket_reservation.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ErrorResponseWriter errorResponseWriter;

    public JsonAccessDeniedHandler(
            ErrorResponseWriter errorResponseWriter) {

        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        errorResponseWriter.write(
                response,
                403,
                "Access denied. Insufficient permissions."
        );
    }
}