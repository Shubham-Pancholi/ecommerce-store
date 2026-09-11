package com.ecommerce.store.service;

import com.ecommerce.store.repository.ProductRepository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.store.entity.ProductReview;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.ProductReviewRepository;
import com.ecommerce.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ProductReviewService {
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;

    public ProductReview addReview(Long userId, Long productId, String title, String comment, int rating) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }

        if (productReviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        ProductReview review = ProductReview.builder()
                                            .userId(userId)
                                            .productId(productId)
                                            .title(title)
                                            .comment(comment)
                                            .rating(rating)
                                            .createdAt(Instant.now())
                                            .recentEdit(Instant.now())
                                            .build();
        return productReviewRepository.save(review);
    }

    public List<ProductReview> getReviewsForProduct(Long productId) {
        return productReviewRepository.findByProductId(productId);
    }
}