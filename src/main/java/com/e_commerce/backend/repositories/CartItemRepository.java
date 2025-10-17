package com.e_commerce.backend.repositories;

import com.e_commerce.backend.models.CartItem;
import com.e_commerce.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.sessionId = :sessionId ORDER BY ci.id DESC")
    List<CartItem> findBySessionIdOrderByIdDesc(@Param("sessionId") String sessionId);

    Optional<CartItem> findBySessionIdAndProduct(String sessionId, Product product);

    Optional<CartItem> findByIdAndSessionId(Long id, String sessionId);

    void deleteBySessionId(String sessionId);
}
