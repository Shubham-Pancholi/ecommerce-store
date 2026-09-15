package com.ecommerce.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.AddressResponse;
import com.ecommerce.store.dto.CreateAddressRequest;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/v1/addresses")
@RequiredArgsConstructor 
public class AddressController {
    private final AddressService addressService;

    @PostMapping 
    public ResponseEntity<AddressResponse> addAddress(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody CreateAddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(addressService.addAddress(currentUser.getId(), request));
    }

    @GetMapping 
    public ResponseEntity<AddressResponse> getAddresses(
        @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(addressService.getAddresses(currentUser.getId()));
    }

    @DeleteMapping ("/{addressId}")
    public ResponseEntity<AddressResponse> removeAddress(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(addressService.removeAddress(currentUser.getId(), addressId));
    }

    @PutMapping ("{addressId}")
    public ResponseEntity<AddressResponse> changeDefaultAddress(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long addressId
    ) {
        return ResponseEntity.ok(addressService.changeDefaultAddress(currentUser.getId(), addressId));
    }
}
