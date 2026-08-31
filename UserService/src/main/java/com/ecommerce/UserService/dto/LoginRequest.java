package com.ecommerce.UserService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @Email
    @NotBlank
    @Schema(
            description = "User's registered email address",
            example = "john@example.com"
    )
    private String email;

    @NotBlank
    @Schema(
            description = "User's password",
            example = "Password@123"
    )
    private String password;
}

