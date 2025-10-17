package com.e_commerce.backend.repositories;

import com.e_commerce.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySessionIdOrderByOrderDateDesc(String sessionId);
}

