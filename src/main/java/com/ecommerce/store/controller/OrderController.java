package com.ecommerce.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.CreateOrderRequest;
import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/v1/orders")
@RequiredArgsConstructor 
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping 
    public ResponseEntity<OrderResponse> createOrder(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = orderService.createOrder(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}