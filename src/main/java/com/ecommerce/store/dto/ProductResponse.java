package com.ecommerce.store.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.ecommerce.store.entity.Product;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    String status,
    Instant createdAt
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getStatus(),
            product.getCreatedAt()
        );
    }
}