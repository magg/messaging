package com.example.messaging;

import com.example.messaging.controller.ChatController;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static com.example.messaging.Configuration.*;

public class Application {



    public static void main(String[] args) throws Exception {

        getFileReader().loadContacts();

        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);

        ChatController chatHandler = new ChatController(getChatService(), getObjectMapper(),
                getErrorHandler(), getFileReader(), getUserService(), getMessageService());
        server.createContext("/chats", chatHandler::handle);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

}
