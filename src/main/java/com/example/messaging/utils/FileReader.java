package com.example.messaging.utils;

import com.example.messaging.domain.User;
import com.example.messaging.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileReader {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    private JsonNode chatSchema = null;
    private JsonNode messageSchema = null;


    public FileReader(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;

        try {
            InputStream in = getClass().getResourceAsStream("/chat.json");
            final JsonNode chatSchema = objectMapper.readTree(in);
            InputStream in2 = getClass().getResourceAsStream("/message.json");
            final JsonNode messageSchema = objectMapper.readTree(in2);
            setChatSchema(chatSchema);
            setMessageSchema(messageSchema);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Long, Set<Long>> readContacts(){

        Map<Long, Set<Long>> map = new HashMap<>();

        InputStream in = getClass().getResourceAsStream("/contacts.json");

        try {

            String json = convert(in);
            map = objectMapper.readValue(json, new TypeReference<Map<Long, Set<Long>>>() {});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }


    private String convert(InputStream inputStream) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    public void loadContacts(){
        Map<Long, Set<Long>> map = readContacts();

        for (Map.Entry<Long, Set<Long>> entry : map.entrySet()) {
            User u = new User();
            u.setId(entry.getKey());
            u.setContactList(entry.getValue());
            userService.createUser(u);
        }
    }

    public void setChatSchema( JsonNode chatSchema){
        this.chatSchema = chatSchema;
    }

    public JsonNode getChatSchema(){
        return chatSchema;
    }

    public JsonNode getMessageSchema() {
        return messageSchema;
    }

    public void setMessageSchema(JsonNode messageSchema) {
        this.messageSchema = messageSchema;
    }

    public JsonNode read(Object o){
        return objectMapper.convertValue(o, JsonNode.class);
    }
}
