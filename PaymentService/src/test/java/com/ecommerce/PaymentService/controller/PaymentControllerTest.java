package com.ecommerce.PaymentService.controller;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.PaymentStatus;
import com.ecommerce.PaymentService.exception.GlobalExceptionHandler;
import com.ecommerce.PaymentService.exception.PaymentNotFoundException;
import com.ecommerce.PaymentService.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getPaymentDetails_shouldReturn200_whenPaymentExists() throws Exception {
        UUID paymentId = UUID.randomUUID();
        PaymentResponseDTO response = new PaymentResponseDTO(
                paymentId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("999.99"),
                PaymentStatus.SUCCESS,
                LocalDateTime.now()
        );

        when(paymentService.getDetails(paymentId)).thenReturn(response);

        mockMvc.perform(get("/api/payment/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.amount").value(999.99))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).getDetails(paymentId);
    }

    @Test
    void getPaymentDetails_shouldReturn404_whenServiceThrowsNotFound() throws Exception {
        UUID paymentId = UUID.randomUUID();

        when(paymentService.getDetails(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment not found with id: " + paymentId));

        // This assertion reflects the current GlobalExceptionHandler implementation.
        mockMvc.perform(get("/api/payment/{id}", paymentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Payment not found with id: " + paymentId))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void savePayment_shouldReturn201_whenRequestIsValid() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        PaymentResponseDTO response = new PaymentResponseDTO(
                paymentId,
                orderId,
                userId,
                new BigDecimal("499.99"),
                PaymentStatus.SUCCESS,
                LocalDateTime.now()
        );

        when(paymentService.savePayment(any(CreatePaymentRequestDTO.class)))
                .thenReturn(response);

        String body = """
                {
                  "orderId": "%s",
                  "userId": "%s",
                  "amount": 499.99
                }
                """.formatted(orderId, userId);

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.amount").value(499.99))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService).savePayment(any(CreatePaymentRequestDTO.class));
    }

    @Test
    void savePayment_shouldReturn400_whenOrderIdIsMissing() throws Exception {
        String body = """
                {
                  "userId": "%s",
                  "amount": 499.99
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void savePayment_shouldReturn400_whenUserIdIsMissing() throws Exception {
        String body = """
                {
                  "orderId": "%s",
                  "amount": 499.99
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void savePayment_shouldReturn400_whenAmountIsZero() throws Exception {
        String body = """
                {
                  "orderId": "%s",
                  "userId": "%s",
                  "amount": 0
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    void savePayment_shouldReturn400_whenAmountIsNegative() throws Exception {
        String body = """
                {
                  "orderId": "%s",
                  "userId": "%s",
                  "amount": -10
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }
}
