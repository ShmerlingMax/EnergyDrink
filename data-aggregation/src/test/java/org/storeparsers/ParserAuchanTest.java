package org.storeparsers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

class ParserAuchanTest extends ParserTest {

    @BeforeEach
    void setLocale() {
        Locale.setDefault(new Locale("en", "RU"));
    }

    @Test
    void parseEnergyDrinkPage() throws IOException {
//        String testHTML = readTxtHtml("auchan/auchanEnergyDrinkHtml.txt");
//        ParserAuchan parser = new ParserAuchan("https://www.auchan.ru/catalog/voda-soki-napitki/energeticheskie-napitki/energeticheskie-napitki/?page=1");
//        String result = parser.parseEnergyDrinkPage(testHTML).toString();
//
//        String expectedResult = readTxtHtml("auchan/auchanEnergyDrinkParseResult.txt").replace("\n", "");
//        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getDrinksUrl() throws IOException {
        String testHTML = readTxtHtml("auchan/auchanDrinksUrlsHtml.txt");
        ParserAuchan parser = new ParserAuchan("https://www.auchan.ru/catalog/voda-soki-napitki/energeticheskie-napitki/energeticheskie-napitki/?page=1");
        Set<String> result = parser.getDrinksUrl(testHTML);
        Set<String> expectedResult = readTxtStringsSet("auchan/auchanDrinksUrlsResult.txt");
        Assertions.assertEquals(result, expectedResult);
    }
}