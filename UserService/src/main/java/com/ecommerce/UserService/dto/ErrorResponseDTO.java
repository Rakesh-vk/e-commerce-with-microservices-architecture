package com.ecommerce.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Schema(description = "Standard error response returned when an API request fails")
public class ErrorResponseDTO {

    private int status;

    private String message;

    private LocalDateTime timestamp;

    private Map<String, String> errors;

    public ErrorResponseDTO(
            int status,
            String message,
            LocalDateTime timestamp
    ) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponseDTO(
            int status,
            String message,
            LocalDateTime timestamp,
            Map<String, String> errors
    ) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }
}