package com.ecommerce.store.dto;

import java.util.List;

import com.ecommerce.store.entity.Wishlist;

public record UserWishlist(
    Long userId,
    List<WishlistedProduct> userWishlist
) {
    public static UserWishlist getUserWishlist( Long userId, List<Wishlist> wishlist ){
        return new UserWishlist(
            userId,
            wishlist.stream()
                    .map( WishlistedProduct :: fromEntity )
                    .toList()
        );
    }
}