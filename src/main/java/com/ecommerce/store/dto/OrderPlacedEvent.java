package com.ecommerce.store.dto;

public record OrderPlacedEvent(
    String orderNumber,
    String userEmail,
    String firstName,
    String totalAmount,
    String shippingAddress
) {}