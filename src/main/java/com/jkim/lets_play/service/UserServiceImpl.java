package com.jkim.lets_play.service;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import com.jkim.lets_play.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(user -> new UserResponse(user.getName(), user.getEmail(), user.getRole()));
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserResponse(user.getName(),user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }
    
    @Override
    public User updateUser(String id, User user) {
        user.setId(id); // Make sure the user has the correct id
        return userRepository.save(user); // save() updates if id exists
    }
    
    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
}

