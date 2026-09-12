package com.ecommerce.store.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal pricePerUnit,
    BigDecimal subTotal
) {}