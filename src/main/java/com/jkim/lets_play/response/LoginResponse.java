package com.jkim.lets_play.response;

import com.jkim.lets_play.model.User;

public class LoginResponse {
    
    private final String message;
    private final String token;
    private final UserResponse user;
    
    public LoginResponse(String message, String token, UserResponse user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }
    
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserResponse getUser() { return user; }
    
}