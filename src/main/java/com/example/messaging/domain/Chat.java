package com.example.messaging.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;


@Table(keyspace = "messaging", name = "chats",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM",
        caseSensitiveKeyspace = false,
        caseSensitiveTable = false)
public class Chat {

    @JsonProperty("id")
    @PartitionKey
    private Long  id;
    @JsonProperty("participantIds")
    @Column(name = "participant_ids")
    private Set<Long> participantIds;

    public Chat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(Set<Long> participantIds) {
        this.participantIds = participantIds;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", participantIds=" + participantIds +
                '}';
    }
}
