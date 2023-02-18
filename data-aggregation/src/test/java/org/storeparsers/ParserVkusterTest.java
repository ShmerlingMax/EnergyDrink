package org.storeparsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Set;

public class ParserVkusterTest extends ParserTest {
    @Test
    void parseEnergyDrinkPage() throws IOException {
        String testHTML = readTxtHtml("vkuster/vkusterEnergyDrinkHtml.txt");
        ParserVkuster parser = new ParserVkuster();
        String result = parser.parseEnergyDrinkPage(testHTML).toString() + "\n";

        String expectedResult = readTxtHtml("vkuster/vkusterEnergyDrinkParseResult.txt");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getDrinksUrl() throws IOException {
        String testHTML = readTxtHtml("vkuster/vkusterDrinksUrlsHtml.txt");
        ParserVkuster parser = new ParserVkuster();
        Set<String> result = parser.getDrinksUrl(testHTML);

        Set<String> expectedResult = readTxtStringsSet("vkuster/vkusterDrinksUrlsResult.txt");
        Assertions.assertEquals(result, expectedResult);
    }
}
