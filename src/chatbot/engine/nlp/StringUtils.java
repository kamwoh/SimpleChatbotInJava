package chatbot.engine.nlp;

import chatbot.engine.Engine;

public class StringUtils {
    public static String replaceAll(String s, String oldWord, String newWord) {
        String[] split = s.split(" ");
        StringBuilder newString = new StringBuilder();
        if (containsWord(s, oldWord)) {
            for (int i = 0; i < split.length; i++) {
                if (split[i].equalsIgnoreCase(oldWord))
                    newString.append(newWord);
                else
                    newString.append(split[i]);
                if (i < split.length - 1)
                    newString.append(" ");
            }
        }
        return newString.toString();
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
