package org.storeparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.coobird.thumbnailator.Thumbnails;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Parser {
    public abstract JsonObject parseStore() throws IOException;

    public abstract JsonObject parseEnergyDrinkPage(String html) throws IOException;
    public abstract Set<String> getDrinksUrl(String html) throws IOException;

    public final Set<String> brands = new HashSet<>();
    protected static final List<String> deleteFromTitle = Arrays.asList("напиток", "безалкогольный", "тонизирующий",
            "пастеризованный", "сильногазированный", "энергетический", "сильногаз.", "сильногаз", "газированный",
            "сильногазированныйированный", "негазированный", "л", "мл", "(энергетический)");
    protected static final String defaultImageLink = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Question-mark-grey.jpg/1200px-Question-mark-grey.jpg";


    protected static String getCompressesImageBase64(URL url) throws IOException {
        InputStream in = new BufferedInputStream(url.openStream());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = in.read(buf))) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();

        String imgName = "tmpImg.jpg";
        String imgCompressedName = "thumbnail.jpg";
        FileOutputStream fos = new FileOutputStream(imgName);
        fos.write(response);
        fos.close();

        Thumbnails.of(new File(imgName))
                .size(300, 300)
                .toFile(imgCompressedName);
        File compressedImg = new File(imgCompressedName);
        byte[] imgContent = Files.readAllBytes(compressedImg.toPath());
        compressedImg.delete();
        new File(imgName).delete();

        return Base64.getEncoder().encodeToString(imgContent);
    }

    protected static String removeWordsFromTitle(String title) {
        List<String> words = Stream.of(title.split(" +"))
                .map(String::trim)
                .collect(Collectors.toList());
        List<String> wordsLowerCase = stringsToLowerCase(words);
        String word;
        for (int i = 0; i < wordsLowerCase.size(); i++) {
            word = wordsLowerCase.get(i);
            if (deleteFromTitle.contains(word) || isNumeric(word)) {
                title = title.replace(words.get(i), "");
            }
        }
        title = title.trim().replaceAll(" +", " ");
        return title;
    }

    protected static List<String> stringsToLowerCase(List<String> strings) {
        List<String> result = new ArrayList<>(strings);

        ListIterator<String> iterator = result.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
        return result;
    }

    protected static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected static JsonObject makeEnergyDrinkJsonObject(String fullName, String brand, String imgLink, int volume, double oldPrice, double newPrice, double discount) throws IOException {
        JsonObject eDrink = new JsonObject();
        String img;
        try{
            img = getCompressesImageBase64(new URL(imgLink));
        }catch (FileNotFoundException c){
            img = getCompressesImageBase64(new URL(defaultImageLink));
        }

        eDrink.addProperty("fullName", removeWordsFromTitle(fullName));
        eDrink.addProperty("brand", brand);
        eDrink.addProperty("image", img);
        eDrink.addProperty("volume", volume);
        eDrink.addProperty("priceWithDiscount", newPrice);
        eDrink.addProperty("priceWithOutDiscount", oldPrice);
        eDrink.addProperty("discount", discount);

        return eDrink;
    }

    protected static String getHtmlCurl(String[] commands) throws IOException {
        Process process = Runtime.getRuntime().exec(commands);
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        for (String command : commands) {
            if (command.contains("lenta") && !command.contains("main")) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(StoresParser.class.getClassLoader().getResourceAsStream("lentadrinks.txt")))) {
                    response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }
            } else if (command.contains("auchan") && !command.contains("main")) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(StoresParser.class.getClassLoader().getResourceAsStream("auchandrinks.txt")))) {
                    response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }
            } else if (command.contains("okey") && !command.contains("main")) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(StoresParser.class.getClassLoader().getResourceAsStream("okeydrinks.txt")))) {
                    response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                }
            }
        }

        return response.toString();
    }

    protected static String checkForRecurrentBrand(String brand) {
        if (brand.equals("Redbull") || brand.equals("RED BULL")) {
            return "Red Bull";
        } else if (brand.equals("FlashUp") || brand.equals("ФЛЭШ АП") || brand.equals("Flash Energy")) {
            return "Flash Up";
        } else if (brand.equals("Adrenalinerush") || brand.equals("ADRENALINE")) {
            return "Adrenaline";
        } else if (brand.equalsIgnoreCase("black monster") || brand.equals("MONSTER")) {
            return "Monster";
        } else if (brand.equals("EGOISTE")) {
            return "Egoiste";
        } else if (brand.equals("ГЕНЕЗИС")) {
            return "Genesis";
        } else if (brand.equals("ТANK")) {
            return "World of Tanks";
        } else if (brand.equals("BURN")) {
            return "Burn";
        } else if (brand.equals("DRIVE ME") || brand.equals("Driveme")) {
            return "Drive Me";
        } else if (brand.equals("TORNADO")) {
            return "Tornado";
        } else if (brand.equals("GORILLA")) {
            return "Gorilla";
        } else if (brand.equals("POWER TORR")) {
            return "Powertorr";
        } else if (brand.equals("БАЙКАЛ")) {
            return "Байкал";
        } else if (brand.equals("E-ON")) {
            return "E-On";
        } else if (brand.equals("ОZВЕРИН")) {
            return "OZВЕРИН";
        }
        return brand;
    }
}
