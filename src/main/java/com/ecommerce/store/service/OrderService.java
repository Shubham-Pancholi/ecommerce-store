package com.ecommerce.store.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.cache.CacheManager;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CreateOrderRequest;
import com.ecommerce.store.dto.OrderItemRequest;
import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.entity.Order;
import com.ecommerce.store.entity.OrderItem;
import com.ecommerce.store.entity.OrderStatus;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.OrderRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional (readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    @Transactional 
    @Retryable (
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff (delay = 100)
    )
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Order order = Order.builder()
                           .user(user)
                           .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                           .status(OrderStatus.PENDING)
                           .totalAmount(BigDecimal.ZERO)
                           .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                                               .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.productId()));

            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested: " + itemRequest.quantity());
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            if (cacheManager.getCache("product") != null) {
                cacheManager.getCache("product").evict(product.getId());
            }

            OrderItem orderItem = OrderItem.builder()
                                           .product(product)
                                           .quantity(itemRequest.quantity())
                                           .pricePerUnit(product.getPrice())
                                           .build();

            order.addItem(orderItem);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            total = total.add(itemTotal);
        }
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.fromEntity(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                              .map(OrderResponse :: fromEntity)
                              .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}