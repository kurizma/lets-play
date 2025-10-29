package com.jkim.lets_play.service;

import com.jkim.lets_play.model.Product;

import java.util.List;

public interface ProductService {
    
    Product createProduct(Product product);
    Product getProductById(String id);
    List<Product> getAllProducts();
    List<Product> getProductsByUserId(String userId);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
    
}