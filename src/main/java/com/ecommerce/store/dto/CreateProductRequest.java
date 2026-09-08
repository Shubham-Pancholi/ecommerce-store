package com.ecommerce.store.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
    @NotBlank (message = "SKU is required")
    @Size (max = 50, message = "SKU cannot exceed 50 characters")
    String sku,

    @NotBlank (message = "Product name is required")
    @Size (max = 255, message = "Product name cannot exceed 255 characters")
    String name,

    String description,

    @NotNull (message = "Price is required")
    @Positive (message = "Price must be greater than zero")
    BigDecimal price,

    @NotNull (message = "Stock quantity is required")
    @PositiveOrZero (message = "Stock quantity cannot be negative")
    Integer stockQuantity
) {}