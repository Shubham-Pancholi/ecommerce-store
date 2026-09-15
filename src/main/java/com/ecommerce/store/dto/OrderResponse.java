package com.ecommerce.store.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ecommerce.store.entity.OrderStatus;

public record OrderResponse(
    Long id,
    String orderNumber,
    Long userId,
    String userEmail,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items,
    Instant createdAt,
    String shippingAddress,
    String razorpayOrderId
) {}