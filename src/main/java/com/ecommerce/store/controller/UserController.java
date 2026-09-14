package com.ecommerce.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.store.dto.PasswordRequest;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/api/v1/user")
public class UserController {
    
    private final UserService userService;

    @PutMapping ("/me")
    public ResponseEntity<String> updateUserPassword(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody PasswordRequest request
    ) {
        userService.updateUserPassword(currentUser.getEmail(), request);
        return ResponseEntity.ok("Password Updated.");
    }

    @DeleteMapping ("/me")
    public ResponseEntity<String> deleteUser(
        @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok("Sad to see you go, but we welcome you back anytime.");
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteUser(
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User Deleted from active list.");
    }
}