package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ParserAuchan extends Parser{
    @Override
    public JsonObject parseStore(Set<String> brands) throws IOException {
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
            StoresParser.LOGGER.info(energyDrink);

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
}
