package com.example.messaging.data;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.example.messaging.config.CassandraConfig;
import com.example.messaging.domain.Chat;
import com.example.messaging.repository.ChatRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DataChatRepository implements ChatRepository {

    private Mapper<Chat> mapper;
    private Session session;

    public DataChatRepository() {
        CassandraConfig cc = new CassandraConfig();
        MappingManager mappingManager= cc.mappingManager(cc.connect(cc.cluster()));
        this.mapper = mappingManager.mapper(Chat.class);
        this.session = mappingManager.getSession();

    }

    @Override
    public Chat create(Chat c) {
        mapper.save(c);
        return c;
    }

    @Override
    public Chat findOne(Long id){
        Chat c= mapper.get(id);
        return c;
    }
}
