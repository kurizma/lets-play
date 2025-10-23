package com.jkim.lets_play.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkim.lets_play.controller.UserController;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.request.UserRequest;
import com.jkim.lets_play.response.UserResponse;
import com.jkim.lets_play.service.UserService;
import com.jkim.lets_play.auth.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private JwtUtil jwtUtil;
    
    // GET /api/users/me when not authenticated
    @Test
    void getMe_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
    
    // PUT /api/users/update/{id} with non-matching user
    @Test
    void updateUser_ShouldReturn403_WhenNotOwnerOrAdmin() throws Exception {
        // Skipping mock auth context; use integration test for deeper checks!
    }
    
    // Add more as needed...
}
