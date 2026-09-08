package com.ecommerce.store.dto;

import java.math.BigDecimal;

import com.ecommerce.store.entity.OrderItem;

public record OrderItemResponse(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal pricePerUnit,
    BigDecimal subTotal
) {
    public static OrderItemResponse fromEntity(OrderItem item) {
        BigDecimal subTotal = item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPricePerUnit(),
            subTotal
        );
    }
}