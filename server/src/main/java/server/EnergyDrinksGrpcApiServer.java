package server;

import api.EnergyDrinksGrpcApi;
import api.ServiceEnergyDrinksGrpc;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Sorts.descending;

public class EnergyDrinksGrpcApiServer {

    private static final Log LOGGER = LogFactory.getLog(EnergyDrinksGrpcApiServer.class);

    private final int port;
    private final Server server;

    public EnergyDrinksGrpcApiServer(int port, MongoClient mongoClient) {
        this(ServerBuilder.forPort(port), port, mongoClient);
    }

    public EnergyDrinksGrpcApiServer(ServerBuilder<?> serverBuilder,
                                     int port,
                                     MongoClient mongoClient) {
        this.port = port;
        this.server = serverBuilder.addService(new EnergyDrinksGrpcApiImpl(mongoClient)).build();
    }

    public void start() throws IOException {
        server.start();
        LOGGER.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.error("*** shutting down gRPC server since JVM is shutting down");
            EnergyDrinksGrpcApiServer.this.stop();
            LOGGER.error("*** server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class EnergyDrinksGrpcApiImpl extends ServiceEnergyDrinksGrpc.ServiceEnergyDrinksImplBase {

        private final AtomicReference<EnergyDrinksGrpcApi.ShopsResponse> cashResponseShops = new AtomicReference<>();
        private final AtomicReference<EnergyDrinksGrpcApi.BrandsResponse> cashResponseBrands = new AtomicReference<>();
        private final AtomicLong lastUpdateShops = new AtomicLong();
        private final AtomicLong lastUpdateBrands = new AtomicLong();
        private static final String DATABASE_NAME = System.getenv(Config.MONGO_INITDB_DATABASE);
        private final MongoClient mongoClient;

        EnergyDrinksGrpcApiImpl(MongoClient mongoClient) {
            this.mongoClient = mongoClient;
        }

        @Override
        public void getShops(EnergyDrinksGrpcApi.ShopsRequest request,
                             StreamObserver<EnergyDrinksGrpcApi.ShopsResponse> responseObserver) {
            try {
                if (cashIsDead(lastUpdateShops)) {
                    String json = getJson(CollectionsMongoDb.SHOPS);
                    if (json == null) {
                        responseObserver.onError(new RuntimeException("Empty database!"));
                        responseObserver.onCompleted();
                    }
                    EnergyDrinksGrpcApi.ShopsResponse.Builder response =
                            EnergyDrinksGrpcApi.ShopsResponse.newBuilder();
                    try {
                        JsonFormat.parser().ignoringUnknownFields().merge(json, response);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                    cashResponseShops.set(response.build());
                    lastUpdateShops.set(System.currentTimeMillis());
                }
                responseObserver.onNext(cashResponseShops.get());
                responseObserver.onCompleted();
            } catch (IOException e) {
                responseObserver.onError(Status.INTERNAL.withDescription("Failed to retrieve shops data")
                        .withCause(e).asRuntimeException());
            }
        }

        @Override
        public void getBrands(EnergyDrinksGrpcApi.BrandsRequest request,
                              StreamObserver<EnergyDrinksGrpcApi.BrandsResponse> responseObserver) {
            try {
                if (cashIsDead(lastUpdateBrands)) {
                    String json = getJson(CollectionsMongoDb.BRANDS);
                    EnergyDrinksGrpcApi.BrandsResponse.Builder response =
                            EnergyDrinksGrpcApi.BrandsResponse.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(json, response);

                    cashResponseBrands.set(response.build());
                    lastUpdateBrands.set(System.currentTimeMillis());
                }
                responseObserver.onNext(cashResponseBrands.get());
                responseObserver.onCompleted();
            } catch (IOException e) {
                responseObserver.onError(Status.INTERNAL.withDescription("Failed to retrieve brands data")
                        .withCause(e).asRuntimeException());
            }

        }

        private static boolean cashIsDead(AtomicLong time) {
            return System.currentTimeMillis() - time.get() > TimeUnit.HOURS.toMillis(2);
        }

        private String getJson(CollectionsMongoDb shops) throws IOException {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            Document doc = database.getCollection(shops.name)
                    .find()
                    .sort(descending("id"))
                    .limit(1)
                    .first();

            if (doc == null || doc.isEmpty()) {
                throw new IOException("Empty database!");
            }
            return doc.getString("json");
        }

        enum CollectionsMongoDb {
            BRANDS("brands"),
            SHOPS("shops");

            final String name;

            CollectionsMongoDb(String name) {
                this.name = name;
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String rootName = System.getenv(Config.MONGO_INITDB_ROOT_USERNAME);
        String password = System.getenv(Config.MONGO_INITDB_ROOT_PASSWORD);
        String databaseName = System.getenv(Config.MONGO_INITDB_DATABASE);
        String host = System.getenv(Config.MONGO_HOSTNAME);

        MongoCredential credential = MongoCredential.createCredential(rootName, databaseName, password.toCharArray());
        ServerAddress serverAddress = new ServerAddress(host, 27017);
        try (MongoClient mongoClient =
                     new MongoClient(serverAddress, credential, MongoClientOptions.builder().build())) {
            EnergyDrinksGrpcApiServer server = new EnergyDrinksGrpcApiServer(8080, mongoClient);
            server.start();
            server.blockUntilShutdown();
        }
    }

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}