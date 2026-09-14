package com.ecommerce.store.service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CreateOrderRequest;
import com.ecommerce.store.dto.OrderPlacedEvent;
import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.entity.Order;
import com.ecommerce.store.entity.OrderItem;
import com.ecommerce.store.entity.OrderStatus;
import com.ecommerce.store.entity.OutboxMessage;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.mapper.OrderMapper;
import com.ecommerce.store.model.Cart;
import com.ecommerce.store.model.CartItem;
import com.ecommerce.store.repository.OrderRepository;
import com.ecommerce.store.repository.OutboxMessageRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service 
@RequiredArgsConstructor 
@Transactional (readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    private final RedissonClient redissonClient;
    private final OrderMapper orderMapper;
    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;
    private final CartService cartService;

    @Transactional 
    @Retryable (
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff (delay = 100)
    )
    @SneakyThrows 
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        RLock lock = redissonClient.getLock("order-lock:user" + userId);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new IllegalStateException("Please wait, your previous order is still processing.");
            }

            User user = userRepository.findById(userId)
                                      .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
            Order order = Order.builder()
                            .user(user)
                            .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                            .status(OrderStatus.PENDING)
                            .totalAmount(BigDecimal.ZERO)
                            .build();

            BigDecimal total = BigDecimal.ZERO;

            Cart cart = cartService.getCart(userId);

            if (cart.getItems().isEmpty()) {
                throw new IllegalArgumentException("Your Cart is empty. Please add products first.");
            }

            for (CartItem item : cart.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                                                   .orElseThrow(() -> new ResourceNotFoundException("Product is discontinued"));

                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity());
                }

                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

                if (cacheManager.getCache("product") != null) {
                    cacheManager.getCache("product").evict(product.getId());
                }

                OrderItem orderItem = OrderItem.builder()
                                               .product(product)
                                               .quantity(item.getQuantity())
                                               .pricePerUnit(product.getPrice())
                                               .build();

                order.addItem(orderItem);

                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }

            order.setTotalAmount(total);

            Order savedOrder = orderRepository.save(order);

            OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getOrderNumber(),
                user.getEmail(),
                user.getFirstName(),
                savedOrder.getTotalAmount().toString(),
                request.shippingAddress()
            );

            String jsonPayload = objectMapper.writeValueAsString(event);

            OutboxMessage outboxMessage = OutboxMessage.builder()
                                                       .topic("order-notifications")
                                                       .payload(jsonPayload)
                                                       .build();

            outboxMessageRepository.save(outboxMessage);

            cartService.clearCart(userId);

            return orderMapper.tOrderResponse(savedOrder);

        }   catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Order Processing was interrupted");
        }   finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id)
                              .map(orderMapper :: tOrderResponse)
                              .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Page<OrderResponse> getOrderByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                              .map(orderMapper :: tOrderResponse);
    }

    public Page<OrderResponse> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                              .map(orderMapper :: tOrderResponse);
    }
}