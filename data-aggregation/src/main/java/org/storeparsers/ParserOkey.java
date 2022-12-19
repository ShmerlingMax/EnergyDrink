package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParserOkey extends Parser{
    @Override
    public JsonObject parseStore(Set<String> brands) throws IOException {
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
            StoresParser.LOGGER.info(energyDrink);
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
                StoresParser.LOGGER.warn("Okey", c.fillInStackTrace());
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
}
