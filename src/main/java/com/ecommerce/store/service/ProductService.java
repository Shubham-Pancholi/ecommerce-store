package com.ecommerce.store.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CreateProductRequest;
import com.ecommerce.store.dto.ProductResponse;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional (readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;

    @Cacheable (value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
              .map(ProductResponse :: fromEntity)
              .orElseThrow(() -> new ResourceNotFoundException("Product not found with the id: +" + id) );
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll( pageable )
              .map(ProductResponse :: fromEntity);
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
                                 .build();

        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }
}