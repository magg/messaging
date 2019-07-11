package com.example.messaging.service;

import com.example.messaging.domain.Chat;
import com.example.messaging.domain.Message;
import com.example.messaging.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

public class MessageService {

    private MessageRepository messageRepository;
    private ChatService chatService;


    public MessageService(MessageRepository messageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    public Optional<Message> send(Message msg){

        Optional<Chat> chat = chatService.findOne(msg.getChatId());

        if (chat.isPresent()){
            msg = messageRepository.send(msg);
            return Optional.ofNullable(msg);
        }
        return Optional.empty();
    }

    public List<Message> findAllChatMessages(Chat chat){
        return messageRepository.findAll(chat);
    }
}
