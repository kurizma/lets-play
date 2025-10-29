package com.jkim.lets_play.service;

import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ConflictException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    // create constructor for DI
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // impl the interface methods
    @Transactional
    @Override
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BadRequestException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new BadRequestException("Price must be a positive number");
        }
        productRepository.findByName(product.getName()).ifPresent(p -> {
            throw new ConflictException("Product with this name already exists");
        });
        
        product.setId(null);
        
        System.out.println("DEBUG -> Saving product: " + product.getName() + " | userId=" + product.getUserId());
        Product saved = productRepository.save(product);
        System.out.println("DEBUG -> Product saved successfully with ID: " + saved.getId());
        
        return saved;
    }
    
    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    
    @Override
    public List<Product> getProductsByUserId(String userId) {
        List<Product> products = productRepository.findByUserId(userId);
        // Either return 404 or empty list – depending on design preference - current: 404
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for userId: " + userId);
        }
        return products;
    }
    
    @Transactional
    @Override
    public Product updateProduct(String id, Product product) {
        Product existing = getProductById(id);
        
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BadRequestException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new BadRequestException("Price must be a positive number");
        }
        
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        
        return productRepository.save(existing);
    }
    
    @Transactional
    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
    
}