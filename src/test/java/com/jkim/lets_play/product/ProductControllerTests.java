package com.jkim.lets_play.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkim.lets_play.config.SecurityTestConfig;
import com.jkim.lets_play.controller.ProductController;
import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.request.ProductRequest;
import com.jkim.lets_play.service.ProductService;
import com.jkim.lets_play.auth.JwtUtil;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityTestConfig.class)
class ProductControllerTests {
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @MockitoBean
    ProductService productService;
    
    @MockitoBean
    JwtUtil jwtUtil;
    
    @MockitoBean
    UserRepository userRepository;
    
    // Example: create product with missing fields returns 400
    @WithMockUser(username="testuser", roles={"USER"})
    @Test
    void createProduct_ShouldReturn400_WhenFieldsInvalid() throws Exception {
        ProductRequest req = new ProductRequest();
        req.setName("");  // Blank name
        req.setDescription("testing");
        req.setPrice(-1.0); // Invalid price
        
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    
    // Add successful creation cases, unauthorized cases, etc.
}
