package org.storeparsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

public class ParserPerekrestokTest extends ParserTest{
    @Test
    void parseEnergyDrinkPage() throws IOException {
        String testHTML = readTxtHtml("perekrestok/perekrestokEnergyDrinkHtml.txt");
        ParserPerekrestok parser = new ParserPerekrestok("https://www.perekrestok.ru/cat/c/206/energeticeskie-napitki");
        String result = parser.parseEnergyDrinkPage(testHTML).toString();

        String expectedResult = readTxtHtml("perekrestok/perekrestokEnergyDrinkParseResult.txt").replace("\n", "");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getDrinksUrl() throws IOException {
        String testHTML = readTxtHtml("perekrestok/perekrestokDrinksUrlsHtml.txt");
        ParserPerekrestok parser = new ParserPerekrestok("https://www.perekrestok.ru/cat/c/206/energeticeskie-napitki");
        Set<String> result = parser.getDrinksUrl(testHTML);

        Set<String> expectedResult = readTxtStringsSet("perekrestok/perekrestokDrinksUrlsResult.txt");
        Assertions.assertEquals(result, expectedResult);
    }
}
