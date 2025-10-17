package com.e_commerce.backend.services;

import com.e_commerce.backend.dto.OrderDTO;
import com.e_commerce.backend.dto.PaymentRequest;
import com.e_commerce.backend.dto.ProductSummaryDTO;
import com.e_commerce.backend.models.*;
import com.e_commerce.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    public OrderDTO processPayment(PaymentRequest request) {
        // Validate cart items
        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Create the order
        Order order = new Order();
        order.setSessionId(request.getSessionId());
        order.setTotalAmount(request.getAmount());
        order.setStatus("PENDING");
        order.setShippingAddress(request.getShippingAddress());

        // Save the order first to get the ID
        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        for (PaymentRequest.CartItemForOrder cartItem : request.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

            // Check stock availability
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());

            orderItemRepository.save(orderItem);

            // Update product quantity
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Create the payment record
        Payment payment = new Payment();
        payment.setOrderId(savedOrder.getId());
        payment.setAmount(request.getAmount());
        payment.setCardholderName(request.getCardName());
        // Only store last 4 digits of card for security
        String cardNumber = request.getCardNumber().replaceAll("\\s", "");
        payment.setCardLastFour(cardNumber.length() >= 4 ?
                cardNumber.substring(cardNumber.length() - 4) : cardNumber);
        payment.setPaymentStatus("COMPLETED");

        Payment savedPayment = paymentRepository.save(payment);

        // Update order status to COMPLETED
        savedOrder.setStatus("COMPLETED");
        orderRepository.save(savedOrder);

        // Clear the user's cart
        if (request.getSessionId() != null) {
            cartItemRepository.deleteBySessionId(request.getSessionId());
        }

        // Return the order DTO
        return convertToOrderDTO(savedOrder, savedPayment);
    }

    public List<OrderDTO> getOrderHistory(String sessionId) {
        List<Order> orders = orderRepository.findBySessionIdOrderByOrderDateDesc(sessionId);
        return orders.stream()
                .map(order -> {
                    List<Payment> payments = paymentRepository.findByOrderId(order.getId());
                    Payment payment = payments.isEmpty() ? null : payments.get(0);
                    return convertToOrderDTO(order, payment);
                })
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long orderId, String sessionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify order belongs to this session
        if (!order.getSessionId().equals(sessionId)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        Payment payment = payments.isEmpty() ? null : payments.get(0);

        return convertToOrderDTO(order, payment);
    }

    private OrderDTO convertToOrderDTO(Order order, Payment payment) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setSessionId(order.getSessionId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());

        // Convert order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderDTO.OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> {
                    ProductSummaryDTO productDTO = new ProductSummaryDTO(
                            item.getProduct().getId(),
                            item.getProduct().getName(),
                            item.getProduct().getDescription(),
                            item.getProduct().getPrice(),
                            item.getProduct().getCategory(),
                            "/api/product/" + item.getProduct().getId() + "/image",
                            item.getProduct().isAvailable(),
                            item.getProduct().getQuantity()
                    );

                    return new OrderDTO.OrderItemDTO(
                            item.getId(),
                            productDTO,
                            item.getQuantity(),
                            item.getPrice()
                    );
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);

        // Add payment info if available
        if (payment != null) {
            OrderDTO.PaymentDTO paymentDTO = new OrderDTO.PaymentDTO(
                    payment.getId(),
                    payment.getAmount(),
                    payment.getPaymentStatus(),
                    payment.getPaymentDate(),
                    payment.getCardLastFour(),
                    payment.getCardholderName()
            );
            dto.setPayment(paymentDTO);
        }

        return dto;
    }
}

