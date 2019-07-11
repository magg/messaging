package com.example.messaging;

import com.example.messaging.data.DataChatRepository;
import com.example.messaging.data.DataMessageRepository;
import com.example.messaging.data.DataUserRepository;
import com.example.messaging.http.exceptions.GlobalExceptionHandler;
import com.example.messaging.repository.ChatRepository;
import com.example.messaging.repository.MessageRepository;
import com.example.messaging.repository.UserRepository;
import com.example.messaging.service.ChatService;
import com.example.messaging.service.MessageService;
import com.example.messaging.service.UserService;
import com.example.messaging.utils.FileReader;
import com.fasterxml.jackson.databind.ObjectMapper;

class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ChatRepository CHAT_REPOSITORY = new DataChatRepository();
    private static final UserRepository USER_REPOSITORY = new DataUserRepository();

    private static final MessageRepository MESSAGE_REPOSITORY = new DataMessageRepository();

    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    private static final UserService USER_SERVICE = new UserService(USER_REPOSITORY);

    private static final FileReader FILE_READER =  new FileReader(USER_SERVICE, OBJECT_MAPPER);
    private static final ChatService CHAT_SERVICE = new ChatService(CHAT_REPOSITORY,USER_SERVICE);

    private static final MessageService MESSAGE_SERVICE = new MessageService(MESSAGE_REPOSITORY, CHAT_SERVICE);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }

    static ChatRepository getChatRepository() {
        return CHAT_REPOSITORY;
    }

    static ChatService getChatService() {
        return CHAT_SERVICE;
    }

    static UserRepository getUserRepository() {
        return USER_REPOSITORY;
    }

    static UserService getUserService() {
        return USER_SERVICE;
    }

    static FileReader getFileReader(){
        return FILE_READER;
    }

    static MessageService getMessageService() {return MESSAGE_SERVICE ;}
}
