package com.example.messaging.domain;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@Table(keyspace = "messaging", name = "messages",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    private UUID id;
    @JsonIgnore
    @PartitionKey(0)
    @Column(name = "chat_id")
    private Long chatId;
    @ClusteringColumn(0)
    @Column(name = "time")
    private Long timestamp;
    private String message;
    @Column(name = "source_user_id")
    private Long sourceUserId;
    @Column(name = "destination_user_id")
    private Long destinationUserId;

    public Message() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(Long destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", sourceUserId=" + sourceUserId +
                ", destinationUserId=" + destinationUserId +
                '}';
    }
}
