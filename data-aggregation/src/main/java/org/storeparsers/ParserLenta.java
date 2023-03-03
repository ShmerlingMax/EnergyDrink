package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class ParserLenta extends Parser {

    private final String cookie;
    private final String storeUrl;

    public ParserLenta(String storeUrl) throws IOException {
        this.cookie = "qrator_jsid=1671392014.424.kyS3IPzWeDk5rNrx-rmq7beradcm6hegoje4v1340lqkl26co";
        this.storeUrl = storeUrl;
    }

    @Override
    public JsonObject parseStore() throws IOException {
        final String logoLink = "https://upload.wikimedia.org/wikipedia/commons/9/94/%D0%9B%D0%95%D0%9D%D0%A2%D0%90_%D0%BB%D0%BE%D0%B3%D0%BE.jpg";

        JsonObject shopLenta = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        String responseAll = "";
        String[] commands = new String[]{"curl", "--cookie", cookie, storeUrl};
        responseAll += getHtmlCurl(commands);
        Set<String> energyDrinks = getDrinksUrl(responseAll);

        for (String energyDrink : energyDrinks) {
            commands = new String[]{"curl", "--cookie", cookie, energyDrink, "-k"};
            String response = getHtmlCurl(commands);
            StoresParser.LOGGER.info(energyDrink);
            try {
                eDrinks.add(parseEnergyDrinkPage(response));
            } catch (StringIndexOutOfBoundsException | IOException c) {
                StoresParser.LOGGER.warn("Lenta", c.fillInStackTrace());
            }
        }
        shopLenta.addProperty("name", "ЛЕНТА");
        shopLenta.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopLenta.add("energyDrinks", eDrinks);
        return shopLenta;
    }

    @Override
    public JsonObject parseEnergyDrinkPage(String html) throws IOException {
        final String fullNameKeyword = "<h1 class=\"sku-page__title\" itemprop=\"name\">        ";
        final String brandKeyword = "{&quot;key&quot;:&quot;Бренд&quot;,&quot;value&quot;:&quot;";
        final String imgKeyword = "<div class=\"sku-images-slider__image-block square__inner\">        <img          src=\"";
        final String volumeKeyword = "&quot;key&quot;:&quot;Упаковка&quot;,&quot;value&quot;:&quot;";
        final String oldPriceKeyword = "&quot;regularPrice&quot;:{&quot;value&quot;:";
        final String newPriceKeyword = "&quot;cardPrice&quot;:{&quot;value&quot;:";
        final String discountKeyword = "<div class=\"discount-label-small discount-label-small--sku-page " +
                "sku-page__discount-label\">";

        int fullNamePosStart = html.indexOf(fullNameKeyword) + fullNameKeyword.length();
        String fullName = html.substring(fullNamePosStart, html.indexOf(",", fullNamePosStart));

        int brandPosStart = html.indexOf(brandKeyword) + brandKeyword.length();
        String brand = html.substring(brandPosStart, html.indexOf("&quot;", brandPosStart));
        brand = checkForRecurrentBrand(brand);
        brands.add(brand);

        int imgLinkPosStart = html.indexOf(imgKeyword) + imgKeyword.length();
        String imgLink = html.substring(imgLinkPosStart, html.indexOf("\"", imgLinkPosStart));
        if (imgLink.length() < 10) {
            imgLink = defaultImageLink;
        }

        int volumePosStart = html.indexOf(volumeKeyword) + volumeKeyword.length();
        int volume = (int) (Double.parseDouble(html.substring(volumePosStart,
                html.indexOf(" ", volumePosStart))) * 1000);

        int oldPricePosStart = html.indexOf(oldPriceKeyword) + oldPriceKeyword.length();
        double oldPrice = Double.parseDouble(html.substring(oldPricePosStart,
                html.indexOf(",", oldPricePosStart)));

        int newPricePosStart = html.indexOf(newPriceKeyword) + newPriceKeyword.length();
        double newPrice = Double.parseDouble(html.substring(newPricePosStart,
                html.indexOf(",", newPricePosStart)));

        int discountPosStart = html.indexOf(discountKeyword);
        double discount;
        if (discountPosStart == -1) {
            discount = Math.abs((oldPrice - newPrice) / oldPrice);
            DecimalFormat df = new DecimalFormat("#.00");
            discount = Double.parseDouble(df.format(discount));
        } else {
            String discountStr = html.substring(discountPosStart + discountKeyword.length(),
                    html.indexOf("</div>", discountPosStart));
            discountStr = discountStr.replaceAll("\\s+", "");
            discountStr = discountStr.substring(1, discountStr.length() - 1);
            discount = (double) Integer.parseInt(discountStr) / 100;
        }
        return makeEnergyDrinkJsonObject(fullName, brand, imgLink, volume, oldPrice, newPrice, discount);
    }

    @Override
    public Set<String> getDrinksUrl(String html) throws IOException {
        int productPosStart = html.indexOf("https", 0);
        Set<String> energyDrinks = new HashSet<>();
        html += "\n";
        while (productPosStart != -1) {
            int productLinkPosEnd = html.indexOf("\n", productPosStart);
            String productLink = html.substring(productPosStart, productLinkPosEnd);

            energyDrinks.add(productLink);
            productPosStart = html.indexOf("https", productLinkPosEnd);
        }
        return energyDrinks;
    }
}
