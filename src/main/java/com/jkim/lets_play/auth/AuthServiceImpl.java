package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    
    @Autowired
    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User authenticate(String email, String password) {
        System.out.println("Trying to find user with email: " + email);
        Optional<User> foundUser = userRepository.findByEmail(email);
        System.out.println("User found? " + foundUser.isPresent());
        if (foundUser.isPresent() && foundUser.get().getPassword().equals(password)) {
            return foundUser.get();
        }
        return null; // throw a custom exception if needed, maybe look into this when we get to that part
    }
    
}