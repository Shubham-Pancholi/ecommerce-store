package com.ecommerce.store.dto;

import java.time.Instant;

import com.ecommerce.store.entity.Wishlist;

public record WishlistedProduct(
    Long id,
    Long productId,
    String productName,
    Instant addedOn
) {
    public static WishlistedProduct fromEntity(Wishlist wishlistEntry) {
        return new WishlistedProduct(
            wishlistEntry.getId(),
            wishlistEntry.getProduct().getId(),
            wishlistEntry.getProduct().getName(),
            wishlistEntry.getAddedOn()
        );
    }
}