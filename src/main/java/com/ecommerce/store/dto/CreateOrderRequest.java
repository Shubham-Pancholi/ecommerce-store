package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull (message = "Shipping Address is required")
    Long addressId
) {}