package com.ecommerce.store.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull (message = "User ID is required")
    Long userId,

    @NotEmpty (message = "Order must contain at least one item")
    @Valid 
    List<OrderItemRequest> items
) {}