package com.jkim.lets_play.repository;

import com.jkim.lets_play.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    // add custom queries;
    List<Product> findByUserId(String userId);
}