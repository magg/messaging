package com.example.messaging.data;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.example.messaging.config.CassandraConfig;
import com.example.messaging.domain.User;
import com.example.messaging.repository.UserRepository;


public class DataUserRepository implements UserRepository {

    private Mapper<User> mapper;
    private Session session;
    public DataUserRepository() {
        CassandraConfig cc = new CassandraConfig();
        MappingManager mappingManager= cc.mappingManager(cc.connect(cc.cluster()));
        this.mapper = mappingManager.mapper(User.class);
        this.session = mappingManager.getSession();

    }

    @Override
    public User create(User c) {
        mapper.save(c);
        return c;
    }

    @Override
    public User findOne(Long id) {
        User u = mapper.get(id);
        return u;

    }
}
