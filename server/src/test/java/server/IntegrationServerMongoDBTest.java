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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String expectedAns = "[list shops]";
        insertInDataBase(database, "shops", expectedAns);
        Response response = client.get("/shops");
        assertEquals( 200, response.getStatus(), "Get shops method should return good status!");
        assertEquals(expectedAns, response.getBodyUtf8(), "Get shops method should return right doc");
    }

    @Test
    public void getBrands() throws Exception {
        String expectedAns = "[list brands]";
        insertInDataBase(database, "brands", expectedAns);
        Response response = client.get("/brands");
        assertEquals(200, response.getStatus(), "Get brands method should return good status!");
        assertEquals(expectedAns, response.getBodyUtf8(), "Get brands method should return right doc");
    }

    @Test
    public void getShopsBad() throws Exception {
        Response response = client.get("/shops");
        assertEquals(0, response.getBody().length, "Get shops method if doc empty, response should contains empty body");
        assertEquals(404, response.getStatus(), "Get shops method if doc empty, response should has status 404 Not Found!");
    }

    @Test
    public void getBrandsBad() throws Exception {
        Response response = client.get("/shops");
        assertEquals(0, response.getBody().length, "Get brands method if not find doc, response should contains empty body");
        assertEquals(404, response.getStatus(), "Get brands method if not find doc, response should has status 404 Not Found!");
    }

    @Test
    public void getShopsUpdateData() throws Exception {
        String expectedAns = "[new list shops]";
        insertInDataBase(database, "shops", "[list shops]");
        insertInDataBase(database, "shops", expectedAns);
        Response response = client.get("/shops");
        assertEquals( 200, response.getStatus(), "Get shops method should return good status!");
        assertEquals(expectedAns, response.getBodyUtf8(), "Get shops method should return right doc");
    }

    @Test
    public void getBrandsUpdateData() throws Exception {
        String expectedAns = "[new list brands]";
        insertInDataBase(database, "brands", "[list brands]");
        insertInDataBase(database, "brands", expectedAns);
        Response response = client.get("/brands");
        assertEquals(200, response.getStatus(), "Get brands method should return good status!");
        assertEquals(expectedAns, response.getBodyUtf8(), "Get brands method should return right doc");
    }


    private static void insertInDataBase(MongoDatabase database, String nameCollection, String json) {
        MongoCollection<Document> shopsDocuments = database.getCollection(nameCollection);
        Document document = new Document("id", System.currentTimeMillis());
        document.append("json", json);
        shopsDocuments.insertOne(document);
    }

}

