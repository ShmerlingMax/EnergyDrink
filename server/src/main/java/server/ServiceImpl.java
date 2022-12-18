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
import java.util.concurrent.CompletableFuture;

public final class ServiceImpl implements Service {

    private HttpServer server;

    private static final int SERVER_PORT = 8080;
    private static final int MONGODB_PORT = 27017;
    MongoClient mongoClient;

    ServiceImpl() {
    }

    private static HttpServerConfig createConfigFromPort() {
        HttpServerConfig httpConfig = new HttpServerConfig();
        AcceptorConfig acceptor = new AcceptorConfig();
        acceptor.port = SERVER_PORT;
        acceptor.reusePort = true;
        httpConfig.acceptors = new AcceptorConfig[]{acceptor};
        return httpConfig;
    }

    @Override
    public CompletableFuture<?> stop() {
        mongoClient.close();
        server.stop();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<?> start() throws IOException {
        String rootName = System.getenv(Config.MONGO_INITDB_ROOT_USERNAME);
        String password = System.getenv(Config.MONGO_INITDB_ROOT_PASSWORD);
        String databaseName = System.getenv(Config.MONGO_INITDB_DATABASE);
        String host = System.getenv(Config.MONGO_HOSTNAME);

        MongoCredential credential = MongoCredential.createCredential(rootName, databaseName, password.toCharArray());

        ServerAddress serverAddress = new ServerAddress(host, MONGODB_PORT);
        mongoClient = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        server = new HttpServerImpl(createConfigFromPort(), database);
        server.start();
        return CompletableFuture.completedFuture(null);
    }

//      - MONGO_INITDB_ROOT_USERNAME=root
//      - MONGO_INITDB_ROOT_PASSWORD=pass12345
//      - MONGO_INITDB_DATABASE=energy_drinks
//      - MONGO_CONNECT=173.18.0.3

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }

}
