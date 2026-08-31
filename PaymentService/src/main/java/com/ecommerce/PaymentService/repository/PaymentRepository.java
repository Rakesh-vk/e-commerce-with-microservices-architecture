package com.ecommerce.PaymentService.repository;

import com.ecommerce.PaymentService.entity.Payment;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
