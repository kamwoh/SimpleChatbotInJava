package chatbot.engine.nlp;

import chatbot.engine.Engine;

import java.util.ArrayList;

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

    public static String[] splitByKeyword(String input, String word) {
        String[] split = input.split(" ");
        ArrayList<String> answers = new ArrayList<>();
        ArrayList<String[]> newStrings = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        boolean reset = false;
        for (String s : split) {
            if (s.equalsIgnoreCase(word))
                reset = true;

            if (!reset) {
                temp.add(s);
            } else {
                newStrings.add(temp.toArray(new String[temp.size()]));
                temp = new ArrayList<>();
                reset = false;
            }
        }

        for (String[] newString : newStrings) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < newString.length; i++) {
                sb.append(newString[i]);
                if (i < newString.length)
                    sb.append(" ");
            }
            answers.add(sb.toString());
        }
        return answers.toArray(new String[answers.size()]);
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
