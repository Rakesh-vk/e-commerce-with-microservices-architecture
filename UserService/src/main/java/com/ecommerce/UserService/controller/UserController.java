package com.ecommerce.UserService.controller;

import com.ecommerce.UserService.dto.LoginRequest;
import com.ecommerce.UserService.dto.UserRegisterRequest;
import com.ecommerce.UserService.dto.UserResponse;
import com.ecommerce.UserService.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User APIs", description = "APIs for user registration, login, and secured endpoint testing")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Operation(summary = "Register user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {

        log.info("Registering user with email: {}", request.getEmail());

        UserResponse response = userServiceImpl.register(request);

        log.info("User registered successfully with email: {}", response.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        String token = userServiceImpl.login(request);

        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(
                Map.of("accessToken", token)
        );
    }

    @Operation(summary = "Test secured endpoint", description = "Tests whether the authenticated user can access a secured endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accessed secured endpoint"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Secured test endpoint accessed successfully");

        return ResponseEntity.ok("secured");
    }
}