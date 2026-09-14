package com.ecommerce.store.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class Cart {
    private Long userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    
    public void recalculation() {
        this.totalPrice = items.stream()
                               .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                               .reduce(BigDecimal.ZERO, (total, currentPrice) -> total.add(currentPrice));
    }
}