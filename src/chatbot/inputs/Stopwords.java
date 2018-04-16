package chatbot.inputs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Stopwords {

    private static ArrayList<String> stopwords;

    static {
        String filePath = "data/stopwords.txt";

        stopwords = new ArrayList<>();

        try {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String s;

            while ((s = f.readLine()) != null) {
                stopwords.add(s.toLowerCase());
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean contains(String word) {
        return stopwords.contains(word.toLowerCase());
    }

}
