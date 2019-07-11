package com.example.messaging.repository;

import com.example.messaging.domain.User;

public interface UserRepository {

    User create(User c);

    User findOne(Long id);

}
