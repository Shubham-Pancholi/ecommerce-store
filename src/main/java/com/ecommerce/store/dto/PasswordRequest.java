package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
    @NotBlank (message = "Password is required")
    @Size (min = 6, message = "Password must contain at least 6 characters")
    String password
) {}
