package com.ecommerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.entity.Order;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping (target = "userId", source = "user.id")
    @Mapping (target = "userEmail", source = "user.email")
    @Mapping (target = "items", source = "items")
    OrderResponse tOrderResponse(Order order);
}