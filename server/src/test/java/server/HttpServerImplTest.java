package server;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import one.nio.http.HttpClient;
import one.nio.http.HttpException;
import one.nio.http.HttpServer;
import one.nio.http.Response;
import one.nio.net.ConnectionString;
import one.nio.pool.PoolException;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.mongodb.client.model.Sorts.descending;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static server.Server.createConfigFromPort;

@ExtendWith(MockitoExtension.class)
class HttpServerImplTest {

    private static final int PORT = 8080;
    private HttpServer server;
    private HttpClient client;

    @Mock
    private MongoDatabase database;
    @Mock
    private MongoCollection<Document> collection;
    @Mock
    private FindIterable<Document> iterable;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        server = new HttpServerImpl(createConfigFromPort(PORT), database);
        server.start();
        client = new HttpClient(new ConnectionString("http://127.0.0.1:8080"));
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void handleBadPath() throws HttpException, IOException, PoolException, InterruptedException, ExecutionException, TimeoutException {
        Response response = client.get("/bad_path");
        assertEquals(0, response.getBody().length, "When bad path, response should contains empty body");
        assertEquals(response.getStatus(), 400, "When bad path, response should has status 400 Bad Request!");
    }

    @Test
    void handlePutMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Response response = client.put("/brands");
        assertEquals(0, response.getBody().length, "Put method not allowed, response should contains empty body");
        assertEquals(response.getStatus(), 405, "Put method not allowed, response should has status 405 Method Not Allowed!");
    }

    @Test
    void handleDeleteMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Response response = client.delete("/shops");
        assertEquals(0, response.getBody().length, "Delete method not allowed, response should contains empty body");
        assertEquals(response.getStatus(), 405, "Delete method not allowed, response should has status 405 Method Not Allowed!");
    }


    @Test
    void handleHeadBrandsMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Response response = client.head("/brands");
        assertEquals(200, response.getStatus(), "Head method /brands allowed, response should has status 200!");
        assertNull(response.getBody(), "Head method shouldn't have body");
    }

    @Test
    void handleHeadShopsMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Response response = client.head("/shops");
        assertEquals(200, response.getStatus(), "Head method /shops allowed, response should has status 200!");
        assertNull(response.getBody(), "Head method shouldn't have body");
    }

    @Test
    void handleGetShopsMethodWhenEmptyDb() throws HttpException, IOException, PoolException, InterruptedException {
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.SHOPS, null);
        Response response = client.get("/shops");
        assertEquals(0, response.getBody().length, "Get shops method if not find doc, response should contains empty body");
        assertEquals(404,response.getStatus(), "Get shops method if not find doc, response should has status 404 Not Found!");
    }

    @Test
    void handleGetBrandsMethodWhenEmptyDocument() throws HttpException, IOException, PoolException, InterruptedException {
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.BRANDS, new Document());
        Response response = client.get("/brands");
        assertEquals(0, response.getBody().length, "Get brands method if doc empty, response should contains empty body");
        assertEquals(404, response.getStatus(), "Get brands method if doc empty, response should has status 404 Not Found!");
    }

    @Test
    void handleGetBrandsMethodWhenEmptyDb() throws HttpException, IOException, PoolException, InterruptedException {
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.BRANDS, null);
        Response response = client.get("/brands");
        assertEquals(0, response.getBody().length, "Get brands method if not find doc, response should contains empty body");
        assertEquals(404, response.getStatus(), "Get brands method if not find doc, response should has status 404 Not Found!");
    }

    @Test
    void handleGetShopsMethodWhenEmptyDocument() throws HttpException, IOException, PoolException, InterruptedException {
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.SHOPS, new Document());
        Response response = client.get("/shops");
        assertEquals(0, response.getBody().length, "Get shops method if doc empty, response should contains empty body");
        assertEquals(response.getStatus(), 404, "Get shops method if doc empty, response should has status 404 Not Found!");
    }

    @Test
    void handleGetBrandsMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Document brandsDocument = new Document("id", 123);
        String expectedAns = "[list brands]";
        brandsDocument.append("json", expectedAns);
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.BRANDS, brandsDocument);
        Response response = client.get("/brands");
        assertEquals(response.getStatus(), 200, "Get brands method should return good status!");
        assertEquals(expectedAns, new String(response.getBody()), "Get brands method should return right doc");
    }

    @Test
    void handleGetShopsMethod() throws HttpException, IOException, PoolException, InterruptedException {
        Document brandsDocument = new Document("id", 123);
        String expectedAns = "[list shops]";
        brandsDocument.append("json", expectedAns);
        mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb.SHOPS, brandsDocument);
        Response response = client.get("/shops");
        assertEquals(response.getStatus(), 200, "Get shops method should return good status!");
        assertEquals(expectedAns, new String(response.getBody()), "Get shops method should return right doc");
    }

    private void mockWorkWithMongo(HttpServerImpl.CollectionsMongoDb shops, Document brandsDocument) {
        when(database.getCollection(shops.name)).thenReturn(collection);
        when(collection.find()).thenReturn(iterable);
        when(iterable.sort(descending("id"))).thenReturn(iterable);
        when(iterable.limit(1)).thenReturn(iterable);
        when(iterable.first()).thenReturn(brandsDocument);
    }
}