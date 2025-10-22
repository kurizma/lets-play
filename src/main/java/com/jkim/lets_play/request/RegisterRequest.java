package com.jkim.lets_play.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest{
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 24, message = "Password must be between 6 and 24 characters")
    private String password;
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    
}

//@Pattern(
//        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{6,24}$",
//        message = "Password must contain a number, an uppercase letter, and a special character"
//)
