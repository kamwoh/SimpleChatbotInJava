package chatbot.inputs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LexiconDatabase {
    private static String filePath = "data/posTag.txt";
    private static HashMap<String, String[]> lexiconMap;

    static {
        load();
    }

    private static void load() {
        lexiconMap = new HashMap<>();

        try {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String s;
            while ((s = f.readLine()) != null) {
                String[] splits = s.split(" ");
                String word = splits[0];
                String[] tags = new String[splits.length - 1];
                for (int i = 1; i < splits.length; i++) {
                    tags[i - 1] = splits[i];
                }
                lexiconMap.put(word, tags);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static boolean contains(String word) {
        return lexiconMap.containsKey(word.toLowerCase());
    }

}
