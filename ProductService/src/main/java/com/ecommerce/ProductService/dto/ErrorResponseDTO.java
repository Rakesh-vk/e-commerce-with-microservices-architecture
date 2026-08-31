package com.ecommerce.ProductService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

@Schema(description = "Standard error response returned when an API request fails")

public class ErrorResponseDTO {
    public ErrorResponseDTO(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    @Schema(description = "HTTP status code", example = "404")

    private int status;
    @Schema(description = "Error message", example = "Order not found with id: 7b1f5a2e-8c3a-4c91-b6f1-4f89251d9a12")

    private String message;
    @Schema(description = "Timestamp when the error occurred", example = "2026-08-31T21:30:00")

    private LocalDateTime timestamp;
}