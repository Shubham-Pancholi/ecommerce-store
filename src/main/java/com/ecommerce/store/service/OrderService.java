package com.ecommerce.store.service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CreateOrderRequest;
import com.ecommerce.store.dto.OrderPlacedEvent;
import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.dto.PaymentVerificationRequest;
import com.ecommerce.store.entity.Address;
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
import com.ecommerce.store.repository.AddressRepository;
import com.ecommerce.store.repository.OrderRepository;
import com.ecommerce.store.repository.OutboxMessageRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayException;

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
    private final AddressRepository addressRepository;
    private final PaymentService paymentService;

    @Transactional 
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Address address = addressRepository.findById(request.addressId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }

        String formattedShippingAddress = String.format("%s, %s, %s, %s, %s", address.getStreet(), address.getCity(), address.getState(), address.getZipCode(), address.getCountry());

        Order order = Order.builder()
                        .user(user)
                        .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .status(OrderStatus.PENDING)
                        .shippingAddress(formattedShippingAddress)
                        .totalAmount(BigDecimal.ZERO)
                        .build();

        BigDecimal total = BigDecimal.ZERO;
        Cart cart = cartService.getCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your Cart is empty.");
        }

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product discontinued"));

            // ONLY VERIFY STOCK. DO NOT DEDUCT IT YET!
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                                            .product(product)
                                            .quantity(item.getQuantity())
                                            .pricePerUnit(product.getPrice())
                                            .build();
            order.addItem(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        try {
            String rzpOrderId = paymentService.createRazorpayOrder(savedOrder.getTotalAmount(), savedOrder.getOrderNumber());
            savedOrder.setRazorpayOrderId(rzpOrderId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate payment gateway: " + e.getMessage());
        }
        savedOrder = orderRepository.save(savedOrder);

        // DO NOT CLEAR CART OR SEND KAFKA EVENT HERE!
        return orderMapper.tOrderResponse(savedOrder);
    }

    @Transactional 
    @SneakyThrows
    public void verifyOrderPayment(PaymentVerificationRequest request) {
        // DEBUGGING: Let's make sure the JSON actually mapped to Java!
        System.out.println("--- VERIFYING PAYMENT ---");
        System.out.println("RZP Order ID: " + request.razorpayOrderId());
        System.out.println("RZP Payment ID: " + request.razorpayPaymentId());
        
        boolean isValid = paymentService.verifySignature(
            request.razorpayOrderId(),
            request.razorpayPaymentId(),
            request.razorpaySignature()
        );

        if (!isValid) {
            throw new IllegalArgumentException("Payment verification failed! The signature did not match.");
        }

        Order order = orderRepository.findByRazorpayOrderId(request.razorpayOrderId())
                                     .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) return; // Prevent double execution

        // LOCK AND FINALIZE (This prevents race conditions on stock)
        RLock lock = redissonClient.getLock("order-lock:user" + order.getUser().getId());
        boolean isLocked = lock.tryLock(0, 10, TimeUnit.SECONDS);

        try {
            if (!isLocked) throw new IllegalStateException("Order is currently processing.");

            // 1. Deduct Stock safely
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Product went out of stock while you were paying!"); 
                }
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                
                if (cacheManager.getCache("product") != null) {
                    cacheManager.getCache("product").evict(product.getId());
                }
            }

            // 2. Mark Paid
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            // 3. Clear Cart
            cartService.clearCart(order.getUser().getId());

            // 4. Fire Kafka Event
            OrderPlacedEvent event = new OrderPlacedEvent(
                order.getOrderNumber(),
                order.getUser().getEmail(),
                order.getUser().getFirstName(),
                order.getTotalAmount().toString(),
                order.getShippingAddress()
            );

            OutboxMessage outboxMessage = OutboxMessage.builder()
                                                       .topic("order-notifications")
                                                       .payload(objectMapper.writeValueAsString(event))
                                                       .build();
            outboxMessageRepository.save(outboxMessage);
        }   catch (Exception e) {
            System.out.println("Order finalization failed! Issuing automatic refund for payment: " + request.razorpayPaymentId());
            try {
                paymentService.issueRefund(request.razorpayPaymentId());
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            } catch (RazorpayException re) {
                // In the real world, this sends an urgent Slack message to the admin!
                System.err.println("CRITICAL FAULT: Could not refund payment! " + re.getMessage());
            }
            // Throw it back to the frontend
            throw new RuntimeException("Checkout failed, but your money was automatically refunded: " + e.getMessage());

        } finally {
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