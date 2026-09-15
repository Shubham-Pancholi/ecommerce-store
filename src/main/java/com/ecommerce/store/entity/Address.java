package com.ecommerce.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import  jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "addresses")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class Address {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id", nullable = false)
    private User user;

    @Column (nullable = false, length = 255)
    private String street;

    @Column (nullable = false, length = 100)
    private String city;

    @Column (nullable = false, length = 100)
    private String state;

    @Column (name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column (nullable = false, length = 100)
    private String country;

    @Column (name = "is_default", nullable = false)
    @Builder.Default
    private boolean defaultAddress = false;
}