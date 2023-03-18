package org.storeparsers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class ParserOkeyTest extends ParserTest {

    @BeforeEach
    void setLocale() {
        Locale.setDefault(new Locale("en", "RU"));
    }
    @Test
    void parseEnergyDrinkPage() throws IOException {
        String testHTML = readTxtHtml("okey/okeyEnergyDrinkHtml.txt");
        ParserOkey parser = new ParserOkey("https://www.okeydostavka.ru/spb/goriachie-i-kholodnye-napitki/energeticheskie-napitki");
        String result = parser.parseEnergyDrinkPage(testHTML).toString();

        String expectedResult = readTxtHtml("okey/okeyEnergyDrinkParseResult.txt").replace("\n", "");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void getDrinksUrl() throws IOException {
        String testHTML = readTxtHtml("okey/okeyDrinksUrlsHtml.txt");
        ParserOkey parser = new ParserOkey("https://www.okeydostavka.ru/spb/goriachie-i-kholodnye-napitki/energeticheskie-napitki");
        Set<String> result = parser.getDrinksUrl(testHTML);
        Set<String> expectedResult = readTxtStringsSet("okey/okeyDrinksUrlsResult.txt");
        Assertions.assertEquals(result, expectedResult);
    }
}
