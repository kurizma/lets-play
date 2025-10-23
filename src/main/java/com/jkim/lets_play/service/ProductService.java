package com.jkim.lets_play.service;

import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.response.ProductResponse;

import java.util.List;

public interface ProductService {
    
    Product createProduct(Product product);
    Product getProductById(String id);
    List<ProductResponse> getAllProducts();
    List<Product> getProductsByUserId(String userId);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
    
}