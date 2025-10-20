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
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User userDetail) {
        // userDetail should have updated fields, id is from the path
        return userService.updateUser(id, userDetail);
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
