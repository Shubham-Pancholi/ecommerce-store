package com.ecommerce.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.store.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @EntityGraph (attributePaths = {"product"})
    List<Wishlist> findByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}