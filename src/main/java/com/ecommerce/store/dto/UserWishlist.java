package com.ecommerce.store.dto;

import java.util.List;

public record UserWishlist(
    Long userId,
    List<WishlistedProduct> userWishlist
) {}