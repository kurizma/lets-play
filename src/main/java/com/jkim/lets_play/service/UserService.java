package com.jkim.lets_play.service;

import com.jkim.lets_play.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(String id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(String id, User user);
    void deleteUser(String id);
}
