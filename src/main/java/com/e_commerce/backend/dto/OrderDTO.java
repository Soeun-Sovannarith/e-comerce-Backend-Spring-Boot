package com.e_commerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String sessionId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private List<OrderItemDTO> items;
    private PaymentDTO payment;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDTO {
        private Long id;
        private ProductSummaryDTO product;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDTO {
        private Long id;
        private BigDecimal amount;
        private String paymentStatus;
        private LocalDateTime paymentDate;
        private String cardLastFour;
        private String cardholderName;
    }
}

