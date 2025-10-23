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
//        System.out.println("DEBUG -> Email: " + email);
//        System.out.println("DEBUG -> Password (raw): " + password);
        
        // user check
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("No user found with email: " + email);
        }
        
        User user = foundUser.get();
//        System.out.println("DEBUG -> User found: " + user.getEmail());
//        System.out.println("DEBUG -> Stored hash: " + user.getPassword());

        
        // password validation check
        boolean matches = passwordEncoder.matches(password, user.getPassword());
//        System.out.println("DEBUG -> Password match result: " + matches);
        
        if (!matches) {
            System.out.println("DEBUG -> Password does NOT match for email: " + email);
            throw new AuthException("Invalid email or password");
        }
        
//        System.out.println("DEBUG -> Authentication successful for: " + user.getEmail()
//                + " | Role: " + user.getRole());
        
        // auth success >> Return auth users
        return user;
        
    }
    
}