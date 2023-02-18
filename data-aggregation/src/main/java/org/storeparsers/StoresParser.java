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

        ParserAuchan parserAuchan = null;
        ParserLenta parserLenta = null;
        ParserPerekrestok parserPerekrestok = null;
        ParserVkuster parserVkuster = null;
        ParserOkey parserOkey = null;
        try {
            parserAuchan = new ParserAuchan("https://www.auchan.ru/catalog/voda-soki-napitki/energeticheskie-napitki/energeticheskie-napitki/?page=1");
            parserLenta  = new ParserLenta("https://lenta.com/catalog/bezalkogolnye-napitki/energetiki--i-sportivnye-napitki/energetiki/");
            parserPerekrestok = new ParserPerekrestok("https://www.perekrestok.ru/cat/c/206/energeticeskie-napitki");
            parserVkuster = new ParserVkuster("https://vkuster.ru/catalog/bezalkogolnye-napitki/energeticheskie-napitki/");
            parserOkey = new ParserOkey("https://www.okeydostavka.ru/spb/goriachie-i-kholodnye-napitki/energeticheskie-napitki");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            parseShop("Parsing Vkuster", shopsArray, parserVkuster.parseStore(), brands, parserVkuster.brands);
            parseShop("Parsing Okey", shopsArray, parserOkey.parseStore(), brands, parserOkey.brands);
            parseShop("Parsing Auchan", shopsArray, parserAuchan.parseStore(), brands, parserAuchan.brands);
            parseShop("Parsing Lenta", shopsArray, parserLenta.parseStore(), brands, parserLenta.brands);
            parseShop("Parsing Perekresok", shopsArray, parserPerekrestok.parseStore(), brands, parserPerekrestok.brands);
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

        /*LOGGER.info("Sending to mongodb");
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
        addDocToMongo(brandsJson, time, brandsDocuments);

        MongoCollection<Document> shopsDocuments = database.getCollection("shops");
        addDocToMongo(shopsJson, time, shopsDocuments);
        mongoClient.close();*/
    }

    private static void addDocToMongo(JsonObject json, long time, MongoCollection<Document> collection) {
        Document brandsDocument = new Document("id", time);
        brandsDocument.append("json", json.toString());
        collection.insertOne(brandsDocument);
    }

    private static void parseShop(String nameShop, JsonArray shopsArray, JsonObject shop, Set<String> brands, Set<String> brandsShop) throws IOException {
        LOGGER.info(nameShop);
        shopsArray.add(shop);
        brands.addAll(brandsShop);
    }

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}