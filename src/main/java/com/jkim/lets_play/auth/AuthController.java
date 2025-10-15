package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
import com.jkim.lets_play.dto.LoginRequest;
import com.jkim.lets_play.dto.RegisterRequest;
import com.jkim.lets_play.response.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;
    
    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.authenticate(
                loginRequest.getEmail(), loginRequest.getPassword()
                );
        return new LoginResponse("You have logged in successfully!", user);
    }
    
    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        return userService.createUser(user);
    }
    
}