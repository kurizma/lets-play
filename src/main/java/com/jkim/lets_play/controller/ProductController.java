package com.jkim.lets_play.controller;

import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }
    
    @GetMapping("/user/{userId}")
    public List<Product> getProductsByUserId(@PathVariable String userId) {
        return productService.getProductsByUserId(userId);
    }
    
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product productDetail) {
        return productService.updateProduct(id, productDetail);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct((id));
    }
    
}
