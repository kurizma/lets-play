package com.jkim.lets_play.service;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    // creating constructor for DI
    private final UserRepository userRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // impl the interface methods
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
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

