package server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.http.HttpSession;
import one.nio.http.PathMapper;
import one.nio.http.Request;
import one.nio.http.RequestHandler;
import one.nio.http.Response;
import one.nio.net.Session;
import one.nio.server.SelectorThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Sorts.descending;
import static one.nio.http.Request.METHOD_DELETE;
import static one.nio.http.Request.METHOD_GET;
import static one.nio.http.Request.METHOD_PUT;

public class HttpServerImpl extends HttpServer {

    private static final Log LOGGER = LogFactory.getLog(HttpServerImpl.class);
    private static final String PATH_BRANDS = "/brands";
    private static final String PATH_SHOPS = "/shops";

    private static final int SIZE_QUEUE = 128;
    private static final int COUNT_CORES = 4;

    private static final Set<Integer> SUPPORTED_METHODS = Set.of(METHOD_GET, METHOD_PUT, METHOD_DELETE);
    private static final Response BAD_RESPONSE = new Response(Response.BAD_REQUEST, Response.EMPTY);
    private static final Response METHOD_NOT_ALLOWED = new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY);
    private static final Response SERVICE_UNAVAILABLE = new Response(Response.SERVICE_UNAVAILABLE, Response.EMPTY);
    private final MongoDatabase database;
    private final PathMapper handlerMapper = new PathMapper();

    private final ExecutorService poolExecutor = new ThreadPoolExecutor(
            COUNT_CORES,
            COUNT_CORES,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(SIZE_QUEUE)
    );

    public HttpServerImpl(HttpServerConfig httpServerConfig, MongoDatabase database,
                          Object... routers) throws IOException {
        super(httpServerConfig, routers);
        this.database = database;
        handlerMapper.add(PATH_BRANDS, new int[]{METHOD_GET}, this::handleGetBrands);
        handlerMapper.add(PATH_SHOPS, new int[]{METHOD_GET}, this::handleGetShops);
    }

    @Override
    public void handleDefault(Request request, HttpSession session) throws IOException {
        session.sendResponse(BAD_RESPONSE);
    }

    private static void handleUnavailable(HttpSession session) {
        try {
            session.sendResponse(SERVICE_UNAVAILABLE);
        } catch (IOException ioException) {
            try {
                LOGGER.error("Error when send SERVICE_UNAVAILABLE response", ioException);
                session.close();
            } catch (Exception exception) {
                LOGGER.error("Error when close connection", exception);
            }
        }
    }

    @Override
    public void handleRequest(Request request, HttpSession session) {
        try {
            runHandleRequest(request, session);
        } catch (RejectedExecutionException rejectedExecutionException) {
            LOGGER.error("Reject request", rejectedExecutionException);
            handleUnavailable(session);
        }
    }

    private void runHandleRequest(Request request, HttpSession session) {
        poolExecutor.execute(() -> {
            try {
                String path = request.getPath();
                if (!path.equals(PATH_BRANDS) && !path.equals(PATH_SHOPS)) {
                    session.sendResponse(BAD_RESPONSE);
                    return;
                }
                int methodName = request.getMethod();
                if (!SUPPORTED_METHODS.contains(methodName)) {
                    session.sendResponse(METHOD_NOT_ALLOWED);
                    return;
                }

                RequestHandler handler = handlerMapper.find(path, methodName);
                if (handler != null) {
                    handler.handleRequest(request, session);
                    return;
                }
                handleDefault(request, session);

            } catch (IOException e) {
                handleUnavailable(session);
            }
        });
    }

    @Override
    public synchronized void stop() {
        for (SelectorThread thread : selectors) {
            for (Session session : thread.selector) {
                session.close();
            }
        }
        super.stop();
        poolExecutor.shutdown();
    }

    private void handleGetShops(@Nonnull Request request, HttpSession session) throws IOException {
        MongoCollection<Document> brands = database.getCollection(CollectionsMongoDb.SHOPS.name);
        handleGetFromMongo(session, brands);
    }

    private void handleGetBrands(@Nonnull Request request, HttpSession session) throws IOException {
        MongoCollection<Document> brands = database.getCollection(CollectionsMongoDb.BRANDS.name);
        handleGetFromMongo(session, brands);
    }

    private void handleGetFromMongo(HttpSession session, MongoCollection<Document> brands) throws IOException {
        Document doc = brands.find()
                .sort(descending("id"))
                .limit(1)
                .first();

        if (doc == null || doc.isEmpty()) {
            session.sendResponse(new Response(Response.NOT_FOUND, Response.EMPTY));
            return;
        }

        String brandJson = doc.getString("json");
        session.sendResponse(new Response(Response.OK, brandJson.getBytes(StandardCharsets.UTF_8)));
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
