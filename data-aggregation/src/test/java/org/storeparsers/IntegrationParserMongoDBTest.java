package org.storeparsers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class IntegrationParserMongoDBTest {
    private static Transitions transitions;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private TransitionWalker.ReachedState<RunningMongodProcess> running;

    @BeforeAll
    public static void setAll() {
        transitions = Mongod.instance().transitions(Version.Main.V4_4);
    }

    @BeforeEach
    void setUp() throws IOException {
        running = transitions.walker().initState(StateID.of(RunningMongodProcess.class));
        mongoClient = new MongoClient(running.current().getServerAddress().getHost(),
                running.current().getServerAddress().getPort());
        database = mongoClient.getDatabase("energy_drinks");
    }

    @AfterEach
    void tearDown() {
        mongoClient.close();
        running.close();
    }

    @Test
    void someTest() {}


    private static void insertInDataBase(MongoDatabase database, String nameCollection, String json) {
        MongoCollection<Document> shopsDocuments = database.getCollection(nameCollection);
        Document document = new Document("id", System.currentTimeMillis());
        document.append("json", json);
        shopsDocuments.insertOne(document);
    }

}

