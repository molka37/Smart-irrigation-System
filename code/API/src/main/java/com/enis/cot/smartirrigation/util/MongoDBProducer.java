package com.enis.cot.smartirrigation.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class MongoDBProducer {

    @Produces
    @ApplicationScoped
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    public void close(@Disposes MongoClient client) {
        client.close();
    }
}
