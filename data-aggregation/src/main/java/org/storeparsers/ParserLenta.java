package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParserLenta extends Parser {
    @Override
    public JsonObject parseStore(Set<String> brands) throws IOException {
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
            StoresParser.LOGGER.info(energyDrink);
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
            } catch (StringIndexOutOfBoundsException | IOException c) {
                StoresParser.LOGGER.warn("Lenta", c.fillInStackTrace());
            }
        }
        shopLenta.addProperty("name", "ЛЕНТА");
        shopLenta.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopLenta.add("energyDrinks", eDrinks);
        return shopLenta;
    }
}
