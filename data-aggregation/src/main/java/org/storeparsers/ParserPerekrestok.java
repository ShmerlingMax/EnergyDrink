package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ParserPerekrestok extends Parser{
    @Override
    public JsonObject parseStore(Set<String> brands) throws IOException {
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
            StoresParser.LOGGER.info(energyDrink);
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
                StoresParser.LOGGER.warn("Perekrestok", c.fillInStackTrace());
            }
        }
        shopPerekrestok.addProperty("name", "ПЕРЕКРЕСТОК");
        shopPerekrestok.addProperty("image", getCompressesImageBase64(new URL(logoLink)));
        shopPerekrestok.add("energyDrinks", eDrinks);
        return shopPerekrestok;
    }
}
