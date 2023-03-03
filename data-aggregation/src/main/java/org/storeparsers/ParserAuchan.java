package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ParserAuchan extends Parser {
    private final String cookie;
    private final String storeUrl;

    public ParserAuchan(String storeUrl) throws IOException {
        this.cookie = "qrator_jsid=1671392014.424.kyS3IPzWeDk5rNrx-rmq7beradcm6hegoje4v1340lqkl26co";
        this.storeUrl = storeUrl;
    }

    @Override
    public JsonObject parseStore() throws IOException {
        final String logoLink = "https://www.logobank.ru/images/ph/ru/a/logo-auchan.png";

        JsonObject shopAuchan = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        String[] commands = new String[]{"curl", "--cookie", cookie, storeUrl};
        String responseAll = getHtmlCurl(commands);

        Set<String> energyDrinks = getDrinksUrl(responseAll);

        for (String energyDrink : energyDrinks) {
            commands = new String[]{"curl", "--cookie", cookie, energyDrink};
            String drinkPage = getHtmlCurl(commands);

            try {
                eDrinks.add(parseEnergyDrinkPage(drinkPage));
            } catch (RuntimeException c) {
                StoresParser.LOGGER.warn("Auchan", c.fillInStackTrace());
            }

        }
        shopAuchan.addProperty("name", "АШАН");
        shopAuchan.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopAuchan.add("energyDrinks", eDrinks);

        return shopAuchan;
    }

    @Override
    public JsonObject parseEnergyDrinkPage(String html) throws IOException {
        final String fullNameKeyword = "<h1 id=\"productName\" class=\"css-pa63tw\">";
        final String brandKeyword = "<th class=\"css-12mfum8\">Бренд</th><td class=\"css-2619sg\">";
        final String imgClassKeyword = "class=\"picture css-11c870t\">";
        final String imgLinkKeyword = "<img src=\"";
        final String volumeKeyword = "<th class=\"css-12mfum8\">Объем, л</th><td class=\"css-2619sg\">";
        final String fullPriceKeyword = "class=\"fullPricePDP css-1129a1l\">";
        final String oldPriceKeyword = "class=\"oldPricePDP css-1a8h9g1\">";
        final String discountKeyword = "class=\"discountValue active css-45w6b9\">-<!-- -->";

        int fullNamePosStart = html.indexOf(fullNameKeyword) + fullNameKeyword.length();
        String fullName = html.substring(fullNamePosStart, html.indexOf(",", fullNamePosStart));
        if (fullName.contains("</h1>")) {
            fullName = fullName.substring(0, fullName.indexOf("</h1>") - 1);
        }

        int brandPosStart = html.indexOf(brandKeyword) + brandKeyword.length();
        String brand = html.substring(brandPosStart, html.indexOf("</td>", brandPosStart));
        brand = checkForRecurrentBrand(brand);
        brands.add(brand);

        int imgLinkPosStart = html.indexOf(imgLinkKeyword, html.indexOf(imgClassKeyword))
                + imgLinkKeyword.length();
        String imgLink = html.substring(imgLinkPosStart, html.indexOf("\"", imgLinkPosStart));
        imgLink = imgLink.substring(imgLink.indexOf("https"));

        int volumePosStart = html.indexOf(volumeKeyword) + volumeKeyword.length();
        String volume = html.substring(volumePosStart, html.indexOf("</td>", volumePosStart));
        int volumeInt = (int) (Double.parseDouble(volume) * 1000);

        int fullPricePosStart = html.indexOf(fullPriceKeyword) + fullPriceKeyword.length();
        String fullPrice = html.substring(fullPricePosStart, html.indexOf(" ", fullPricePosStart));
        double fullPricef = Double.parseDouble(fullPrice);


        int oldPricePosStart = html.indexOf(oldPriceKeyword);
        double oldPricef;
        double newPricef;
        if (oldPricePosStart != -1) {
            oldPricePosStart += oldPriceKeyword.length();
            String oldPrice = html.substring(oldPricePosStart, html.indexOf(" ", oldPricePosStart));
            oldPricef = Double.parseDouble(oldPrice);
        } else {
            oldPricef = fullPricef;
        }
        newPricef = fullPricef;
        double discountf = 0.0;
        if (oldPricePosStart != -1) {
            int discountPosStart = html.indexOf(discountKeyword) + discountKeyword.length();
            String discount = html.substring(discountPosStart, html.indexOf("<!-- -->", discountPosStart));
            discountf = Integer.parseInt(discount);
            discountf /= 100;
        }

        return makeEnergyDrinkJsonObject(fullName, brand, imgLink, volumeInt, oldPricef, newPricef, discountf);
    }

    @Override
    public Set<String> getDrinksUrl(String html) throws IOException {
        int productPosStart = html.indexOf("https", 0);
        html += "\n";
        Set<String> energyDrinks = new HashSet<>();
        while (productPosStart != -1) {
            int productLinkPosEnd = html.indexOf("\n", productPosStart);
            String productLink = html.substring(productPosStart, productLinkPosEnd);
            energyDrinks.add(productLink);
            productPosStart = html.indexOf("https", productLinkPosEnd);
        }
        return energyDrinks;
    }
}
