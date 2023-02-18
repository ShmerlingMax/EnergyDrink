package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.io.*;
import java.util.*;

public class StoresParser extends TimerTask {

    public static final Log LOGGER = LogFactory.getLog(StoresParser.class);

    private static final int MONGODB_PORT = 27017;

    @Override
    public void run() {
        Locale.setDefault(new Locale("en", "RU"));
        LOGGER.info("Locale: " + Locale.getDefault());

        JsonObject shopsJson = new JsonObject();
        JsonArray shopsArray = new JsonArray();
        JsonObject brandsJson = new JsonObject();
        JsonArray brandsArray = new JsonArray();

        Set<String> brands = new HashSet<>();

        ParserAuchan parserAuchan = new ParserAuchan();
        ParserLenta parserLenta = new ParserLenta();
        ParserPerekrestok parserPerekrestok = new ParserPerekrestok();
        ParserVkuster parserVkuster = new ParserVkuster();
        ParserOkey parserOkey = new ParserOkey();

        try {
            LOGGER.info("Parsing Vkuster");
            shopsArray.add(parserVkuster.parseStore());
            brands.addAll(parserVkuster.brands);

            LOGGER.info("Parsing Okey");
            shopsArray.add(parserOkey.parseStore());
            brands.addAll(parserOkey.brands);

            LOGGER.info("Parsing Auchan");
            shopsArray.add(parserAuchan.parseStore());
            brands.addAll(parserAuchan.brands);

            LOGGER.info("Parsing Lenta");
            shopsArray.add(parserLenta.parseStore());
            brands.addAll(parserLenta.brands);

            LOGGER.info("Parsing Perekresok");
            shopsArray.add(parserPerekrestok.parseStore());
            brands.addAll(parserPerekrestok.brands);
        } catch (IOException e) {
            LOGGER.error("IOException In StoresParser", e);
            throw new RuntimeException(e);
        }


        LOGGER.info("Building Brands Array");
        for (String brand : brands) {
            brandsArray.add(brand);
        }

        shopsJson.add("shops", shopsArray);
        brandsJson.add("brands", brandsArray);

        LOGGER.info("Sending to mongodb");
        String rootName = System.getenv(Config.MONGO_INITDB_ROOT_USERNAME);
        String password = System.getenv(Config.MONGO_INITDB_ROOT_PASSWORD);
        String databaseName = System.getenv(Config.MONGO_INITDB_DATABASE);
        String host = System.getenv(Config.MONGO_HOSTNAME);

        MongoCredential credential = MongoCredential.createCredential(rootName, databaseName, password.toCharArray());

        ServerAddress serverAddress = new ServerAddress(host, MONGODB_PORT);
        MongoClient mongoClient = new MongoClient(serverAddress, credential, MongoClientOptions.builder().build());
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        long time =  System.currentTimeMillis();
        MongoCollection<Document> brandsDocuments = database.getCollection("brands");
        Document brandsDocument = new Document("id", time);
        brandsDocument.append("json", brandsJson.toString());
        brandsDocuments.insertOne(brandsDocument);

        MongoCollection<Document> shopsDocuments = database.getCollection("shops");
        Document shopsDocument = new Document("id", time);
        shopsDocument.append("json", shopsJson.toString());
        shopsDocuments.insertOne(shopsDocument);
        mongoClient.close();
    }
    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}