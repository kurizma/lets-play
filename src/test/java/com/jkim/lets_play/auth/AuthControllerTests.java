package com.jkim.lets_play.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jkim.lets_play.config.SecurityTestConfig;
import com.jkim.lets_play.exception.AuthException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.request.LoginRequest;
import com.jkim.lets_play.request.RegisterRequest;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
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
    private JwtUtil jwtUtil;
    
    // --- Register endpoint validation ---
    @Test
    void register_ShouldReturn400_WhenInvalidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setName(""); // Blank name
        request.setPassword("123"); // Too short
        
        mockMvc.perform(post("/api/auth/register")
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
        
        User returnedUser = new User();
        returnedUser.setName("John Doe");
        returnedUser.setEmail("john@example.com");
        returnedUser.setPassword("securePass");
        returnedUser.setRole("USER");
        Mockito.when(userService.createUser(Mockito.any(User.class)))
                .thenReturn(returnedUser);
        
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("John Doe")));
    }
    
    // --- Login endpoint validation ---
    @Test
    void login_ShouldReturn400_WhenMissingFields() throws Exception {
        LoginRequest request = new LoginRequest(); // empty
        
        mockMvc.perform(post("/api/auth/login")
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
        mockUser.setEmail("john@example.com");
        mockUser.setPassword("123456");
        mockUser.setRole("USER");
        
        Mockito.when(authService.authenticate("john@example.com", "123456"))
                .thenReturn(mockUser);
        Mockito.when(jwtUtil.generateToken(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("mocked.jwt.token");
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("logged in successfully")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("mocked.jwt.token")));
    }
    
    // --- Login fails for invalid password ---
    @Test
    void login_ShouldReturn401_WhenInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongpass");
        
        Mockito.when(authService.authenticate("john@example.com", "wrongpass"))
                .thenThrow(new AuthException("Invalid email or password"));
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid email or password")));
    }
    
    // --- Login fails for unknown email ---
    @Test
    void login_ShouldReturn404_WhenUserNotFound() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("nobody@example.com");
        request.setPassword("something");
        
        Mockito.when(authService.authenticate("nobody@example.com", "something"))
                .thenThrow(new ResourceNotFoundException("No user found with email"));
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
