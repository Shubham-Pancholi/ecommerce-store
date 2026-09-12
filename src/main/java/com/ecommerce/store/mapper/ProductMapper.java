package com.ecommerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.ProductResponse;
import com.ecommerce.store.entity.Product;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    
    ProductResponse toResponse(Product product);
}