package com.ecommerce.store.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CreateProductRequest;
import com.ecommerce.store.dto.ProductResponse;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.mapper.ProductMapper;
import com.ecommerce.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional (readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    @Cacheable (value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
              .map(productMapper :: toResponse)
              .orElseThrow(() -> new ResourceNotFoundException("Product not found with the id: +" + id) );
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll( pageable )
              .map(productMapper :: toResponse);
    }

    @Transactional 
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.sku() + "' already exists");
        }

        Product product = Product.builder()
                                 .sku(request.sku())
                                 .name(request.name())
                                 .description(request.description())
                                 .price(request.price())
                                 .stockQuantity(request.stockQuantity())
                                 .status("ACTIVE")
                                 .imageUrl(request.imageUrl())
                                 .build();

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    public Page<ProductResponse> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        String safeKeyword = (keyword != null) ? keyword : "";

        return productRepository.searchProducts(safeKeyword, minPrice, maxPrice, pageable)
                                .map(productMapper :: toResponse);
    }

    @Transactional 
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                                           .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());

        if (cacheManager.getCache("product") != null) {
            cacheManager.getCache("product").evict(id);
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional 
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                                           .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStatus("DELETED");
        productRepository.save(product);

        Optional.ofNullable(cacheManager.getCache("product"))
                .ifPresent(cache -> cache.evictIfPresent(id));
    }
}