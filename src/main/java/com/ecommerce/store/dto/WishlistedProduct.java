package com.ecommerce.store.dto;

import java.time.Instant;

public record WishlistedProduct(
    Long id,
    Long productId,
    String productName,
    Instant addedOn
) {}