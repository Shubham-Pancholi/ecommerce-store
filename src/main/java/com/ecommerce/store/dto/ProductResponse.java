package com.ecommerce.store.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    String status,
    Instant createdAt,
    String imageUrl,
    String category
) implements java.io.Serializable {}