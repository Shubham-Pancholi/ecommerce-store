package com.ecommerce.store.dto;

import java.time.Instant;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    Instant createdAt
) {}