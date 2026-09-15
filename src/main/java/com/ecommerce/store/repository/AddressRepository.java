package com.ecommerce.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.store.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdAndDefaultAddressTrue(Long UserId);
}