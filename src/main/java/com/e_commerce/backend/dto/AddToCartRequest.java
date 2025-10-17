package com.e_commerce.backend.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private int productId;
    private int quantity;
}

