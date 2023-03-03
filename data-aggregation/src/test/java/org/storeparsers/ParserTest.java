package org.storeparsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ParserTest {

    String readTxtHtml(String fileName) throws IOException {
        StringBuilder response;
        String line;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(StoresParser.class.getClassLoader().getResourceAsStream(fileName)))) {
            response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }
        }

        return response.toString();
    }

    Set<String> readTxtStringsSet(String fileName) throws IOException {
        Set<String> result = new HashSet<>();
        String line;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(StoresParser.class.getClassLoader().getResourceAsStream(fileName)))) {
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }
}
