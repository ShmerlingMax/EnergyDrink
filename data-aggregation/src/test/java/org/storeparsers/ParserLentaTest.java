package org.storeparsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

public class ParserLentaTest extends ParserTest {
    @Test
    void parseEnergyDrinkPage() throws IOException {
        String testHTML = readTxtHtml("lenta/lentaEnergyDrinkHtml.txt");
        ParserLenta parser = new ParserLenta();
        String result = parser.parseEnergyDrinkPage(testHTML).toString();

        String expectedResult = readTxtHtml("lenta/lentaEnergyDrinkParseResult.txt").replace("\n", "");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getDrinksUrl() throws IOException {
        String testHTML = readTxtHtml("lenta/lentaDrinksUrlsHtml.txt");
        ParserLenta parser = new ParserLenta();
        Set<String> result = parser.getDrinksUrl(testHTML);
        Set<String> expectedResult = readTxtStringsSet("lenta/lentaDrinksUrlsResult.txt");
        Assertions.assertEquals(result, expectedResult);
    }
}
