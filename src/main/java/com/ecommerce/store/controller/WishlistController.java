package com.ecommerce.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.UserWishlist;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.service.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("api/v1/wishlist")
@RequiredArgsConstructor 
public class WishlistController {
    
    private final WishlistService wishlistService;

    @GetMapping 
    public ResponseEntity<UserWishlist> getUserWishlist(
        @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(wishlistService.getWishlistOfUser(currentUser.getId()));
    }

    @PostMapping ("/products/{productId}")
    public ResponseEntity<UserWishlist> addProductToWishlist(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long productId
    ) {
        UserWishlist userWishlist = wishlistService.addProductToUserWishlist(currentUser.getId(), productId);
        return ResponseEntity.status(HttpStatus.OK).body(userWishlist);
    }

    @DeleteMapping ("/products/{productId}")
    public ResponseEntity<UserWishlist> removeProductFromWishlist(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long productId
    ) {
        UserWishlist userWishlist = wishlistService.removeProductFromUserWishlist(currentUser.getId(), productId);
        return ResponseEntity.status(HttpStatus.OK).body(userWishlist);
    }
}