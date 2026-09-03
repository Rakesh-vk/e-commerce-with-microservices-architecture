package com.ecommerce.PaymentService.security;

import com.ecommerce.PaymentService.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication required. Please provide a valid token. ProductService",
                LocalDateTime.now()
        );
        log.debug("Authentication required. Please provide a valid token. product Service");


        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDTO));
    }
}