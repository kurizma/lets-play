package com.jkim.lets_play.repository;

import com.jkim.lets_play.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    // add custom queries;
}