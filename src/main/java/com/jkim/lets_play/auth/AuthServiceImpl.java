package com.jkim.lets_play.auth;

import com.jkim.lets_play.exception.AuthException;
import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
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
        
        // basic input check
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }
        
        if (password== null || password.isBlank()) {
            throw new BadRequestException("Password cannot be empty");
            
        }
        
        // user check
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("No user found with email: " + email);
        }
        
        User user = foundUser.get();

        // password validation check
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        
        // auth success >> Return auth users
        return user;
        
    }
    
}