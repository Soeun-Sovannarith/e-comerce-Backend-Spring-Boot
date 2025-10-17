package com.e_commerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String message;
    private Long orderId;
    private Long paymentId;
}

