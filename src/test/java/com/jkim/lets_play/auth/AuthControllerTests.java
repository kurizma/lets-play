package com.jkim.lets_play.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkim.lets_play.dto.LoginRequest;
import com.jkim.lets_play.dto.RegisterRequest;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper; // Converts objects to JSON
    
    @MockitoBean
    private AuthService authService;
    
    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private JwtUtil jwtUtil
    
    // --- Register endpoint validation ---
    @Test
    void register_ShouldReturn400_WhenInvalidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email"); // Invalid email
        request.setName(""); // Blank name
        request.setPassword("123"); // Too short
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    // --- Register endpoint success ---
    @Test
    void register_ShouldReturn200_WhenValidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("securePass");
        
        Mockito.when(userService.createUser(Mockito.any(User.class)))
                .thenReturn(new User());
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    // --- Login endpoint validation ---
    @Test
    void login_ShouldReturn400_WhenMissingFields() throws Exception {
        LoginRequest request = new LoginRequest(); // empty, invalid
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    // --- Login endpoint success ---
    @Test
    void login_ShouldReturn200_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("123456");
        
        User mockUser = new User();
        mockUser.setName("John Doe");
        Mockito.when(authService.authenticate("john@example.com", "123456"))
                .thenReturn(mockUser);
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("logged in successfully")));
    }
}
