package com.jkim.lets_play.response;

public class UserResponse {
    private String name;
    
    public UserResponse() {}
    
    public UserResponse(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
