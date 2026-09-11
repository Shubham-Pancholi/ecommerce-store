package com.ecommerce.store.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
@Document (collection = "product_reviews")
public class ProductReview {
    
    @Id 
    private String id;

    private Long productId;
    private Long userId;

    private String title;
    private String comment;
    private int rating;

    private Instant createdAt;
    private Instant recentEdit;
}