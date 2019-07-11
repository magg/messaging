package com.example.messaging.service;

import com.example.messaging.domain.User;
import com.example.messaging.repository.UserRepository;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User u){
        return userRepository.create(u);
    }

    public Optional<User> findOne(Long id){
        User u = userRepository.findOne(id);
        return Optional.ofNullable(u);

    }

}
