package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parsers {
    private static final List<String> deleteFromTitle = Arrays.asList("напиток", "безалкогольный", "тонизирующий",
            "пастеризованный", "сильногазированный", "энергетический", "сильногаз.", "сильногаз", "газированный",
            "сильногазированныйированный", "негазированный", "л", "мл", "(энергетический)");
    private static final String defaultImageLink = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Question-mark-grey.jpg/1200px-Question-mark-grey.jpg";

    private static final Log LOGGER = LogFactory.getLog(Parsers.class);

    private static final int MONGODB_PORT = 27017;

    public static void main(String[] args) throws IOException, InterruptedException {
        Locale.setDefault(new Locale("en", "RU"));
        LOGGER.info("Locale: " + Locale.getDefault());
        JsonObject mainJson = new JsonObject();
        JsonArray shops = new JsonArray();
        JsonObject brandsObject = new JsonObject();
        JsonArray brandsArr = new JsonArray();
        Set<String> brands = new HashSet<>();

        LOGGER.info("Parse Lenta");
        shops.add(parseLenta(brands));

        LOGGER.info("Parse Auchan");
        shops.add(parseAuchan(brands));

        LOGGER.info("Parse Vkuster");
        shops.add(parseVkuster(brands));

        LOGGER.info("Parse Perekresok");
        shops.add(parsePerekrestok(brands));

        LOGGER.info("Parse Okey");
        shops.add(parseOkey(brands));

        LOGGER.info("Build Brands");
        for (String brand : brands) {
            brandsArr.add(brand);
        }


        mainJson.add("shops", shops);
        brandsObject.add("brands", brandsArr);

        PrintWriter writer = new PrintWriter("shops.json", StandardCharsets.UTF_8);
        writer.println(mainJson);
        writer.close();

        writer = new PrintWriter("brands.json", StandardCharsets.UTF_8);
        writer.println(brandsObject);
        writer.close();

        LOGGER.info("Put to mongodb");
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
        brandsDocument.append("json", brandsObject.toString());
        brandsDocuments.insertOne(brandsDocument);

        MongoCollection<Document> shopsDocuments = database.getCollection("shops");
        Document shopsDocument = new Document("id", time);
        shopsDocument.append("json", mainJson.toString());
        shopsDocuments.insertOne(shopsDocument);
        mongoClient.close();

    }

    private static JsonObject parseLenta(Set<String> brands) throws IOException, InterruptedException {
        final String logoLink = "https://upload.wikimedia.org/wikipedia/commons/9/94/%D0%9B%D0%95%D0%9D%D0%A2%D0%90_%D0%BB%D0%BE%D0%B3%D0%BE.jpg";
        final String fullNameKeyword = "<h1 class=\"sku-page__title\" itemprop=\"name\">        ";
        final String brandKeyword = "{&quot;key&quot;:&quot;Бренд&quot;,&quot;value&quot;:&quot;";
        final String imgKeyword = "<div class=\"sku-images-slider__image-block square__inner\">        <img          src=\"";
        final String volumeKeyword = "&quot;key&quot;:&quot;Упаковка&quot;,&quot;value&quot;:&quot;";
        final String oldPriceKeyword = "&quot;regularPrice&quot;:{&quot;value&quot;:";
        final String newPriceKeyword = "&quot;cardPrice&quot;:{&quot;value&quot;:";
        final String discountKeyword = "<div class=\"discount-label-small discount-label-small--sku-page " +
                "sku-page__discount-label\">";

        String responseAll = "";
        String cookie = "qrator_jsid=1671391617.717.U1wKIpbumC3YsYmC-gm32jfvjslm37j0hmc453jijn0bo5r66";

        String[] commands = new String[]{"curl", "--cookie", cookie,
                "https://lenta.com/catalog/bezalkogolnye-napitki/energetiki--i-sportivnye-napitki/energetiki/"};
        responseAll += getHtmlCurl(commands);

        int productPosStart = 0;
        List<String> energyDrinks = new ArrayList<>();

        while (productPosStart != -1) {
            int productLinkPosEnd = responseAll.indexOf("\n", productPosStart);
            String productLink = responseAll.substring(productPosStart, productLinkPosEnd);

            energyDrinks.add(productLink);
            productPosStart = responseAll.indexOf("https", productLinkPosEnd);
        }
        JsonObject shopLenta = new JsonObject();
        JsonArray eDrinks = new JsonArray();


        for (String energyDrink : energyDrinks) {
            JsonObject eDrink = new JsonObject();

            commands = new String[]{"curl", "--cookie", cookie, energyDrink, "-k"};
            String response = getHtmlCurl(commands);
            LOGGER.info(energyDrink);
            try {
                int fullNamePosStart = response.indexOf(fullNameKeyword) + fullNameKeyword.length();
                String fullName = response.substring(fullNamePosStart, response.indexOf(",", fullNamePosStart));
                fullName = removeWordsFromTitle(fullName);

                int brandPosStart = response.indexOf(brandKeyword) + brandKeyword.length();
                String brand = response.substring(brandPosStart, response.indexOf("&quot;", brandPosStart));
                brand = checkForRecurrentBrand(brand);
                brands.add(brand);

                int imgLinkPosStart = response.indexOf(imgKeyword) + imgKeyword.length();
                String imgLink = response.substring(imgLinkPosStart, response.indexOf("\"", imgLinkPosStart));
                if (imgLink.length() < 10) {
                    imgLink = defaultImageLink;
                }

                int volumePosStart = response.indexOf(volumeKeyword) + volumeKeyword.length();
                int volume = (int) (Double.parseDouble(response.substring(volumePosStart,
                        response.indexOf(" ", volumePosStart))) * 1000);

                int oldPricePosStart = response.indexOf(oldPriceKeyword) + oldPriceKeyword.length();
                double oldPrice = Double.parseDouble(response.substring(oldPricePosStart,
                        response.indexOf(",", oldPricePosStart)));

                int newPricePosStart = response.indexOf(newPriceKeyword) + newPriceKeyword.length();
                double newPrice = Double.parseDouble(response.substring(newPricePosStart,
                        response.indexOf(",", newPricePosStart)));

                int discountPosStart = response.indexOf(discountKeyword);
                double discount;
                if (discountPosStart == -1) {
                    discount = Math.abs((oldPrice - newPrice) / oldPrice);
                    DecimalFormat df = new DecimalFormat("#.00");
                    discount = Double.parseDouble(df.format(discount));
                } else {
                    String discountStr = response.substring(discountPosStart + discountKeyword.length(),
                            response.indexOf("</div>", discountPosStart));
                    discountStr = discountStr.replaceAll("\\s+", "");
                    discountStr = discountStr.substring(1, discountStr.length() - 1);
                    discount = (double) Integer.parseInt(discountStr) / 100;
                }

                makeEnergyDrinkJsonObject(eDrinks, eDrink, fullName, brand, imgLink, volume, oldPrice, newPrice, discount);
            } catch (StringIndexOutOfBoundsException c) {
                PrintWriter writer = new PrintWriter("logLenta.txt", "UTF-8");
                writer.println(response);
                writer.close();
            }
        }
        shopLenta.addProperty("name", "ЛЕНТА");
        shopLenta.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopLenta.add("energyDrinks", eDrinks);
        return shopLenta;
    }

    private static void makeEnergyDrinkJsonObject(JsonArray eDrinks, JsonObject eDrink, String fullName, String brand, String imgLink, int volume, double oldPrice, double newPrice, double discount) throws IOException {
        eDrink.addProperty("fullName", removeWordsFromTitle(fullName));
        eDrink.addProperty("brand", brand);
        eDrink.addProperty("image", getCompressesImageBase64(new URL(imgLink)));
        eDrink.addProperty("volume", volume);
        eDrink.addProperty("priceWithDiscount", newPrice);
        eDrink.addProperty("priceWithOutDiscount", oldPrice);
        eDrink.addProperty("discount", discount);

        eDrinks.add(eDrink);
    }

    private static JsonObject parseAuchan(Set<String> brands) throws IOException {
        final String logoLink = "https://www.logobank.ru/images/ph/ru/a/logo-auchan.png";
        final String fullNameKeyword = "<h1 id=\"productName\" class=\"css-pa63tw\">";
        final String brandKeyword = "<th class=\"css-12mfum8\">Бренд</th><td class=\"css-2619sg\">";
        final String imgClassKeyword = "class=\"picture css-11c870t\">";
        final String imgLinkKeyword = "<img src=\"";
        final String volumeKeyword = "<th class=\"css-12mfum8\">Объем, л</th><td class=\"css-2619sg\">";
        final String fullPriceKeyword = "class=\"fullPricePDP css-1129a1l\">";
        final String oldPriceKeyword = "class=\"oldPricePDP css-1a8h9g1\">";
        final String discountKeyword = "class=\"discountValue active css-45w6b9\">-<!-- -->";

        String cookie = "qrator_jsid=1671392014.424.kyS3IPzWeDk5rNrx-rmq7beradcm6hegoje4v1340lqkl26co";
        String[] commands = new String[]{"curl", "--cookie", cookie,
                "https://www.auchan.ru/catalog/voda-soki-napitki/energeticheskie-napitki/energeticheskie-napitki/?page=1"};
        String responseAll = getHtmlCurl(commands);
        commands = new String[]{"curl", "--cookie", cookie,
                "https://www.auchan.ru/catalog/voda-soki-napitki/energeticheskie-napitki/energeticheskie-napitki/?page=2"};
        responseAll += getHtmlCurl(commands);


        int productPosStart = 0;
        Set<String> energyDrinks = new HashSet<>();

        while (productPosStart != -1) {
            int productLinkPosEnd = responseAll.indexOf("\n", productPosStart);
            String productLink = responseAll.substring(productPosStart, productLinkPosEnd);

            energyDrinks.add(productLink);
            productPosStart = responseAll.indexOf("https", productLinkPosEnd);
        }

        JsonObject shopAuchan = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        for (String energyDrink : energyDrinks) {
            JsonObject eDrink = new JsonObject();

            commands = new String[]{"curl", "--cookie", cookie, energyDrink};
            String drinkPage = getHtmlCurl(commands);
            LOGGER.info(energyDrink);

            try{
                int fullNamePosStart = drinkPage.indexOf(fullNameKeyword) + fullNameKeyword.length();
                String fullName = drinkPage.substring(fullNamePosStart, drinkPage.indexOf(",", fullNamePosStart));
                if (fullName.contains("</h1>")) {
                    fullName = fullName.substring(0, fullName.indexOf("</h1>") - 1);
                }

                int brandPosStart = drinkPage.indexOf(brandKeyword) + brandKeyword.length();
                String brand = drinkPage.substring(brandPosStart, drinkPage.indexOf("</td>", brandPosStart));
                brand = checkForRecurrentBrand(brand);
                brands.add(brand);

                int imgLinkPosStart = drinkPage.indexOf(imgLinkKeyword, drinkPage.indexOf(imgClassKeyword))
                        + imgLinkKeyword.length();
                String imgLink = drinkPage.substring(imgLinkPosStart, drinkPage.indexOf("\"", imgLinkPosStart));
                imgLink = imgLink.substring(imgLink.indexOf("https"));

                int volumePosStart = drinkPage.indexOf(volumeKeyword) + volumeKeyword.length();
                String volume = drinkPage.substring(volumePosStart, drinkPage.indexOf("</td>", volumePosStart));
                int volumeInt = (int) (Double.parseDouble(volume) * 1000);

                int fullPricePosStart = drinkPage.indexOf(fullPriceKeyword) + fullPriceKeyword.length();
                String fullPrice = drinkPage.substring(fullPricePosStart, drinkPage.indexOf(" ", fullPricePosStart));
                double fullPricef = Double.parseDouble(fullPrice);


                int oldPricePosStart = drinkPage.indexOf(oldPriceKeyword);
                double oldPricef;
                double newPricef;
                if (oldPricePosStart != -1) {
                    oldPricePosStart += oldPriceKeyword.length();
                    String oldPrice = drinkPage.substring(oldPricePosStart, drinkPage.indexOf(" ", oldPricePosStart));
                    oldPricef = Double.parseDouble(oldPrice);
                } else {
                    oldPricef = fullPricef;
                }
                newPricef = fullPricef;
                double discountf = 0.0;
                if (oldPricePosStart != -1) {
                    int discountPosStart = drinkPage.indexOf(discountKeyword) + discountKeyword.length();
                    String discount = drinkPage.substring(discountPosStart, drinkPage.indexOf("<!-- -->", discountPosStart));
                    discountf = Integer.parseInt(discount);
                    discountf /= 100;
                }

                makeEnergyDrinkJsonObject(eDrinks, eDrink, fullName, brand, imgLink, volumeInt, oldPricef, newPricef, discountf);
            }catch (StringIndexOutOfBoundsException c) {
                PrintWriter writer = new PrintWriter("logAuchan.txt", "UTF-8");
                writer.println(drinkPage);
                writer.close();
            }

        }
        shopAuchan.addProperty("name", "АШАН");
        shopAuchan.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopAuchan.add("energyDrinks", eDrinks);

        return shopAuchan;
    }

    private static JsonObject parseVkuster(Set<String> brands) throws IOException {
        String baseUrl = "https://vkuster.ru";
        String url = "https://vkuster.ru/catalog/bezalkogolnye-napitki/energeticheskie-napitki/";
        String logoLink = "https://yoplace.ru/media/chain/vkuster.jpg";
        HashMap<String, String> drinksNames = new HashMap<>();
        drinksNames.put("Gorilla", "Горилла");
        drinksNames.put("Adrenaline", "Адреналин");
        boolean isDiscounted = true;

        JsonObject shopVkuster = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        org.jsoup.nodes.Document mainPage = Jsoup.connect(url).get();
        Elements energyDrinks = mainPage.select("div[class=\"col-6 col-md-4\"]");
        for (Element energyDrink : energyDrinks) {
            JsonObject eDrink = new JsonObject();

            Elements oldPriceClass = energyDrink.select("div[class=\"product__hint grey\"]");
            if (oldPriceClass.isEmpty()) {
                isDiscounted = false;
                oldPriceClass = energyDrink.select("span[class=\"product__price\"]");

            }

            String oldPrice = oldPriceClass.text();
            oldPrice = oldPrice.substring(0, oldPrice.length() - 1);
            String newPrice;
            if (isDiscounted) {
                newPrice = energyDrink.select("span[class=\"product__price promo\"]").text();
                newPrice = newPrice.replaceAll("\\s+", "");
                newPrice = newPrice.substring(0, newPrice.length() - 1);
            } else {
                newPrice = oldPrice;
            }


            String fullName = energyDrink.select("div[class=\"product__description\"]").text();
            String volume = "";
            Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
            Matcher m = p.matcher(fullName);
            while (m.find()) {
                volume = m.group(1);
            }

            String brand = "";
            for (String key : drinksNames.keySet()) {
                if (fullName.contains(drinksNames.get(key))) {
                    brand = key;
                    brand = checkForRecurrentBrand(brand);
                    brands.add(key);

                    fullName = fullName.substring(0, fullName.indexOf(volume));
                }
            }


            String imgLink = energyDrink.select("div[class=\"product__cover\"]").attr("style");
            imgLink = baseUrl + imgLink.substring(imgLink.indexOf("url") + 5, imgLink.length() - 2);

            double oldPricef = Double.parseDouble(oldPrice.substring(0, oldPrice.length() - 1));
            double newPricef = Double.parseDouble(newPrice.substring(0, newPrice.length() - 1));
            double discount = Math.abs((oldPricef - newPricef) / oldPricef);
            DecimalFormat df = new DecimalFormat("#.00");
            discount = Double.parseDouble(df.format(discount));

            eDrink.addProperty("fullName", removeWordsFromTitle(fullName));
            eDrink.addProperty("brand", brand);
            eDrink.addProperty("image", getCompressesImageBase64(new URL(imgLink)));
            eDrink.addProperty("volume", (int) (Double.parseDouble(volume) * 1000));
            eDrink.addProperty("priceWithDiscount", newPricef);
            eDrink.addProperty("priceWithOutDiscount", oldPricef);
            eDrink.addProperty("discount", discount);

            eDrinks.add(eDrink);
            isDiscounted = true;
        }
        shopVkuster.addProperty("name", "ВКУСТЕР");
        shopVkuster.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopVkuster.add("energyDrinks", eDrinks);
        return shopVkuster;
    }

    private static JsonObject parseOkey(Set<String> brands) throws IOException {
        final String baseUrlOkey = "https://www.okeydostavka.ru";
        final String baseUrlOkeyIp = "https://178.218.214.178";
        final String logoLink = "https://play-lh.googleusercontent.com/XRU3HtXnV3DitNFXQzO2aE-pGSYSvazaUt8SGNvGFzHOKTwKnIQwhAIrs3OP7Dhf7zWr";
        final String productKeyword = "class=\"product-name\"";
        final String productLinkKeyword = "href=\"";
        final String priceWithoutDiscountKeyword = "class=\"label small crossed\"> ";
        final String priceWithDiscountKeyword = "class=\"price label  label-red \"> ";
        final String offerPriceKeyword = "class=\"price label \"> ";
        final String discountKeyword = "span class=\"special-discount__text\">";
        final String volumeKeyword = "id=\"descAttributeValue_2_6_3074457345618260154_3074457345618267449\"> ";
        final String brandKeyword = "id=\"descAttributeValue_1_6_3074457345618260154_3074457345618267449\"> ";
        final String imgKeyword = "\"productMainImage\" src=\"";
        final String titleKeyword = "\"main_header\" itemprop=\"name\">";
        boolean isDiscounted = true;

        String[] commands = new String[]{"curl", "-H", "Host: www.okeydostavka.ru", "https://178.218.214.178/spb/goriachie-i-kholodnye-napitki/energeticheskie-napitki#facet:&productBeginIndex:0&orderBy:2&pageView:grid&minPrice:1&maxPrice:300&pageSize:1000&", "-k"};
        String responseAll = getHtmlCurl(commands);


        List<String> energyDrinks = new ArrayList<>();
        int productPosStart = responseAll.indexOf(productKeyword);
        while (productPosStart != -1) {
            int productLinkPosStart = responseAll.indexOf(productLinkKeyword, productPosStart);
            int productLinkPosEnd = responseAll.indexOf("\"",
                    productLinkPosStart + productLinkKeyword.length());
            String productLink = baseUrlOkeyIp +
                    responseAll.substring(productLinkPosStart + productLinkKeyword.length(), productLinkPosEnd);

            productLink = productLink.replaceAll(baseUrlOkey, "");

            energyDrinks.add(productLink);
            productPosStart = responseAll.indexOf(productKeyword, productLinkPosEnd);
        }

        JsonObject shopOkey = new JsonObject();
        JsonArray eDrinks = new JsonArray();


        for (String energyDrink : energyDrinks) {

            JsonObject eDrink = new JsonObject();
            commands = new String[]{"curl", "-H", "Host: www.okeydostavka.ru", energyDrink, "-k"};
            String response = getHtmlCurl(commands);
            LOGGER.info(energyDrink);
            try {
                int priceWithoutDiscountPosStart = response.indexOf(priceWithoutDiscountKeyword);
                String priceWithoutDiscount;
                if (priceWithoutDiscountPosStart != -1) {
                    priceWithoutDiscount = response.substring(priceWithoutDiscountPosStart + priceWithoutDiscountKeyword.length(),
                            response.indexOf("</span>", priceWithoutDiscountPosStart) - 2);
                } else {
                    int offerPricePosStart = response.indexOf(offerPriceKeyword) + offerPriceKeyword.length();
                    priceWithoutDiscount = response.substring(offerPricePosStart,
                            response.indexOf("</span>", offerPricePosStart) - 2);
                    isDiscounted = false;
                }
                priceWithoutDiscount = priceWithoutDiscount.replaceAll("\\s+", "");
                priceWithoutDiscount = priceWithoutDiscount.replace(',', '.');
                priceWithoutDiscount = priceWithoutDiscount.substring(0, priceWithoutDiscount.length() - 1);

                int priceWithDiscountPosStart = response.indexOf(priceWithDiscountKeyword);
                String priceWithDiscount;
                if (isDiscounted) {
                    priceWithDiscount = response.substring(priceWithDiscountPosStart + priceWithDiscountKeyword.length(),
                            response.indexOf("</span>", priceWithDiscountPosStart) - 2);
                    priceWithDiscount = priceWithDiscount.replaceAll("\\s+", "");
                    priceWithDiscount = priceWithDiscount.replace(',', '.');
                    priceWithDiscount = priceWithDiscount.substring(0, priceWithDiscount.length() - 1);
                } else {
                    priceWithDiscount = priceWithoutDiscount;
                }

                String discount;
                int discountPosStart = response.indexOf(discountKeyword);
                if (isDiscounted) {
                    discount = response.substring(discountPosStart + discountKeyword.length(),
                            response.indexOf("</span>", discountPosStart));
                    discount = discount.substring(1, discount.length() - 1);
                } else {
                    discount = "0";
                }

                int volumePosStart = response.indexOf(volumeKeyword);
                String volume = response.substring(volumePosStart + volumeKeyword.length(),
                        response.indexOf("</div>", volumePosStart));
                volume = volume.replaceAll("\\s+", "");
                volume = volume.replace(",", ".");

                int brandPosStart = response.indexOf(brandKeyword);
                String brand = response.substring(brandPosStart + brandKeyword.length(),
                        response.indexOf("</div>", brandPosStart));
                brand = brand.replaceAll("\\s+", "");
                if (brand.equals("О&#039;кей")) {
                    brand = "О'кей";
                }
                brand = checkForRecurrentBrand(brand);
                brands.add(brand);

                int imgPosStart = response.indexOf(imgKeyword);
                String imgLink = baseUrlOkey + response.substring(imgPosStart + imgKeyword.length(),
                        response.indexOf("\"", imgPosStart + imgKeyword.length()));

                int titlePosStart = response.indexOf(titleKeyword);
                String title = response.substring(titlePosStart + titleKeyword.length(),
                        response.indexOf(",", titlePosStart));
                if (Character.isDigit(title.charAt(title.length() - 1))) {
                    title = title.substring(0, title.length() - 2);
                }
                if (title.contains("</h1>")) {
                    title = title.substring(0, title.indexOf("</h1>"));
                }

                if (volume.length() > 5 || (volume.length() > 1 && !volume.contains("."))) {
                    volume = response.substring(response.indexOf(",", titlePosStart) + 2,
                            response.indexOf("</h1>"));
                    int i = volume.indexOf("мл");
                    if (i == -1) {
                        i = volume.indexOf("л");
                    }

                    volume = volume.substring(0, i);
                    volume = volume.replaceAll(",", ".");
                }

                eDrink.addProperty("fullName", removeWordsFromTitle(title));
                eDrink.addProperty("brand", brand);
                eDrink.addProperty("image", getCompressesImageBase64(new URL(imgLink)));
                eDrink.addProperty("volume", (int) (Double.parseDouble(volume) * 1000));
                eDrink.addProperty("priceWithDiscount", Double.parseDouble(priceWithDiscount));
                eDrink.addProperty("priceWithOutDiscount", Double.parseDouble(priceWithoutDiscount));
                eDrink.addProperty("discount", Math.abs(Double.parseDouble(discount)) / 100);
                eDrinks.add(eDrink);
                isDiscounted = true;

            } catch (StringIndexOutOfBoundsException c) {
                LOGGER.warn("Okey", c.fillInStackTrace());
                PrintWriter writer = new PrintWriter("logOkey.txt", "UTF-8");
                writer.println(response);
                writer.close();
            }
        }

        shopOkey.addProperty("name", "ОКЕЙ");
        shopOkey.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopOkey.add("energyDrinks", eDrinks);

        return shopOkey;
    }

    private static JsonObject parsePerekrestok(Set<String> brands) throws IOException {
        String baseUrl = "https://www.perekrestok.ru";
        String url = "https://www.perekrestok.ru/cat/c/206/energeticeskie-napitki";
        String logoLink = "https://gidpopromo.ru/storage/2021/07/12/d68db4a606380cf445af4df5997420d5b61e3a52.jpg";

        final String productKeyword = "class=\"sc-fFubgz fsUTLG product-card__link\"";
        final String productLinkKeyword = "href=\"";
        final String fullNameKeyword = "<h1 class=\"sc-fubCfw cqjzZF product__title\" itemProp=\"name\">";
        final String brandKeyword = "<h3 class=\"product-brand__title\">";
        final String imgKeyword = "name=\"twitter:url\"/><meta data-react-helmet=\"true\" content=\"";
        final String priceKeyword = "<div class=\"sc-jcVebW jiRWKg\">";
        final String newPriceKeyword = "<div class=\"price-new\">";
        final String oldPriceKeyword = "<div class=\"price-old\">";
        final String discountKeyword = "<div class=\"sc-bZSQDF hmfpeM sc-hTZrWc gIoqJs\">";
        boolean isDiscounted = true;

        //org.jsoup.nodes.Document mainPage = Jsoup.connect(url).get();
        String[] commands = new String[]{"curl", url};
        String responseAll = getHtmlCurl(commands);

        Set<String> energyDrinks = new HashSet<>();

        int productPosStart = responseAll.indexOf(productKeyword);
        while (productPosStart != -1) {
            int productLinkPosStart = responseAll.indexOf(productLinkKeyword, productPosStart);
            int productLinkPosEnd = responseAll.indexOf("\"",
                    productLinkPosStart + productLinkKeyword.length());
            String productLink = baseUrl +
                    responseAll.substring(productLinkPosStart + productLinkKeyword.length(), productLinkPosEnd);
            energyDrinks.add(productLink);
            productPosStart = responseAll.indexOf(productKeyword, productLinkPosEnd);
        }

        JsonObject shopPerekrestok = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        for (String energyDrink : energyDrinks) {
            JsonObject eDrink = new JsonObject();
            commands = new String[]{"curl", energyDrink};
            String response = getHtmlCurl(commands);
            LOGGER.info(energyDrink);
            if (response.contains("Этот товар закончился")) {
                continue;
            }

            try {
                int fullNamePosStart = response.indexOf(fullNameKeyword);
                String fullName = response.substring(fullNamePosStart + fullNameKeyword.length(),
                        response.indexOf(",", fullNamePosStart));
                String volume = response.substring(response.indexOf(",", fullNamePosStart) + 1,
                        response.indexOf("</h1>", response.indexOf(",", fullNamePosStart) + 1));

                int i = volume.length() - 1;
                char tmp = volume.charAt(i);
                while (!Character.isDigit(tmp)) {
                    i--;
                    tmp = volume.charAt(i);
                }
                if (volume.contains("мл")) {
                    volume = volume.substring(0, volume.indexOf("мл"));
                }
                if (volume.contains("л")) {
                    volume = volume.substring(0, volume.indexOf("л"));
                }

                double volumeTmp = Double.parseDouble(volume);
                if (volumeTmp < 1) {
                    volumeTmp *= 1000;
                }
                int volumeRes = (int) volumeTmp;

                int brandPosStart = response.indexOf(brandKeyword);
                String brand = response.substring(brandPosStart + brandKeyword.length(),
                        response.indexOf("</h3>", brandPosStart));
                brand = checkForRecurrentBrand(brand);
                brands.add(brand);


                int imgLinkPosStart = response.indexOf(imgKeyword);
                String imgLink = response.substring(imgLinkPosStart + imgKeyword.length(),
                        response.indexOf("\"", imgLinkPosStart + imgKeyword.length()));

                String span = "<span>";
                String discount;
                int discountPosStart = response.indexOf(discountKeyword);
                if (discountPosStart != -1) {
                    int discountPosStart1 = response.indexOf(span, discountPosStart);
                    discount = response.substring(discountPosStart1 + span.length(),
                            response.indexOf("</span>", discountPosStart1));
                    discount = discount.substring(0, discount.length() - 1);
                } else {
                    discount = "0";
                    isDiscounted = false;
                }


                int pricePosStart = response.indexOf(priceKeyword);

                String newPrice = response.substring(response.indexOf(newPriceKeyword, pricePosStart) + newPriceKeyword.length(),
                        response.indexOf("</div>", response.indexOf(newPriceKeyword, pricePosStart)) - 2);
                newPrice = newPrice.replace(',', '.');

                String oldPrice;
                if (isDiscounted) {
                    oldPrice = response.substring(response.indexOf(oldPriceKeyword, pricePosStart) + oldPriceKeyword.length(),
                            response.indexOf("</div>", response.indexOf(oldPriceKeyword, pricePosStart)) - 2);
                    oldPrice = oldPrice.replace(',', '.');
                } else {
                    oldPrice = newPrice;
                }


                eDrink.addProperty("fullName", removeWordsFromTitle(fullName));
                eDrink.addProperty("brand", brand);
                eDrink.addProperty("image", getCompressesImageBase64(new URL(imgLink)));
                eDrink.addProperty("volume", volumeRes);
                eDrink.addProperty("priceWithDiscount", Double.parseDouble(newPrice));
                eDrink.addProperty("priceWithOutDiscount", Double.parseDouble(oldPrice));
                eDrink.addProperty("discount", Math.abs((double) Math.abs(Integer.parseInt(discount)) / 100));

                eDrinks.add(eDrink);
                isDiscounted = true;
            } catch (StringIndexOutOfBoundsException c) {
                PrintWriter writer = new PrintWriter("logPerekrestok.txt", "UTF-8");
                writer.println(response);
                writer.close();
            }
        }
        shopPerekrestok.addProperty("name", "ПЕРЕКРЕСТОК");
        shopPerekrestok.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopPerekrestok.add("energyDrinks", eDrinks);
        return shopPerekrestok;
    }

    private static String getCompressesImageBase64(URL url) throws IOException {
        InputStream in = new BufferedInputStream(url.openStream());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();

        String imgName = "tmpImg.jpg";
        String imgCompressedName = "thumbnail.jpg";
        FileOutputStream fos = new FileOutputStream(imgName);
        fos.write(response);
        fos.close();

        Thumbnails.of(new File(imgName))
                .size(300, 300)
                .toFile(imgCompressedName);
        File compressedImg = new File(imgCompressedName);
        byte[] imgContent = Files.readAllBytes(compressedImg.toPath());
        compressedImg.delete();
        new File(imgName).delete();

        return Base64.getEncoder().encodeToString(imgContent);
    }

    private static String removeWordsFromTitle(String title) {
        List<String> words = Stream.of(title.split(" +"))
                .map(String::trim)
                .collect(Collectors.toList());
        List<String> wordsLowerCase = stringsToLowerCase(words);
        String word;
        for (int i = 0; i < wordsLowerCase.size(); i++) {
            word = wordsLowerCase.get(i);
            if (Parsers.deleteFromTitle.contains(word) || isNumeric(word)) {
                title = title.replace(words.get(i), "");
            }
        }
        title = title.trim().replaceAll(" +", " ");
        return title;
    }

    public static List<String> stringsToLowerCase(List<String> strings) {
        List<String> result = new ArrayList<>(strings);

        ListIterator<String> iterator = result.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String getHtmlCurl(String[] commands) throws IOException {
        Process process = Runtime.getRuntime().exec(commands);
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        for (String command : commands) {
            if (command.contains("lenta") && !command.contains("main")) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(Parsers.class.getClassLoader().getResourceAsStream("lentadrinks.txt")))) {
                    response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }

                /*File file = new File("src/main/resources/lentadrinks.txt");
                FileReader reader1 = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader1);
                response = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line).append("\n");
                }*/

            } else if (command.contains("auchan") && !command.contains("main")) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(Parsers.class.getClassLoader().getResourceAsStream("auchandrinks.txt")))) {
                    response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }
            }
        }

        return response.toString();
    }

    public static String checkForRecurrentBrand(String brand) {
        if (brand.equals("Redbull") || brand.equals("RED BULL")) {
            return "Red Bull";
        } else if (brand.equals("FlashUp") || brand.equals("ФЛЭШ АП") || brand.equals("Flash Energy")) {
            return "Flash Up";
        } else if (brand.equals("Adrenalinerush") || brand.equals("ADRENALINE")) {
            return "Adrenaline";
        } else if (brand.equalsIgnoreCase("black monster") || brand.equals("MONSTER")) {
            return "Monster";
        } else if (brand.equals("EGOISTE")) {
            return "Egoiste";
        } else if (brand.equals("ГЕНЕЗИС")) {
            return "Genesis";
        } else if (brand.equals("ТANK")) {
            return "World of Tanks";
        } else if (brand.equals("BURN")) {
            return "Burn";
        } else if (brand.equals("DRIVE ME") || brand.equals("Driveme")) {
            return "Drive Me";
        } else if (brand.equals("TORNADO")) {
            return "Tornado";
        } else if (brand.equals("GORILLA")) {
            return "Gorilla";
        } else if (brand.equals("POWER TORR")) {
            return "Powertorr";
        } else if (brand.equals("БАЙКАЛ")) {
            return "Байкал";
        } else if (brand.equals("E-ON")) {
            return "E-On";
        } else if (brand.equals("ОZВЕРИН")) {
            return "OZВЕРИН";
        }
        return brand;
    }

    static class Config {
        private static final String MONGO_INITDB_ROOT_USERNAME = "MONGO_INITDB_ROOT_USERNAME";
        private static final String MONGO_INITDB_ROOT_PASSWORD = "MONGO_INITDB_ROOT_PASSWORD";
        private static final String MONGO_INITDB_DATABASE = "MONGO_INITDB_DATABASE";
        private static final String MONGO_HOSTNAME = "MONGO_HOSTNAME";
    }
}