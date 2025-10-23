package com.jkim.lets_play.controller;

import com.jkim.lets_play.exception.ForbiddenException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.request.AdminRequest;
import com.jkim.lets_play.request.UserRequest;
import com.jkim.lets_play.response.UserResponse;
import com.jkim.lets_play.service.UserService;
import com.jkim.lets_play.auth.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        System.out.println("DEBUG -> Entered UserController.getAllUsers()");
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public Optional<UserResponse> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserEmail() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponse(
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUserAsAdmin(@Valid @RequestBody UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole("USER"); // Always default; can be changed by update later
        
        User createdUser = userService.createUser(user);
        UserResponse response = new UserResponse(
                createdUser.getName(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
        
        // Get current authenticated user
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Check if user exists in DB
        Optional<User> existingUserOpt = userService.getUserById(id)
                .flatMap(resp -> userService.getUserByEmail(resp.getEmail()));
        
        if (existingUserOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        
        User existingUser = existingUserOpt.get();
        
        // Check if current user is admin
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Users can only edit themselves unless they are admin
        if (!isAdmin && !existingUser.getEmail().equals(currentEmail)) {
            throw new ForbiddenException("You do not have permission to modify this user");
        }
        
        // store old fields before update ( for token regen)
        String oldEmail = existingUser.getEmail();
        String oldRole = existingUser.getRole();
        
        // Merge updates instead of full overwrite
        if (userRequest.getName() != null && !userRequest.getName().isBlank()) {
            existingUser.setName(userRequest.getName());
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            existingUser.setPassword(userRequest.getPassword());
        }
        
        User updated = userService.updateUser(id, existingUser);
        
        // re-issue jwt token
        String newToken = null;
        
        if (!oldEmail.equals(updated.getEmail()) || !oldRole.equals(updated.getRole())) {
            newToken = jwtUtil.generateToken(updated.getEmail(), updated.getRole());
        }

        // prep response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully.");
        response.put("user", new UserResponse(updated.getName(), updated.getEmail(), updated.getRole()));
        if (newToken != null) {
            response.put("token", newToken);
        }
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/promote/{id}")
    public ResponseEntity<String> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody AdminRequest adminRequest) {
        
        // Fetch user entity directly
        User user = userService.getUserEntityById(id);  // FIX: fetch from repository
        user.setRole(adminRequest.getRole().toUpperCase()); // e.g., “ADMIN” or “USER”
        
        userService.updateUser(id, user);
        
        return ResponseEntity.ok("User role updated to " + user.getRole());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser((id));
        return ResponseEntity.ok("User Deleted");
    }
    
}
