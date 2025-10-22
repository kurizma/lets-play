package com.jkim.lets_play.model;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// tell mongo to use products
@Document(collection = "Product")
public class Product {
    
    @Id
    private String id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Product description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Positive(message = "Price must be greater than zero")
    private Double price;
    
    private String userId;
    
    public Product() {}
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getUserId() { return userId; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setUserId(String userId) { this.userId = userId; }
}