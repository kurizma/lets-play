package com.jkim.lets_play.auth;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User authenticate(String email, String password) {
        // System.out.println("Trying to find user with email: " + email);
        Optional<User> foundUser = userRepository.findByEmail(email);
        // System.out.println("User found? " + foundUser.isPresent());
        if (foundUser.isPresent() && passwordEncoder.matches(password, foundUser.get().getPassword())) {
            return foundUser.get();
        }
        return null; // throw a custom exception if needed, maybe look into this when we get to that part
    }
    
}