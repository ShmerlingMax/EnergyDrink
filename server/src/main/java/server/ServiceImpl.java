package server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.server.AcceptorConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public final class ServiceImpl implements Service {

    private HttpServer server;

    MongoClient mongoClient;

    ServiceImpl() {}

    private static HttpServerConfig createConfigFromPort(int port) {
        HttpServerConfig httpConfig = new HttpServerConfig();
        AcceptorConfig acceptor = new AcceptorConfig();
        acceptor.port = port;
        acceptor.reusePort = true;
        httpConfig.acceptors = new AcceptorConfig[]{acceptor};
        return httpConfig;
    }

    @Override
    public CompletableFuture<?> stop() throws IOException {
        mongoClient.close();
        server.stop();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<?> start() throws IOException {
        MongoCredential credential = MongoCredential.createCredential("root",
                "energy_drinks",
                "pass12345".toCharArray());

        ServerAddress serverAddress = new ServerAddress("173.18.0.3", 27017);
        mongoClient = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        MongoDatabase database = mongoClient.getDatabase("energy_drinks");
        server = new HttpServerImpl(createConfigFromPort(8080), database);
        server.start();
        return CompletableFuture.completedFuture(null);
    }

}
