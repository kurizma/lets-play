package com.jkim.lets_play.service;

import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.repository.ProductRepository;
import com.jkim.lets_play.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    
    // create constructor for DI
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // impl the interface methods
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> getProductsByUserId(String userId) {
        return productRepository.findByUserId(userId);
    }
    
    @Override
    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }
    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    
}