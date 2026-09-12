package com.ecommerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.WishlistedProduct;
import com.ecommerce.store.entity.Wishlist;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface WishlistedProductMapper {

    @Mapping (target = "productId", source="product.id")
    @Mapping (target = "productName", source = "product.name")
    WishlistedProduct toWishlistedProduct(Wishlist wishlist);
}