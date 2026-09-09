package com.ecommerce.store.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateOrderRequest(
    @NotEmpty (message = "Order must contain at least one item")
    @Valid 
    List<OrderItemRequest> items
) {}