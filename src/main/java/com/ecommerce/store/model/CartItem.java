package com.ecommerce.store.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class CartItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}