package com.e_commerce.backend.controllers;

import com.e_commerce.backend.dto.AddToCartRequest;
import com.e_commerce.backend.dto.CartDTO;
import com.e_commerce.backend.dto.UpdateCartItemRequest;
import com.e_commerce.backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", exposedHeaders = "X-Cart-Session")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(
            @RequestHeader(value = "X-Cart-Session", required = false) String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            // Return empty cart if no session
            return ResponseEntity.ok(new CartDTO());
        }
        CartDTO cart = cartService.getCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartDTO> addToCart(
            @RequestBody AddToCartRequest request,
            @RequestHeader(value = "X-Cart-Session", required = false) String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        try {
            CartDTO cart = cartService.addToCart(sessionId, request);
            return ResponseEntity.ok()
                    .header("X-Cart-Session", sessionId)
                    .body(cart);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request,
            @RequestHeader(value = "X-Cart-Session", required = false) String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            CartDTO cart = cartService.updateCartItem(sessionId, itemId, request);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Cart-Session", required = false) String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cartService.removeCartItem(sessionId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader(value = "X-Cart-Session", required = false) String sessionId) {

        if (sessionId != null && !sessionId.isEmpty()) {
            cartService.clearCart(sessionId);
        }
        return ResponseEntity.noContent().build();
    }
}
