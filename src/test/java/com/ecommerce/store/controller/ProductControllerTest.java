package com.ecommerce.store.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest 
@AutoConfigureMockMvc 
public class ProductControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @Test 
    void shouldReturnPaginatedSearchResults() throws Exception {
        mockMvc.perform(get("/api/v1/products/search")
               .param("keyword", "mo")
               .param("page", "0")
               .param("size", "5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.pageNumber").value(0))
        .andExpect(jsonPath("$.pageSize").value(5));
    }
}