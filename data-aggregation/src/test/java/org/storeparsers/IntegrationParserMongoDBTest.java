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

import static com.mongodb.client.model.Sorts.descending;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
    void setUp() {
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
    void testIntegrationParserMongo() {
        StoresParser parser = new StoresParser(mongoClient, "energy_drinks");
        parser.run();
        String brands = parser.getBrands().toString();
        String shops = parser.getShops().toString();

        assertEquals(brands, getFromMongoDb(database, "brands"));
        assertEquals(shops, getFromMongoDb(database, "shops"));
    }

    @Test
    void testIntegrationParserMongoWithOldDataInMongo() {
        insertInDataBase(database, "shops", "[json shops]");
        insertInDataBase(database, "brands", "[json brands]");
        StoresParser parser = new StoresParser(mongoClient, "energy_drinks");
        parser.run();
        String brands = parser.getBrands().toString();
        String shops = parser.getShops().toString();

        assertEquals(brands, getFromMongoDb(database, "brands"));
        assertEquals(shops, getFromMongoDb(database, "shops"));
    }


    private static String getFromMongoDb(MongoDatabase database, String nameCollection) {
        MongoCollection<Document> documents = database.getCollection(nameCollection);
        Document document = documents.find().sort(descending("id")).limit(1).first();
        if (document == null) {
            return "";
        }
        return document.getString("json");
    }

    private static void insertInDataBase(MongoDatabase database, String nameCollection, String json) {
        MongoCollection<Document> shopsDocuments = database.getCollection(nameCollection);
        Document document = new Document("id", System.currentTimeMillis());
        document.append("json", json);
        shopsDocuments.insertOne(document);
    }

}

