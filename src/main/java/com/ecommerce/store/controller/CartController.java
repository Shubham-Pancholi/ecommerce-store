package com.ecommerce.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.OrderItemRequest;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.model.Cart;
import com.ecommerce.store.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping 
    public ResponseEntity<Cart> getCart(
        @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(cartService.getCart(currentUser.getId()));
    }

    @PostMapping ("/items")
    public ResponseEntity<Cart> addItemToCart(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody OrderItemRequest cartAddRequest
    ) {
        return ResponseEntity.ok(cartService.addItemToCart(currentUser.getId(), cartAddRequest.productId(), cartAddRequest.quantity()));
    }

    @DeleteMapping 
    public ResponseEntity<String> clearCart(
        @AuthenticationPrincipal User currentUser
    ) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.ok("Cart cleared.");
    }

    @DeleteMapping ("/items/{productId}")
    public ResponseEntity<Cart> removeItemFromCart(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long productId
    ) {
        return ResponseEntity.ok(cartService.removeItemFromCart(currentUser.getId(), productId));
    }
}
