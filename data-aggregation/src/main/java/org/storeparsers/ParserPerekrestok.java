package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ParserPerekrestok extends Parser{
    private final String storeUrl;

    public ParserPerekrestok(String storeUrl) throws IOException {
        this.storeUrl = storeUrl;
    }
    @Override
    public JsonObject parseStore() throws IOException {
        final String logoLink = "https://gidpopromo.ru/storage/2021/07/12/d68db4a606380cf445af4df5997420d5b61e3a52.jpg";

        String[] commands = new String[]{"curl", storeUrl};
        String responseAll = getHtmlCurl(commands);

        Set<String> energyDrinks = getDrinksUrl(responseAll);

        JsonObject shopPerekrestok = new JsonObject();
        JsonArray eDrinks = new JsonArray();

        for (String energyDrink : energyDrinks) {
            commands = new String[]{"curl", energyDrink};
            String response = getHtmlCurl(commands);

            try {
                JsonObject eDrink = parseEnergyDrinkPage(response);
                if (eDrink == null){
                    continue;
                }
                eDrinks.add(eDrink);
            } catch (RuntimeException c) {
                StoresParser.LOGGER.warn("Perekrestok", c.fillInStackTrace());
            }
        }
        shopPerekrestok.addProperty("name", "ПЕРЕКРЕСТОК");
        shopPerekrestok.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopPerekrestok.add("energyDrinks", eDrinks);
        return shopPerekrestok;
    }

    @Override
    public JsonObject parseEnergyDrinkPage(String html) throws IOException {

        final String fullNameKeyword = "<h1 class=\"sc-fubCzh ibFUIH product__title\" itemProp=\"name\">";
        final String brandKeyword = "<h3 class=\"product-brand__title\">";
        final String imgKeyword = "<div class=\"product__gallery\"><div class=\"Flex-brknwi-0";
        final String priceKeyword = "<div class=\"Flex-brknwi-0";
        final String newPriceKeyword = "<div class=\"price-new\">";
        final String oldPriceKeyword = "<div class=\"price-old\">";
        final String discountKeyword = "<div class=\"sc-xyEDr eYwDjP sc-irOPex koUUzn\">";
        boolean isDiscounted = true;

        int fullNamePosStart = html.indexOf(fullNameKeyword);
        String fullName = html.substring(fullNamePosStart + fullNameKeyword.length(),
                html.indexOf(",", fullNamePosStart));
        String volume = html.substring(html.indexOf(",", fullNamePosStart) + 1,
                html.indexOf("</h1>",
                        html.indexOf(",", fullNamePosStart) + 1));

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

        int brandPosStart = html.indexOf(brandKeyword);
        String brand = html.substring(brandPosStart + brandKeyword.length(),
                html.indexOf("</h3>", brandPosStart));
        brand = checkForRecurrentBrand(brand);

        int imgLinkPosStart1 = html.indexOf(imgKeyword);
        int imgLinkPosStart2 = html.indexOf("<img src=\"", imgLinkPosStart1);
        String imgLink = html.substring(imgLinkPosStart2 + "<img src=\"".length(),
                html.indexOf("\"", imgLinkPosStart2 +
                        "<img src=\"".length()));


        String span = "<span>";
        String discount;
        int discountPosStart = html.indexOf(discountKeyword);
        if (discountPosStart != -1) {
            int discountPosStart1 = html.indexOf(span, discountPosStart);
            discount = html.substring(discountPosStart1 + span.length(),
                    html.indexOf("</span>", discountPosStart1));
            discount = discount.substring(0, discount.length() - 1);
        } else {
            discount = "0";
            isDiscounted = false;
        }


        int pricePosStart = html.indexOf(priceKeyword);

        String newPrice = html.substring(html.indexOf(newPriceKeyword, pricePosStart) + newPriceKeyword.length(),
                html.indexOf("</div>", html.indexOf(newPriceKeyword, pricePosStart)) - 2);
        newPrice = newPrice.replace(',', '.');

        String oldPrice;
        if (isDiscounted) {
            oldPrice = html.substring(html.indexOf(oldPriceKeyword, pricePosStart) + oldPriceKeyword.length(),
                    html.indexOf("</div>", html.indexOf(oldPriceKeyword, pricePosStart)) - 2);
            oldPrice = oldPrice.replace(',', '.');
        } else {
            oldPrice = newPrice;
        }
        if (brand.length() > 50 || fullName.length() > 50){
            return null;
        }
        brands.add(brand);
        return makeEnergyDrinkJsonObject(fullName, brand,
                imgLink, volumeRes, Double.parseDouble(oldPrice),
                Double.parseDouble(newPrice), Math.abs((double) Math.abs(Integer.parseInt(discount)) / 100));
    }

    @Override
    public Set<String> getDrinksUrl(String html) throws IOException {
        final String productKeyword = "class=\"sc-fFucqa dUNCjf product-card__link\"";
        final String productLinkKeyword = "href=\"";
        final String baseUrl = "https://www.perekrestok.ru";

        Set<String> energyDrinks = new HashSet<>();



        int productPosStart = html.indexOf(productKeyword);
        while (productPosStart != -1) {
            int productLinkPosStart = html.indexOf(productLinkKeyword, productPosStart);
            int productLinkPosEnd = html.indexOf("\"",
                    productLinkPosStart + productLinkKeyword.length());
            String productLink = baseUrl +
                    html.substring(productLinkPosStart + productLinkKeyword.length(), productLinkPosEnd);
            energyDrinks.add(productLink);
            productPosStart = html.indexOf(productKeyword, productLinkPosEnd);
        }
        return energyDrinks;
    }
}
