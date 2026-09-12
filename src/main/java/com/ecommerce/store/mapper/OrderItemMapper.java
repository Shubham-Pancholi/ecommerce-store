package com.ecommerce.store.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.OrderItemResponse;
import com.ecommerce.store.entity.OrderItem;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderItemMapper {
    
    @Mapping (target = "productId", source = "product.id")
    @Mapping (target = "productName", source = "product.name")
    @Mapping (target = "subTotal", source = ".")
    OrderItemResponse toResponse(OrderItem orderItem);

    default BigDecimal mapSubTotal(OrderItem orderItem) {
        return orderItem.getPricePerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
}