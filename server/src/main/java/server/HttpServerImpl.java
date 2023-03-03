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
import static one.nio.http.Request.METHOD_GET;
import static one.nio.http.Request.METHOD_HEAD;

public class HttpServerImpl extends HttpServer {

    private static final Log LOGGER = LogFactory.getLog(HttpServerImpl.class);
    private static final String PATH_BRANDS = "/brands";
    private static final String PATH_SHOPS = "/shops";

    private static final int SIZE_QUEUE = 128;
    private static final int COUNT_CORES = 4;

    private static final Set<Integer> SUPPORTED_METHODS = Set.of(METHOD_GET, METHOD_HEAD);
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
        handlerMapper.add(PATH_BRANDS, new int[]{METHOD_HEAD}, this::handleHeadBrands);
        handlerMapper.add(PATH_SHOPS, new int[]{METHOD_GET}, this::handleGetShops);
        handlerMapper.add(PATH_SHOPS, new int[]{METHOD_HEAD}, this::handleHeadShops);
    }

    private static void handleUnavailable(HttpSession session) {
        try {
            session.sendError(Response.SERVICE_UNAVAILABLE, null);
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
                    session.sendResponse(badRequest());
                    return;
                }
                int methodName = request.getMethod();
                if (!SUPPORTED_METHODS.contains(methodName)) {
                    session.sendResponse(methodNotAllowed());
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

    private void handleHeadShops(@Nonnull Request request, HttpSession session) throws IOException {
        session.sendResponse(new Response(Response.OK, Response.EMPTY));
    }

    private void handleHeadBrands(@Nonnull Request request, HttpSession session) throws IOException {
        session.sendResponse(new Response(Response.OK, Response.EMPTY));
    }

    private void handleGetFromMongo(HttpSession session, MongoCollection<Document> collection) throws IOException {
        Document doc = collection.find()
                .sort(descending("id"))
                .limit(1)
                .first();

        if (doc == null || doc.isEmpty()) {
            session.sendResponse(new Response(Response.NOT_FOUND, Response.EMPTY));
            return;
        }

        String json = doc.getString("json");
        session.sendResponse(new Response(Response.OK, json.getBytes(StandardCharsets.UTF_8)));
    }

    private static Response badRequest() {
        return new Response(Response.BAD_REQUEST, Response.EMPTY);
    }

    private static Response methodNotAllowed() {
        return new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY);
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
