package com.jkim.lets_play.controller;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.response.UserResponse;
import com.jkim.lets_play.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    
//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable String id, @RequestBody User userDetail) {
//        // userDetail should have updated fields, id is from the path
//        return userService.updateUser(id, userDetail);
//    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody User userDetail) {
        
        // Get current authenticated user
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Check if user exists in DB
        Optional<User> existingUser = userService.getUserById(id)
                .flatMap(resp -> userService.getUserByEmail(resp.getEmail()));
        
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if current user is admin
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Users can only edit themselves unless they are admin
        if (!isAdmin && !existingUser.get().getEmail().equals(currentEmail)) {
            return ResponseEntity.status(403).build();
        }
        
        // Merge updates instead of full overwrite
        User userToUpdate = existingUser.get();
        if (userDetail.getName() != null && !userDetail.getName().isBlank()) {
            userToUpdate.setName(userDetail.getName());
        }
        if (userDetail.getPassword() != null && !userDetail.getPassword().isBlank()) {
            userToUpdate.setPassword(userDetail.getPassword());
        }
        if (isAdmin && userDetail.getRole() != null) {
            userToUpdate.setRole(userDetail.getRole()); // Only admins can change roles
        }
        
        User updated = userService.updateUser(id, userToUpdate);
        UserResponse response = new UserResponse(
                updated.getName(),
                updated.getEmail(),
                updated.getRole()
        );
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
