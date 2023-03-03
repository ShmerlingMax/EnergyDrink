package server;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.Transitions;
import one.nio.http.HttpClient;
import one.nio.http.HttpServer;
import one.nio.http.Response;
import one.nio.net.ConnectionString;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static server.Server.createConfigFromPort;

public class IntegrationServerMongoDBTest {
    private static Transitions transitions;

    private HttpServer server;
    private HttpClient client;
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
        server = new HttpServerImpl(createConfigFromPort(8080), database);
        server.start();
        client = new HttpClient(new ConnectionString("http://127.0.0.1:8080"));
    }

    @AfterEach
    void tearDown() {
        client.close();
        server.stop();
        mongoClient.close();
        running.close();
    }

    @Test
    public void getShops() throws Exception {
        insertInDataBase(database, "shops", "[list shops]");
        Response response = client.get("/shops");
        System.out.println("JOPA: " + new String(response.getBody()));
    }

    @Test
    public void getBrands() throws Exception {
        insertInDataBase(database, "brands", "[list brands]");
        Response response = client.get("/shops");
        System.out.println("JOPA: " + new String(response.getBody()));
    }

    @Test
    public void getShopsBad() throws Exception {
        //insertInDataBase(database, "shops", "[list shops]");
        Response response = client.get("/shops");
        System.out.println("JOPA: " + new String(response.getBody()));
    }

    @Test
    public void getBrandsBad() throws Exception {
        //insertInDataBase(database, "brands", "[list brands]");
        Response response = client.get("/shops");
        System.out.println("JOPA: " + response);
    }



    private static void insertInDataBase(MongoDatabase database, String nameCollection, String json) {
        MongoCollection<Document> shopsDocuments = database.getCollection(nameCollection);
        Document document = new Document("id", System.currentTimeMillis());
        document.append("json", json);
        shopsDocuments.insertOne(document);
    }

}

