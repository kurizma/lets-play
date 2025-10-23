package com.jkim.lets_play.service;

import com.jkim.lets_play.model.User;
import com.jkim.lets_play.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<UserResponse> getUserById(String id);
    Optional<User> getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    User updateUser(String id, User user);
    User getUserEntityById(String id);
    void deleteUser(String id);
}
