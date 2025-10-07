package com.jkim.lets_play.repository;

import  com.jkim.lets_play.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // add custom queries
}

