package com.example.messaging.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.HashSet;
import java.util.Set;


@Table(keyspace = "messaging", name = "users",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
public class User {

    @PartitionKey
    private Long id;
    @Column(name = "contact_list")
    private Set<Long> contactList;
    @Column(name = "chats")
    private Set<Long> chats = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getContactList() {
        return contactList;
    }

    public void setContactList(Set<Long> contactList) {
        this.contactList = contactList;
    }

    public Set<Long> getChats() {
        return chats;
    }

    public void setChats(Set<Long> chats) {
        this.chats = chats;
    }
    public void addChat(Chat chat) {
        this.chats.add(chat.getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", contactList=" + contactList +
                ", chats=" + chats +
                '}';
    }
}
