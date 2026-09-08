package com.ecommerce.store.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ecommerce.store.entity.Order;
import com.ecommerce.store.entity.OrderStatus;

public record OrderResponse(
    Long id,
    String orderNumber,
    Long userId,
    String userEmail,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> items,
    Instant createdAt
) {
    public static OrderResponse fromEntity(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                                                .map(OrderItemResponse :: fromEntity)
                                                .toList();

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getUser().getId(),
            order.getUser().getEmail(),
            order.getStatus(),
            order.getTotalAmount(),
            itemResponses,
            order.getCreatedAt()
        );
    }
}