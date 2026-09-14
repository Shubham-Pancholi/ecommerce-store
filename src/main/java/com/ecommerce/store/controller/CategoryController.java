package com.ecommerce.store.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.CategoryResponse;
import com.ecommerce.store.dto.CreateCategoryRequest;
import com.ecommerce.store.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor
@RequestMapping ("/api/v1/categories") 
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping 
    public ResponseEntity<CategoryResponse> createCategory(
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping 
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
        @PageableDefault (page = 0, size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @PutMapping 
    public ResponseEntity<CategoryResponse> updateCategory(
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(request));
    }
}