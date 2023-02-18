package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserVkuster extends Parser{
    @Override
    public JsonObject parseStore() throws IOException {
        String url = "https://vkuster.ru/catalog/bezalkogolnye-napitki/energeticheskie-napitki/";
        String logoLink = "https://yoplace.ru/media/chain/vkuster.jpg";

        JsonObject shopVkuster = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        Set<String> energyDrinks = getDrinksUrl(url);

        for (String energyDrink : energyDrinks) {
            eDrinks.add(parseEnergyDrinkPage(energyDrink));
        }

        shopVkuster.addProperty("name", "ВКУСТЕР");
        shopVkuster.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopVkuster.add("energyDrinks", eDrinks);
        return shopVkuster;
    }

    @Override
    public JsonObject parseEnergyDrinkPage(String html) throws IOException {
        String baseUrl = "https://vkuster.ru";
        HashMap<String, String> drinksNames = new HashMap<>();
        drinksNames.put("Gorilla", "Горилла");
        drinksNames.put("Adrenaline", "Адреналин");
        boolean isDiscounted = true;

        Element energyDrink = Jsoup.parse(html);

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

        return makeEnergyDrinkJsonObject(fullName,
                brand,
                imgLink,
                (int) (Double.parseDouble(volume) * 1000),
                oldPricef,
                newPricef,
                discount);
    }

    @Override
    public Set<String> getDrinksUrl(String html) throws IOException {;
        org.jsoup.nodes.Document mainPage = Jsoup.connect(html).get();
        Set<String> energyDrinksRes = new HashSet<>();
        Elements energyDrinks = mainPage.select("div[class=\"col-6 col-md-4\"]");
        for (Element energyDrink : energyDrinks) {
            energyDrinksRes.add(energyDrink.html().replace("\n", ""));
        }
        return energyDrinksRes;
    }
}
