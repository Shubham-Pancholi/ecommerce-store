package com.ecommerce.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.UserWishlist;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.entity.Wishlist;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;
import com.ecommerce.store.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
@Transactional (readOnly = true)
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserWishlist getWishlistOfUser(Long userId) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return UserWishlist.getUserWishlist(
            user.getId(),
            wishlistRepository.findByUserId(user.getId())
        );
    }

    @Transactional 
    public UserWishlist addProductToUserWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                          .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Product is already in the wishlist");
        }

        Wishlist wishlistElement = Wishlist.builder()
                                           .user(user)
                                           .product(product)
                                           .build();

        wishlistRepository.save(wishlistElement);

        return getWishlistOfUser(userId);
    }

    @Transactional 
    public UserWishlist removeProductFromUserWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return getWishlistOfUser(userId);
    }
}