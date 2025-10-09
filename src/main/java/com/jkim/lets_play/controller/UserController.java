package com.jkim.lets_play.controller;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    
    @GetMapping("/me")
    public User getCurrentUser() {
        // Temporary placeholder (replace when you add authentication)
        // Later, extract current user from security context
        return userService.getAllUsers().get(0);
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User userDetail) {
        // userDetail should have updated fields, id is from the path
        return userService.updateUser(id, userDetail);
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser((id));
    }
    
}
