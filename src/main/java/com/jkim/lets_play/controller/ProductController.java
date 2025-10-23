package com.jkim.lets_play.controller;

import com.jkim.lets_play.auth.JwtUtil;
import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ForbiddenException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import com.jkim.lets_play.request.ProductRequest;
import com.jkim.lets_play.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    @Autowired
    public ProductController(ProductService productService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.productService = productService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }
    
    @GetMapping("/user/{userId}")
    public List<Product> getProductsByUserId(@PathVariable String userId) {
        return productService.getProductsByUserId(userId);
    }
    
    // auth'd users can create
    @PostMapping
    public Product createProduct(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ProductRequest productRequest) {
        
        // extract token
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        
        // find user by email, 404 if none
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // map request to product
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setUserId(user.getId());
        
        // save and return
        return productService.createProduct(product);
    }
    

    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequest productRequest) {
        // get current auth user email
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Fetch user
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + currentEmail));
        
        // fetch product to be updated
        Product existingProduct = productService.getProductById(id);
        
        //chk if admin
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // only admin or owner can modify product
        if (!isAdmin && !existingProduct.getUserId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to modify this product");
        }
        
        // update only allowed fields from DTO
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        
        Product updated = productService.updateProduct(id, existingProduct);
        return ResponseEntity.ok(updated);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentEmail));
        
        Product product = productService.getProductById(id);
        
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !product.getUserId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to delete this product");
        }
        
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
    
}
