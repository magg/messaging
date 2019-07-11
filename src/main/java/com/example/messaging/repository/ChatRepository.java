package com.example.messaging.repository;

import com.example.messaging.domain.Chat;

public interface ChatRepository {

    Chat create(Chat c);

    Chat findOne(Long id);
}
