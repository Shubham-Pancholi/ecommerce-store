package com.ecommerce.store.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.PasswordRequest;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional 
    public void updateUserPassword(String email, PasswordRequest request) {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String hashedPassword = passwordEncoder.encode(request.newPassword());

        user.setPasswordHash(hashedPassword);

        userRepository.save(user);
    }

    @Transactional 
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Later check if there is any active order in the midst of delivery

        user.setStatus("DELETED");
        userRepository.save(user);
    }
}