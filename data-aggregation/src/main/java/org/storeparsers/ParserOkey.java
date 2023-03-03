package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParserOkey extends Parser{
    private final String baseUrlOkey;
    private final String cookie;
    private final String storeUrl;

    public ParserOkey(String storeUrl) throws IOException {
        this.baseUrlOkey = "https://www.okeydostavka.ru";
        this.cookie = "qrator_jsid=1671392014.424.kyS3IPzWeDk5rNrx-rmq7beradcm6hegoje4v1340lqkl26co";
        this.storeUrl = storeUrl;
    }
    @Override
    public JsonObject parseStore() throws IOException {
        final String logoLink = "https://play-lh.googleusercontent.com/XRU3HtXnV3DitNFXQzO2aE-pGSYSvazaUt8SGNvGFzHOKTwKnIQwhAIrs3OP7Dhf7zWr";
        String[] commands = new String[]{"curl", "--cookie", cookie, storeUrl};
        String responseAll = getHtmlCurl(commands);
        Set<String> energyDrinks = getDrinksUrl(responseAll);

        JsonObject shopOkey = new JsonObject();
        JsonArray eDrinks = new JsonArray();
        int i = 1;
        for (String energyDrink : energyDrinks) {
            commands = new String[]{"curl", "--cookie", cookie, energyDrink};
            String response = getHtmlCurl(commands);

            StoresParser.LOGGER.info(energyDrink);
            try {
                eDrinks.add(parseEnergyDrinkPage(response));
            } catch (RuntimeException c) {
                StoresParser.LOGGER.warn("Okey", c.fillInStackTrace());
            }
        }

        shopOkey.addProperty("name", "ОКЕЙ");
        shopOkey.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopOkey.add("energyDrinks", eDrinks);

        return shopOkey;
    }

    @Override
    public JsonObject parseEnergyDrinkPage(String html) throws IOException {
        final String priceWithoutDiscountKeyword = "class=\"label small crossed\"> ";
        final String priceWithDiscountKeyword = "class=\"price label  label-red \"> ";
        final String offerPriceKeyword = "class=\"price label \"> ";
        final String discountKeyword = "span class=\"special-discount__text\">";
        final String volumeKeyword = "id=\"descAttributeValue_2_6_3074457345618260154_3074457345618267449\"> ";
        final String brandKeyword = "id=\"descAttributeValue_1_6_3074457345618260154_3074457345618267449\"> ";
        final String imgKeyword = "\"productMainImage\" src=\"";
        final String titleKeyword = "\"main_header\" itemprop=\"name\">";
        boolean isDiscounted = true;

        int priceWithoutDiscountPosStart = html.indexOf(priceWithoutDiscountKeyword);
        String priceWithoutDiscount;
        if (priceWithoutDiscountPosStart != -1) {
            priceWithoutDiscount = html.substring(priceWithoutDiscountPosStart + priceWithoutDiscountKeyword.length(),
                    html.indexOf("</span>", priceWithoutDiscountPosStart) - 2);
        } else {
            int offerPricePosStart = html.indexOf(offerPriceKeyword) + offerPriceKeyword.length();
            priceWithoutDiscount = html.substring(offerPricePosStart,
                    html.indexOf("</span>", offerPricePosStart) - 2);
            isDiscounted = false;
        }
        priceWithoutDiscount = priceWithoutDiscount.replaceAll("\\s+", "");
        priceWithoutDiscount = priceWithoutDiscount.replace(',', '.');
        priceWithoutDiscount = priceWithoutDiscount.substring(0, priceWithoutDiscount.length() - 1);

        int priceWithDiscountPosStart = html.indexOf(priceWithDiscountKeyword);
        String priceWithDiscount;
        if (isDiscounted) {
            priceWithDiscount = html.substring(priceWithDiscountPosStart + priceWithDiscountKeyword.length(),
                    html.indexOf("</span>", priceWithDiscountPosStart) - 2);
            priceWithDiscount = priceWithDiscount.replaceAll("\\s+", "");
            priceWithDiscount = priceWithDiscount.replace(',', '.');
            priceWithDiscount = priceWithDiscount.substring(0, priceWithDiscount.length() - 1);
        } else {
            priceWithDiscount = priceWithoutDiscount;
        }

        String discount;
        int discountPosStart = html.indexOf(discountKeyword);
        if (isDiscounted) {
            discount = html.substring(discountPosStart + discountKeyword.length(),
                    html.indexOf("</span>", discountPosStart));
            discount = discount.substring(1, discount.length() - 1);
        } else {
            discount = "0";
        }

        int volumePosStart = html.indexOf(volumeKeyword);
        String volume = html.substring(volumePosStart + volumeKeyword.length(),
                html.indexOf("</div>", volumePosStart));
        volume = volume.replaceAll("\\s+", "");
        volume = volume.replace(",", ".");

        int brandPosStart = html.indexOf(brandKeyword);
        String brand = html.substring(brandPosStart + brandKeyword.length(),
                html.indexOf("</div>", brandPosStart));
        brand = brand.replaceAll("\\s+", "");
        if (brand.equals("О&#039;кей")) {
            brand = "О'кей";
        }
        brand = checkForRecurrentBrand(brand);
        brands.add(brand);

        int imgPosStart = html.indexOf(imgKeyword);
        String imgLink = baseUrlOkey + html.substring(imgPosStart + imgKeyword.length(),
                html.indexOf("\"", imgPosStart + imgKeyword.length()));

        int titlePosStart = html.indexOf(titleKeyword);
        String title = html.substring(titlePosStart + titleKeyword.length(),
                html.indexOf(",", titlePosStart));
        if (Character.isDigit(title.charAt(title.length() - 1))) {
            title = title.substring(0, title.length() - 2);
        }
        if (title.contains("</h1>")) {
            title = title.substring(0, title.indexOf("</h1>"));
        }

        if (volume.length() > 5 || (volume.length() > 1 && !volume.contains("."))) {
            volume = html.substring(html.indexOf(",", titlePosStart) + 2,
                    html.indexOf("</h1>"));
            int i = volume.indexOf("мл");
            if (i == -1) {
                i = volume.indexOf("л");
            }

            volume = volume.substring(0, i);
            volume = volume.replaceAll(",", ".");
        }

        return makeEnergyDrinkJsonObject(title,
                brand,
                imgLink,
                (int) (Double.parseDouble(volume) * 1000),
                Double.parseDouble(priceWithoutDiscount),
                Double.parseDouble(priceWithDiscount),
                Math.abs(Double.parseDouble(discount)) / 100);
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
