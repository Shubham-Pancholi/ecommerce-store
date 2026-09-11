package com.ecommerce.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.ReviewRequest;
import com.ecommerce.store.entity.ProductReview;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.service.ProductReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/api/v1/products/{productId}/reviews")
public class ProductReviewController {
    
    private final ProductReviewService productReviewService;

    @PostMapping 
    public ResponseEntity<ProductReview> addReview(
        @PathVariable Long productId,
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody ReviewRequest request
    ) {
        ProductReview review = productReviewService.addReview(
            currentUser.getId(),
            productId,
            request.title(),
            request.comment(),
            request.rating()
        );
    return ResponseEntity.ok(review);
    }

    @GetMapping 
    public ResponseEntity<List<ProductReview>> getProductReview(
        @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
            productReviewService.getReviewsForProduct(productId)
        );
    }
}