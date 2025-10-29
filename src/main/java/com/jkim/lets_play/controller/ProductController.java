package com.jkim.lets_play.controller;

import com.jkim.lets_play.auth.JwtUtil;
import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ForbiddenException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.model.User;
import com.jkim.lets_play.repository.UserRepository;
import com.jkim.lets_play.request.ProductRequest;
import com.jkim.lets_play.response.ProductResponse;
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
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getUserId()))
                .toList();
    }
    
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable String id) {
        Product p = productService.getProductById(id);
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getUserId());
    }

    
    @GetMapping("/{userId}/products")
    public List<ProductResponse> getProductsByUserId(@PathVariable String userId) {
        return productService.getProductsByUserId(userId)
                .stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getUserId()))
                .toList();
    }

    
    // auth'd users can create
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ProductResponse createProduct(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody ProductRequest productRequest) {
        System.out.println("DEBUG -> Entered ProductController.createProduct()");

        // extract token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Missing or invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        System.out.println("DEBUG -> Extracted Token: " + token);
        
        jwtUtil.extractEmail(token);
        String email;
        try {
            email = jwtUtil.extractEmail(token);
            System.out.println("DEBUG -> Extracted Email from token: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ForbiddenException("Failed to extract email from token");
        }
        
        // find user by email, 404 if none
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        System.out.println("DEBUG -> Found user: " + user.getName() + " | ID: " + user.getId());
        
        if (user.getId() == null) {
            throw new IllegalStateException("Authenticated user has no valid database ID!");
        }
        // map request to product
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setUserId(user.getId());
        
        System.out.println("DEBUG -> Product ready to save: "
                + "[Name=" + product.getName()
                + ", Price=" + product.getPrice()
                + ", UserId=" + product.getUserId() + "]");
        
        // save and return
//        Product saved = productService.createProduct(product);
//        return new ProductResponse(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice(), saved.getUserId());
        try {
            Product saved = productService.createProduct(product);
            System.out.println("DEBUG -> Product successfully saved. ID: " + saved.getId());
            return new ProductResponse(
                    saved.getId(),
                    saved.getName(),
                    saved.getDescription(),
                    saved.getPrice(),
                    saved.getUserId()
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR -> Failed to save product: " + e.getMessage());
            throw new BadRequestException("Could not save product: " + e.getMessage());
        }
    }
    

    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequest productRequest) {
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
        ProductResponse response = new ProductResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getPrice(),
                updated.getUserId()
        );
        
        return ResponseEntity.ok(response);

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
