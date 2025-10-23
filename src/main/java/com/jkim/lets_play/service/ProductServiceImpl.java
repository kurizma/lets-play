package com.jkim.lets_play.service;

import com.jkim.lets_play.exception.BadRequestException;
import com.jkim.lets_play.exception.ConflictException;
import com.jkim.lets_play.exception.ResourceNotFoundException;
import com.jkim.lets_play.model.Product;
import com.jkim.lets_play.repository.ProductRepository;
import com.jkim.lets_play.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    // create constructor for DI
    private final ProductRepository productRepository;
    private final ProductService productService;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
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
        
        productRepository.findById(product.getId()).ifPresent(p -> {
            throw new ConflictException("Product with this ID already exists");
        });
        productRepository.findByName(product.getName()).ifPresent(p -> {
            throw new ConflictException("Product with this name already exists");
        });
        return productRepository.save(product);
    }
    
    @Override
    public ProductResponse getProductById(@PathVariable String id) {
        Product p = productService.getProductById(id);
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getUserId());
    }

    
    @Override
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getUserId()))
                .toList();
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
        if (product.getPrice() == null || product.getPrice() < 0) {
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