package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
import com.jkim.lets_play.dto.LoginRequest;
import com.jkim.lets_play.dto.RegisterRequest;
import com.jkim.lets_play.response.LoginResponse;
import com.jkim.lets_play.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthController(AuthService authService, UserService userService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(
                loginRequest.getEmail(), loginRequest.getPassword()
                );
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        
        UserResponse userResponse = new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
        return new LoginResponse("You have logged in successfully!", token, userResponse);
    }
    
    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole("USER");
        return userService.createUser(user);
    }
    
}