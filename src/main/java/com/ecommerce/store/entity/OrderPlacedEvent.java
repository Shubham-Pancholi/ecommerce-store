package com.ecommerce.store.entity;

public record OrderPlacedEvent(
    String orderNumber,
    String userEmail,
    String firstName,
    String totalAmount
) {}