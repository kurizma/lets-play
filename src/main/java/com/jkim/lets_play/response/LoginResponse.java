package com.jkim.lets_play.response;

import com.jkim.lets_play.model.User;

public class LoginResponse {
    
    private final  String message;
    private final User user;
    
    public LoginResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }
    
    public String getMessage() { return message; }
    public User getUser() { return user; }
    
}