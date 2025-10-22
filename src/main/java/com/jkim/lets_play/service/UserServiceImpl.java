package com.jkim.lets_play.service;

import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ConflictException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import com.jkim.lets_play.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    // creating constructor for DI
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // impl the interface methods
    @Override
    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists: " + user.getEmail());
        }
        
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("Password cannot be empty");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(user -> new UserResponse(user.getName(), user.getEmail(), user.getRole()))
                .or(() -> {
                    throw new ResourceNotFoundException("User not found with ID " + id);
                });
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email must not be empty");
        }
        
        return userRepository.findByEmail(email)
                .or(() -> { throw new ResourceNotFoundException("No user found with email: " + email); });
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        
        List<User> users = userRepository.findAll();
        
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        
        return users.stream()
                .map(user -> new UserResponse(user.getName(),user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }
    
    @Override
    public User updateUser(String id, User user) {
        User existingUser = userRepository.findById(id)
                        .orElseThrow( () -> new ResourceNotFoundException("User not found with ID: " + id));
        
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()
            && !user.getEmail().equals(existingUser.getEmail())) {
            throw new ConflictException("Email already exists: " + user.getEmail());
        }
        
        if (user.getName() != null ) {
            existingUser.setName(user.getName());
        }
        
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        return userRepository.save(existingUser); // save() updates if id exists
    }
    
    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete — user not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
}

