package com.ecommerce.UserService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id ;
    @Column(nullable = false)
    String email;// String (unique, not null)
    @Column(name ="password_hash", nullable = false)
    String passwordHash;
    @Column(nullable = false)
    String username;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;// Instant
}
