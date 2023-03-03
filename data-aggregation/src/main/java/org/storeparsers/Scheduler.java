package org.storeparsers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Timer;

public class Scheduler {

    private static final int SIX_HOURS = 21600000;
    private static final int MONGODB_PORT = 27017;
    public static void main(String[] args) {
        Timer timer = new Timer();
        String rootName = System.getenv(Config.MONGO_INITDB_ROOT_USERNAME);
        String password = System.getenv(Config.MONGO_INITDB_ROOT_PASSWORD);
        String databaseName = System.getenv(Config.MONGO_INITDB_DATABASE);
        String host = System.getenv(Config.MONGO_HOSTNAME);

        MongoCredential credential = MongoCredential.createCredential(rootName, databaseName, password.toCharArray());

        ServerAddress serverAddress = new ServerAddress(host, MONGODB_PORT);
        MongoClient mongoClient = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        StoresParser task = new StoresParser(mongoClient, databaseName);
        task.run();
        timer.scheduleAtFixedRate(task, 0, SIX_HOURS);
    }

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}
