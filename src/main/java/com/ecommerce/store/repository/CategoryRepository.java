package com.ecommerce.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.store.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    public Optional<Category> findByName(String name);

    public Optional<Category> findById(Long id);

    public boolean existsByName(String name);
}