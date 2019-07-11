package com.example.messaging.controller;

import com.example.messaging.domain.*;
import com.example.messaging.http.Handler;
import com.example.messaging.http.ResponseEntity;
import com.example.messaging.http.exceptions.ApplicationExceptions;
import com.example.messaging.http.exceptions.GlobalExceptionHandler;
import com.example.messaging.service.ChatService;
import com.example.messaging.service.MessageService;
import com.example.messaging.service.UserService;
import com.example.messaging.utils.FileReader;
import com.example.messaging.utils.ParameterResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.messaging.domain.Constants.HTTP_GET;
import static com.example.messaging.domain.Constants.HTTP_POST;
import static com.example.messaging.utils.APIUtils.splitQuery;

public class ChatController extends Handler {

    private final ChatService chatService;

    private final FileReader fileReader;

    private final UserService userService;

    private final MessageService messageService;

    public ChatController(ChatService chatService,
                          ObjectMapper objectMapper,
                          GlobalExceptionHandler exceptionHandler, FileReader fileReader, UserService userService, MessageService messageService ) {
        super(objectMapper, exceptionHandler);
        this.chatService = chatService;
        this.fileReader = fileReader;
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {

        final ParameterResolver parameterResolver = new ParameterResolver("/chats/{chatId}/messages");
        final Map<String, String> resultMap = parameterResolver.parametersByName(exchange.getRequestURI().toString());
        byte[] response;
        if (HTTP_POST.equals(exchange.getRequestMethod()) && resultMap.isEmpty()) {
            ResponseEntity e = createChat(exchange.getRequestBody());
            exchange.getResponseHeaders().putAll(e.getHeaders());
            exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);
            response = super.writeResponse(e.getBody());
        } else if (HTTP_POST.equals(exchange.getRequestMethod()) && !resultMap.isEmpty()) {

            try {
                // to handle message-tools
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResponseEntity e = postMessage(exchange.getRequestBody(),resultMap.get("chatId") );
            exchange.getResponseHeaders().putAll(e.getHeaders());
            exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);
            response = super.writeResponse(e.getBody());

        } else if (HTTP_GET.equals(exchange.getRequestMethod()) && resultMap.isEmpty()) {

            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());

            ResponseEntity e = getHandlerQueryURL(params);
            exchange.getResponseHeaders().putAll(e.getHeaders());
            exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);

            response = super.writeResponse(e.getBody());


        } else if (HTTP_GET.equals(exchange.getRequestMethod()) && !resultMap.isEmpty()) {

            ResponseEntity e = getHandlerPath(resultMap);
            exchange.getResponseHeaders().putAll(e.getHeaders());
            exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);

            response = super.writeResponse(e.getBody());

        } else {
            throw ApplicationExceptions.methodNotAllowed(
                    "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity<?> getHandlerQueryURL(Map<String, List<String>> params ){

        Optional<String> userIdStr = params.get("userId").stream().findFirst();

        if (userIdStr.isPresent()){
            Optional<User> u = userService.findOne(Long.parseLong(userIdStr.get()));
            if (u.isPresent()){

                List<Chat> chats = chatService.findChatPerUser(u.get().getChats());

                return new ResponseEntity<>(chats,
                        getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
            }
        }

        ErrorResponse err = ErrorResponse
                .newBuilder()
                .code(StatusCode.NOT_FOUND.ordinal())
                .message("User ID NOT found")
                .build();

        return new ResponseEntity<>(err,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.NOT_FOUND);

    }



    private ResponseEntity<?> getHandlerPath(Map<String, String> resultMap){

        Long chatId = Long.parseLong(resultMap.get("chatId"));

        Optional<Chat> chat = chatService.findOne(chatId);


        if (chat.isPresent()){
            List<Message> messages = messageService.findAllChatMessages(chat.get());
            return new ResponseEntity<>(messages,
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);

        } else {

            ErrorResponse err = ErrorResponse
                    .newBuilder()
                    .code(StatusCode.NOT_FOUND.ordinal())
                    .message("Chat ID NOT found")
                    .build();

            return new ResponseEntity<>(err
                    ,
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.NOT_FOUND);

        }

    }


    private ResponseEntity<?> createChat(InputStream is) {


        Chat chat = super.readRequest(is, Chat.class);

        JsonNode jsonMap = fileReader.read(chat);
        JsonNode chatSchema = fileReader.getChatSchema();
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema;
        ProcessingReport report;

        try {
            schema = factory.getJsonSchema(chatSchema);
            report = schema.validate(jsonMap);

            if (!report.isSuccess()){

                ErrorResponse err = ErrorResponse
                        .newBuilder()
                        .code(StatusCode.BAD_REQUEST.ordinal())
                        .message("Invalid JSON format")
                        .build();
                return new ResponseEntity<>(
                        err,
                        getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.BAD_REQUEST);
            }

        } catch (ProcessingException e) {
            e.printStackTrace();
        }


        Optional<Chat> c = chatService.createChat(chat);

        return c.map(value -> new ResponseEntity<>(

                value,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK)).orElseGet(() -> new ResponseEntity<>(

                null,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.BAD_REQUEST));


    }

    private ResponseEntity<?> postMessage(InputStream is, String chatId) {
        Message message = super.readRequest(is, Message.class);

        System.out.println(message);


        JsonNode jsonMap =fileReader.read(message);
        JsonNode messageSchema = fileReader.getMessageSchema();
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema;
        ProcessingReport report;

        try {
            schema = factory.getJsonSchema(messageSchema);
            report = schema.validate(jsonMap);

            if (!report.isSuccess()){

                ErrorResponse err = ErrorResponse
                        .newBuilder()
                        .code(StatusCode.BAD_REQUEST.ordinal())
                        .message("Invalid JSON format ")
                        .build();

                return new ResponseEntity<>(
                        err,
                        getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.BAD_REQUEST);
            }

        } catch (ProcessingException e) {
            e.printStackTrace();
        }

        message.setChatId(Long.parseLong(chatId));

        Optional<Message> result = messageService.send(message);


        return result.map(value -> new ResponseEntity<>(

                value,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK)).orElseGet(() -> new ResponseEntity<>(

                null,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.BAD_REQUEST));


    }
}
