package com.example.messaging.repository;

import com.example.messaging.domain.Chat;
import com.example.messaging.domain.Message;

import java.util.List;

public interface MessageRepository {

    Message send(Message msg);

    List<Message> findAll(Chat id);

}
