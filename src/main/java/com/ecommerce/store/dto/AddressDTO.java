package com.ecommerce.store.dto;

public record AddressDTO(
    Long id,
    String street,
    String city,
    String state,
    String zipCode,
    String country,
    boolean defaultAddress
) {}