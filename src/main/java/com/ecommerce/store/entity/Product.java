package com.ecommerce.store.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "products")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class Product {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true, length = 50)
    private String sku;

    @Column (nullable = false)
    private String name;

    @Column (columnDefinition = "TEXT")
    private String description;

    @Column (nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column (name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column (nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @Version
    @Column (nullable = false)
    @Builder.Default
    private Integer version = 0;

    @Column (name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column (name = "image_url", length = 512)
    private String imageUrl;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "category_id")
    private Category category;

    @PrePersist 
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}