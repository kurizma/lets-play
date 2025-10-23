package com.jkim.lets_play.request;

import jakarta.validation.constraints.NotBlank;

public class AdminRequest {
    @NotBlank
    private String role;
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}