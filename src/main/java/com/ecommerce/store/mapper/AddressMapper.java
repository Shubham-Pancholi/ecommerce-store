package com.ecommerce.store.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.AddressResponse;
import com.ecommerce.store.entity.Address;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING, uses = {AddressDTOMapper.class})
public interface AddressMapper {

    AddressResponse toResponse(Long userId, List<Address> addresses);

    default Long mapUserId(Long userId, List<Address> addresses) {
        return userId;
    }
}