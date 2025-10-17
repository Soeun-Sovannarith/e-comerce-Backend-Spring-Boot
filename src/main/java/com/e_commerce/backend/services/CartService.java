package com.e_commerce.backend.services;

import com.e_commerce.backend.dto.*;
import com.e_commerce.backend.models.CartItem;
import com.e_commerce.backend.models.Product;
import com.e_commerce.backend.repositories.CartItemRepository;
import com.e_commerce.backend.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Cacheable(value = "cart", key = "#sessionId")
    public CartDTO getCart(String sessionId) {
        List<CartItem> cartItems = cartItemRepository.findBySessionIdOrderByIdDesc(sessionId);
        return buildCartDTO(cartItems);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#sessionId")
    public CartDTO addToCart(String sessionId, AddToCartRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        if (!product.isAvailable()) {
            throw new IllegalArgumentException("Product is not available");
        }

        if (product.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findBySessionIdAndProduct(sessionId, product)
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            // New cart item
            cartItem.setSessionId(sessionId);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        } else {
            // Update existing cart item
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (product.getQuantity() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
            }
            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return getCart(sessionId);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#sessionId")
    public CartDTO updateCartItem(String sessionId, Long itemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByIdAndSessionId(itemId, sessionId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            if (cartItem.getProduct().getQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + cartItem.getProduct().getQuantity());
            }
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return getCart(sessionId);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#sessionId")
    public void removeCartItem(String sessionId, Long itemId) {
        CartItem cartItem = cartItemRepository.findByIdAndSessionId(itemId, sessionId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    @CacheEvict(value = "cart", key = "#sessionId")
    public void clearCart(String sessionId) {
        cartItemRepository.deleteBySessionId(sessionId);
    }

    private CartDTO buildCartDTO(List<CartItem> cartItems) {
        List<CartItemDTO> items = cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartDTO(items, totalAmount, totalItems);
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        ProductSummaryDTO productDTO = new ProductSummaryDTO(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getDescription(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getCategory(),
                "/api/product/" + cartItem.getProduct().getId() + "/image",
                cartItem.getProduct().isAvailable(),
                cartItem.getProduct().getQuantity()
        );

        return new CartItemDTO(cartItem.getId(), productDTO, cartItem.getQuantity());
    }
}
