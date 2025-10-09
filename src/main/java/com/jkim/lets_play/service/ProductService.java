package com.jkim.lets_play.service;

import com.jkim.lets_play.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    Product createProduct(Product product);
    Optional<Product> getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getProductsByUserId(String userId);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
    
}