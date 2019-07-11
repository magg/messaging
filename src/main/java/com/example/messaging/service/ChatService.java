package com.example.messaging.service;

import com.example.messaging.domain.Chat;
import com.example.messaging.domain.User;
import com.example.messaging.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;

    }

    public Optional<Chat> createChat(Chat c){

        boolean allInContactLists = true;

        List<User> users = new ArrayList<>();

        Set<Long> commons = new HashSet<>();
        for ( Long participantId: c.getParticipantIds()){

            Optional<User> u = userService.findOne(participantId);

            if (u.isPresent()){
                Set<Long> set = u.get().getContactList();
                commons.addAll(set);
                users.add(u.get());
            }
        }

        commons.retainAll(c.getParticipantIds());

        if(commons.size() != c.getParticipantIds().size()){
            allInContactLists = false;
        }

        if (allInContactLists){

            for(User u : users){
                u.addChat(c);
                userService.createUser(u);
            }

            c = chatRepository.create(c);

            return Optional.ofNullable(c);
        }


        return Optional.empty();
    }

    public Optional<Chat> findOne(Long id){
        Chat c = chatRepository.findOne(id);
        return Optional.ofNullable(c);

    }

    public List<Chat> findChatPerUser(Set<Long> chatList){
        List<Chat> list = new ArrayList<>();
        for (Long id: chatList){
            findOne(id).ifPresent(list::add);
        }
        return list;
    }



}
