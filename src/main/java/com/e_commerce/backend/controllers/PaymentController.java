package com.e_commerce.backend.controllers;

import com.e_commerce.backend.dto.OrderDTO;
import com.e_commerce.backend.dto.PaymentRequest;
import com.e_commerce.backend.dto.PaymentResponse;
import com.e_commerce.backend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        try {
            // Validate input
            if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new PaymentResponse(false, "Session ID is required", null, null)
                );
            }

            if (request.getAmount() == null || request.getAmount().doubleValue() <= 0) {
                return ResponseEntity.badRequest().body(
                        new PaymentResponse(false, "Invalid payment amount", null, null)
                );
            }

            if (request.getCardName() == null || request.getCardName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new PaymentResponse(false, "Cardholder name is required", null, null)
                );
            }

            if (request.getCardNumber() == null || request.getCardNumber().replaceAll("\\s", "").length() < 13) {
                return ResponseEntity.badRequest().body(
                        new PaymentResponse(false, "Invalid card number", null, null)
                );
            }

            // Process the payment
            OrderDTO order = paymentService.processPayment(request);

            PaymentResponse response = new PaymentResponse(
                    true,
                    "Payment processed successfully",
                    order.getId(),
                    order.getPayment().getId()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new PaymentResponse(false, e.getMessage(), null, null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PaymentResponse(false, "Payment processing failed: " + e.getMessage(), null, null)
            );
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(
            @RequestHeader(value = "X-Cart-Session", required = true) String sessionId) {
        try {
            List<OrderDTO> orders = paymentService.getOrderHistory(sessionId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-Cart-Session", required = true) String sessionId) {
        try {
            OrderDTO order = paymentService.getOrderById(orderId, sessionId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
