package com.ecommerce.store.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.UserWishlist;
import com.ecommerce.store.entity.Wishlist;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING, uses = {WishlistedProductMapper.class})
public interface UserWishlistMapper {
    
    UserWishlist toUserWishlist(Long userId, List<Wishlist> userWishlist);

    default Long mapUserId(Long userId, List<Wishlist> userWishlist) {
        return userId;
    }
}