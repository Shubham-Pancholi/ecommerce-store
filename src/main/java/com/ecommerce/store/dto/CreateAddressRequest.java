package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
    @NotBlank (message = "Street is required")
    @Size (max = 255, message = "Street name/no cannot exceed 255 characters")
    String street,

    @NotBlank (message = "City is required")
    @Size (max = 100, message = "City is required")
    String city,

    @NotBlank (message = "State is required")
    @Size (max = 100, message = "State is required")
    String state,

    @NotBlank (message = "Zip code is required")
    @Size (max = 100, message = "Zip code is required")
    String zipCode,

    @NotBlank (message = "Country is required")
    @Size (max = 100, message = "Country is required")
    String country,

    @NotNull (message = "Default status is required")
    Boolean defaultAddress
) {}