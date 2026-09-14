package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank (message = "Category name is required")
    @Size (max = 255, message = "Category name cannot exceed 255 characters")
    String name,

    String description
) {}
