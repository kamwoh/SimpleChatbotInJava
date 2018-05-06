package chatbot.engine.nlp;

import chatbot.engine.Engine;

public class StringUtils {
    public static String replaceAll(String s, String oldWord, String newWord) {
        return "";
    }

    public static boolean containsWord(String s, String word) {
        String[] tokenized = Engine.tokenize(s);
        for (String t : tokenized) {
            if (t.equalsIgnoreCase(word))
                return true;
        }
        return false;
    }
}
