package com.jkim.lets_play.controller;

import com.jkim.lets_play.exception.ForbiddenException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.response.UserResponse;
import com.jkim.lets_play.service.UserService;
import com.jkim.lets_play.auth.JwtUtil;
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
    public ResponseEntity<UserResponse> createUserAsAdmin(@RequestBody User user) {
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }
        
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
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody User userDetail) {
        
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
        if (userDetail.getName() != null && !userDetail.getName().isBlank()) {
            existingUser.setName(userDetail.getName());
        }
        if (userDetail.getPassword() != null && !userDetail.getPassword().isBlank()) {
            existingUser.setPassword(userDetail.getPassword());
        }
        
        // only Admins can change role or email
        if (isAdmin) {
            if (userDetail.getRole() != null && !userDetail.getRole().isBlank()) {
                existingUser.setRole(userDetail.getRole());
            }
            if (userDetail.getEmail() != null && !userDetail.getEmail().isBlank()) {
                existingUser.setEmail(userDetail.getEmail());
            }
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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser((id));
        return ResponseEntity.ok("User Deleted");
    }
    
//    // futureCase - promote
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{id}/promote")
//    public ResponseEntity<String> promoteUser(@PathVariable String id) {
//        Optional<User> optionalUser = userService.getUserById(id);
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            user.setRole("ADMIN");
//            userService.updateUser(id, user);
//            return ResponseEntity.ok("User promoted to ADMIN.");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//

}
