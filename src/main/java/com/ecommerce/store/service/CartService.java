package com.ecommerce.store.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import com.ecommerce.store.entity.Product;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.model.Cart;
import com.ecommerce.store.model.CartItem;
import com.ecommerce.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CartService {
    
    private final RedisTemplate<String, Cart> cartRedisTemplate;
    private final ProductRepository productRepository;

    private static final String CART_PREFIX = "cart:";

    public Cart getCart(Long userId) {
        Cart cart = cartRedisTemplate.opsForValue().get(CART_PREFIX + userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }
        return cart;
    }

    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCart(userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                                                         .filter(item -> item.getProductId().equals(productId))
                                                         .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        }
        else {
            Product product = productRepository.findById(productId)
                                               .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            cart.getItems().add(new CartItem(
                product.getId(),
                product.getName(),
                quantity,
                product.getPrice()
            ));
        }

        cart.recalculation();

        cartRedisTemplate.opsForValue().set(CART_PREFIX + userId, cart, Expiration.from(7, TimeUnit.DAYS));

        return cart;
    }

    public Cart removeItemFromCart(Long userId, Long productId) {
        Cart cart = getCart(userId);

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));

        cart.recalculation();

        cartRedisTemplate.opsForValue().set(CART_PREFIX + userId, cart, Expiration.from(7, TimeUnit.DAYS));

        return cart;
    }

    public void clearCart(Long userId) {
        cartRedisTemplate.delete(CART_PREFIX + userId);
    }
}