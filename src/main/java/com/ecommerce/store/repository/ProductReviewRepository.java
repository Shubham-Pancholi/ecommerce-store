package com.ecommerce.store.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecommerce.store.entity.ProductReview;

public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    
    List<ProductReview> findByProductId(Long productId);

    List<ProductReview> findByUserId(Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);
}