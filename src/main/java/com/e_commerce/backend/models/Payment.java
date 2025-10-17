package com.e_commerce.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private BigDecimal amount;
    private String paymentStatus; // COMPLETED, FAILED
    private LocalDateTime paymentDate;
    private String cardLastFour;
    private String cardholderName;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}

