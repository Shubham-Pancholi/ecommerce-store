package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
    @NotBlank (message = "Address is required")
    String shippingAddress
) {}