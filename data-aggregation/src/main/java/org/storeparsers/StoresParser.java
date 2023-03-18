package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import java.io.*;
import java.util.*;

public class StoresParser extends TimerTask {

    public static final Log LOGGER = LogFactory.getLog(StoresParser.class);
    private final MongoClient mongoClient;
    private final String databaseName;

    private JsonObject brandsJson;
    private JsonObject shopsJson;

    public StoresParser(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    @Override
    public void run() {
        Locale.setDefault(new Locale("en", "RU"));
        LOGGER.info("Locale: " + Locale.getDefault());

        shopsJson = new JsonObject();
        JsonArray shopsArray = new JsonArray();
        brandsJson = new JsonObject();
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
            LOGGER.error("IOException when initialize parsers", e);
            throw new RuntimeException();
        }

        try {
            parseShop("Parsing Vkuster", shopsArray, parserVkuster, brands);
            parseShop("Parsing Okey", shopsArray, parserOkey, brands);
            parseShop("Parsing Auchan", shopsArray, parserAuchan, brands);
            parseShop("Parsing Lenta", shopsArray, parserLenta, brands);
            parseShop("Parsing Perekresok", shopsArray, parserPerekrestok, brands);
        } catch (IOException e) {
            LOGGER.error("IOException In StoresParser", e);
        }


        LOGGER.info("Building Brands Array");
        for (String brand : brands) {
            brandsArray.add(brand);
        }

        shopsJson.add("shops", shopsArray);
        brandsJson.add("brands", brandsArray);
        LOGGER.info("Sending to mongodb");
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        long time =  System.currentTimeMillis();
        MongoCollection<Document> brandsDocuments = database.getCollection("brands");
        addDocToMongo(brandsJson, time, brandsDocuments);
        MongoCollection<Document> shopsDocuments = database.getCollection("shops");
        addDocToMongo(shopsJson, time, shopsDocuments);
    }

    public JsonObject getBrands() {
        return brandsJson;
    }

    public JsonObject getShops() {
        return shopsJson;
    }

    private static void addDocToMongo(JsonObject json, long time, MongoCollection<Document> collection) {
        Document document = new Document("id", time);
        document.append("json", json.toString());
        collection.insertOne(document);
    }

    private static void parseShop(String nameShop, JsonArray shopsArray, Parser parser, Set<String> brandsShop) throws IOException {
        LOGGER.info(nameShop);
        shopsArray.add(parser.parseStore());
        brandsShop.addAll(parser.brands);
    }

}