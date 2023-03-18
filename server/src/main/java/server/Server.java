package server;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.server.AcceptorConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Server {

    private static final Log LOGGER = LogFactory.getLog(Server.class);
    private static final int MONGODB_PORT = 27017;
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            String rootName = System.getenv(Config.MONGO_INITDB_ROOT_USERNAME);
            String password = System.getenv(Config.MONGO_INITDB_ROOT_PASSWORD);
            String databaseName = System.getenv(Config.MONGO_INITDB_DATABASE);
            String host = System.getenv(Config.MONGO_HOSTNAME);
            MongoCredential credential = MongoCredential.createCredential(rootName, databaseName, password.toCharArray());
            ServerAddress serverAddress = new ServerAddress(host, MONGODB_PORT);
            MongoClient mongoClient = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            HttpServer server = new HttpServerImpl(createConfigFromPort(SERVER_PORT), database);
            server.start();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static HttpServerConfig createConfigFromPort(int port) {
        HttpServerConfig httpConfig = new HttpServerConfig();
        AcceptorConfig acceptor = new AcceptorConfig();
        acceptor.port = port;
        acceptor.reusePort = true;
        httpConfig.acceptors = new AcceptorConfig[]{acceptor};
        return httpConfig;
    }

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}
