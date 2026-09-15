package com.ecommerce.store.dto;

import java.util.List;

public record AddressResponse(
    Long userId,
    List<AddressDTO> addresses
) {}