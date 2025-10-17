package com.e_commerce.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String sessionId;
    private BigDecimal amount;
    private String cardName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String shippingAddress;
    private List<CartItemForOrder> cartItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemForOrder {
        private int productId;
        private int quantity;
        private BigDecimal price;
    }
}

