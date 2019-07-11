package com.example.messaging.data;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.example.messaging.config.CassandraConfig;
import com.example.messaging.domain.Chat;
import com.example.messaging.domain.Message;
import com.example.messaging.repository.MessageRepository;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

public class DataMessageRepository implements MessageRepository {

    private Mapper<Message> mapper;
    private Session session;
    private static final String TABLE = "messages";

    public DataMessageRepository() {
        CassandraConfig cc = new CassandraConfig();
        MappingManager mappingManager= cc.mappingManager(cc.connect(cc.cluster()));
        this.mapper = mappingManager.mapper(Message.class);
        this.session = mappingManager.getSession();
    }

    @Override
    public Message send(Message msg) {
       mapper.save(msg);
       return msg;
    }

    @Override
    public List<Message> findAll(Chat chat) {
        Statement query = select()
                .all()
                .from(TABLE)
                .where(eq("chat_id", chat.getId()));
        final ResultSet result = session.execute(query);
        return mapper.map(result).all();
    }
}
