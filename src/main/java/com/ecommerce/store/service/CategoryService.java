package com.ecommerce.store.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CategoryResponse;
import com.ecommerce.store.dto.CreateCategoryRequest;
import com.ecommerce.store.entity.Category;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.mapper.CategoryMapper;
import com.ecommerce.store.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional 
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category already exists.");
        }

        Category category = Category.builder()
                                    .name(request.name())
                                    .description(request.description())
                                    .createdAt(Instant.now())
                                    .build();

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                                 .map(categoryMapper :: toResponse);
    }

    @Transactional 
    public CategoryResponse updateCategory(CreateCategoryRequest request) {
        Category category = categoryRepository.findByName(request.name())
                                              .orElseThrow(() -> new ResourceNotFoundException("Category doesn't exist"));

        category.setDescription(request.description());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
