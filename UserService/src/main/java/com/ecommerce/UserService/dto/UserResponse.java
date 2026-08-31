package com.ecommerce.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserResponse {

    @Schema(
            description = "Unique identifier of the user",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID id;

    @Schema(
            description = "User's email address",
            example = "john@example.com"
    )
    private String email;

    @Schema(
            description = "User's username",
            example = "john_doe"
    )
    private String name;

    @Schema(
            description = "Timestamp when the user was created",
            example = "2026-08-31T18:30:00"
    )
    private LocalDateTime createdAt;
}

