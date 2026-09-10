package com.ecommerce.store.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.CreateProductRequest;
import com.ecommerce.store.dto.PageResponse;
import com.ecommerce.store.dto.ProductResponse;
import com.ecommerce.store.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/v1/products")
@RequiredArgsConstructor 
public class ProductController {
    
    private final ProductService productService;

    @PostMapping 
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping 
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
        @PageableDefault (page = 0, size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping ("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
        @RequestParam (required = false, defaultValue = "") String keyword,
        @RequestParam (required = false, defaultValue = "0.00") BigDecimal minPrice,
        @RequestParam (required = false, defaultValue = "9999999.99") BigDecimal maxPrice,
        @PageableDefault (size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<ProductResponse> products = productService.searchProducts(keyword, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(PageResponse.of(products));
    }
}