package com.ecommerce.store.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.store.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByStatus(String status, Pageable pageable);

    @Query ( 
        "SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " + 
        "(:categoryName IS NULL OR p.category.name = :categoryName) AND " + 
        "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + 
        "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " + 
        "(:minPrice IS NULL OR p.price >= :minPrice) AND" + 
        "(:maxPrice IS NULL OR p.price <= :maxPrice)"
    )
    Page<Product> searchProducts(
        @Param ("categoryName") String categoryName,
        @Param ("keyword") String keyword,
        @Param ("minPrice") BigDecimal minPrice,
        @Param ("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
}