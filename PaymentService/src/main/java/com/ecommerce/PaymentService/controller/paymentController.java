package com.ecommerce.PaymentService.controller;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class paymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentDetails(@PathVariable UUID id){
        return new ResponseEntity<>(paymentService.getDetails(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> savePayment(@RequestBody CreatePaymentRequestDTO requestDTO){
        return new ResponseEntity<>(paymentService.savePayment(requestDTO),HttpStatus.OK);
    }
}
