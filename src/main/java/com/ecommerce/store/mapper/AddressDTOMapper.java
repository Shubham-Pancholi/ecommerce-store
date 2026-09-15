package com.ecommerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.AddressDTO;
import com.ecommerce.store.entity.Address;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressDTOMapper {

    AddressDTO toAddressDTO(Address address);
}
