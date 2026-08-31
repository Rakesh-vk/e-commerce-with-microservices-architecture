package com.ecommerce.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class ErrorResponseDTO {
    @Schema( description = "HTTP status code", example = "400" )
    private int status;
    @Schema( description = "Description of the error", example = "Invalid email format" )
    private String message;
    @Schema( description = "Time when the error occurred", example = "2026-08-31T18:30:00" )
    private LocalDateTime timestamp;
    public ErrorResponseDTO(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

}