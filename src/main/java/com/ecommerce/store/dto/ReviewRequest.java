package com.ecommerce.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
    @NotBlank (message = "Title cannot be empty")
    String title,

    @NotBlank (message = "Comment cannot be empty")
    String comment,

    @Min (value = 1, message = "Rating must be at least 1")
    @Max (value = 5, message = "Rating cannot be greater than 5")
    int rating
) {}