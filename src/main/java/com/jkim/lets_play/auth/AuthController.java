package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
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
    public User login(@RequestParam String email, @RequestParam String password) {
        return authService.authenticate(email, password);
    }
    
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        // delegate to UserService, add validation here also
        return userService.createUser(user);
    }
    
}