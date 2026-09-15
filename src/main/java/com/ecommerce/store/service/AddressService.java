package com.ecommerce.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.AddressResponse;
import com.ecommerce.store.dto.CreateAddressRequest;
import com.ecommerce.store.entity.Address;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.mapper.AddressMapper;
import com.ecommerce.store.repository.AddressRepository;
import com.ecommerce.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional 
    public AddressResponse addAddress(Long userId, CreateAddressRequest request) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Could not find the user."));

        Address address = Address.builder()
                                 .user(user)
                                 .street(request.street())
                                 .city(request.city())
                                 .state(request.state())
                                 .zipCode(request.zipCode())
                                 .country(request.country())
                                 .defaultAddress(request.defaultAddress())
                                 .build();

        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        if (existingAddresses.isEmpty()) {
            address.setDefaultAddress(true);
        } else if (request.defaultAddress()) {
            removeDefaultAddress(userId);
        }

        addressRepository.save(address);

        return getAddresses(userId);
    }

    public AddressResponse getAddresses(Long userId) {
        return addressMapper.toResponse(userId, addressRepository.findByUserId(userId));
    }

    @Transactional 
    public AddressResponse removeAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = addressRepository.findById(addressId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (user.getId() != address.getUser().getId()) {
            throw new IllegalArgumentException("No such address associated with the user");
        }

        boolean wasDefault = address.isDefaultAddress();

        addressRepository.delete(address);
        addressRepository.flush();

        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByUserId(userId);
            if (!remainingAddresses.isEmpty()) {
                remainingAddresses.get(0).setDefaultAddress(true);
            }
        }

        return getAddresses(userId);
    }

    @Transactional 
    public AddressResponse changeDefaultAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = addressRepository.findById(addressId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (user.getId().equals(address.getUser().getId())) {
            throw new IllegalArgumentException("No such address associated with the user");
        }

        removeDefaultAddress(userId);

        address.setDefaultAddress(true);

        return getAddresses(userId);
    }

    @Transactional 
    private void removeDefaultAddress(Long userId) {
        List<Address> defaultAddress = addressRepository.findByUserIdAndDefaultAddressTrue(userId);
        
        if (defaultAddress.size() > 1) {
            throw new RuntimeException("Business logic failed");
        }

        if (defaultAddress.size() == 1) {
            defaultAddress.get(0).setDefaultAddress(false);
        }
    }
}