package com.ecommerce.PaymentService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
