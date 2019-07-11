package com.example.messaging.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.*;

import static com.datastax.driver.mapping.NamingConventions.LOWER_CAMEL_CASE;
import static com.datastax.driver.mapping.NamingConventions.LOWER_SNAKE_CASE;

public class CassandraConfig {



    public Cluster cluster(){
        // Connect to the cluster and keyspace
        return Cluster.builder()
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .withCredentials("cassandra", "cassandra")
                .withoutJMXReporting()
                .withoutMetrics().build();
    }

    public Session connect(Cluster cluster){
        return cluster.connect("messaging");
    }

    public MappingManager mappingManager(Session session) {
        final PropertyMapper propertyMapper =
                new DefaultPropertyMapper()
                        .setNamingStrategy(new DefaultNamingStrategy(LOWER_CAMEL_CASE, LOWER_SNAKE_CASE));
        final MappingConfiguration configuration =
                MappingConfiguration.builder().withPropertyMapper(propertyMapper).build();
        return new MappingManager(session, configuration);
    }
}
