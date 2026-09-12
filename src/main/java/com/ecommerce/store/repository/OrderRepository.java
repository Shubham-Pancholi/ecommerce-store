package com.ecommerce.store.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.store.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph (attributePaths = {"items.product"})
    Optional<Order> findByOrderNumber(String orderNumber);

    @EntityGraph (attributePaths = {"user", "items.product"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph (attributePaths = {"user", "items.product"})
    Page<Order> findAll(Pageable pageable);
}