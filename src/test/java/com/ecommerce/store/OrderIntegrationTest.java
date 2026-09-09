package com.ecommerce.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ecommerce.store.entity.Product;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.repository.OrderRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.ecommerce.store.service.JwtService jwtService;

    private User testUser;
    private Product testProduct;
    private String authToken;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .email("alice@example.com")
                .passwordHash("secret123")
                .firstName("Alice")
                .lastName("Smith")
                .role("ROLE_CUSTOMER")
                .status("ACTIVE")
                .build());

        testProduct = productRepository.save(Product.builder()
                .sku("LAPTOP-001")
                .name("Developer Laptop")
                .description("High-end workstation")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .status("ACTIVE")
                .build());

        authToken = jwtService.generateToken(testUser.getEmail(), testUser.getRole());
    }

    @Test
    @DisplayName("Should successfully create order and deduct product inventory")
    void shouldCreateOrderAndDeductStock() throws Exception {
        String requestJson = """
            {
                "items": [
                    {
                        "productId": %d,
                        "quantity": 2
                    }
                ]
            }
        """.formatted(testProduct.getId());

        mockMvc.perform(post("/api/v1/orders")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(2599.98))
                .andExpect(jsonPath("$.items.length()").value(1));

        // Verify Database State (Stock deduction in real PostgreSQL)
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(8); // 10 - 2 = 8
    }

    @Test
    @DisplayName("Should reject order with 400 Bad Request when stock is insufficient")
    void shouldFailWhenInsufficientStock() throws Exception {
        String requestJson = """
            {
                "items": [
                    {
                        "productId": %d,
                        "quantity": 15
                    }
                ]
            }
        """.formatted(testProduct.getId());

        mockMvc.perform(post("/api/v1/orders")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        // Verify inventory was untouched
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(10);
    }
}