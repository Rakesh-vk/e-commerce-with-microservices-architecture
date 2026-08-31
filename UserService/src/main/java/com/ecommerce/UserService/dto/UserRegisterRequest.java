package com.ecommerce.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank
    @Schema(
            description = "Username of the new user",
            example = "john_doe"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(
            description = "Email address of the new user",
            example = "john@example.com"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(
            description = "Password for the new account",
            example = "Password@123"
    )
    private String passwordHash;

    public UserRegisterRequest(
            String username,
            String email,
            String passwordHash
    ) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}

