package chatbot.engine;

import chatbot.engine.math.MathFunction;
import chatbot.engine.nlp.POS;
import chatbot.engine.nlp.Stemmer;
import chatbot.inputs.Stopwords;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Engine {

    private static String punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    public static String[] tokenize(String sentence) {
        ArrayList<String> filtered = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(sentence);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();

            if (s.length() == 0)
                continue;

            if (MathFunction.isNumber(s)) {
                filtered.add(s);
                continue;
            }

            // assume correct sentence structure
            boolean firstPunctuation = punctuation.contains(s.charAt(0) + "");
            boolean lastPunctuation = punctuation.contains(s.charAt(s.length() - 1) + "");

            int start = 0;
            int end = s.length();

            if (firstPunctuation) {
                start = 1;
                filtered.add(s.charAt(0) + "");
            }

            if (lastPunctuation) {
                end -= 1;
                filtered.add(s.substring(start, end));
                filtered.add(s.charAt(s.length() - 1) + "");
            } else {
                filtered.add(s.substring(start, end));
            }
        }
        return filtered.toArray(new String[filtered.size()]);
    }

    public static String stem(String input) {
        Stemmer stemmer = new Stemmer();

        for (char c : input.toLowerCase().toCharArray())
            stemmer.add(c);

        stemmer.stem();

        return stemmer.toString();
    }

    public static String[] stem(String[] inputs) {
        ArrayList<String> list = new ArrayList<>();

        for (String input : inputs) {
            Stemmer stemmer = new Stemmer();

            for (char c : input.toLowerCase().toCharArray())
                stemmer.add(c);

            stemmer.stem();

            list.add(stemmer.toString());
        }

        return list.toArray(new String[list.size()]);
    }

    public static String[] removeUnimportantWords(POS[] tokenized) {
        ArrayList<String> filtered = new ArrayList<>();
        int i = 0;
        int maxCount = 2; // consider only first 2 words are unimportant words
        for (POS s : tokenized) {
            // check tag
            boolean firstFilter = s.isVerb() || s.isNoun() || s.isAdj() || s.isAdv() || s.isAdp() || s.isNum() || s.isPron() || s.isDet();
            if (i < maxCount) {
                // first filter should eliminate all punctuation & stopwords dy, do below just in case tagging is wrong.
                boolean notPunctuation = firstFilter && !punctuation.contains(s.getTerm());
                // check stopwords
                boolean notStopword = notPunctuation && !Stopwords.contains(s.getTerm());

                i++;

                if (notStopword) {
                    filtered.add(s.getTerm());
                }
            } else {
                if (firstFilter) // only condition stated above
                    filtered.add(s.getTerm());
            }
        }

        return filtered.toArray(new String[filtered.size()]);
    }

    public static boolean isQuestion(String[] tokenized) {
        int i = 0;
        boolean isWhat = tokenized[i].equalsIgnoreCase("what");
        boolean isWho = isWhat || tokenized[i].equalsIgnoreCase("who");
        boolean isWhen = isWho || tokenized[i].equalsIgnoreCase("when");
        boolean isWhere = isWhen || tokenized[i].equalsIgnoreCase("where");
        boolean isWhy = isWhere || tokenized[i].equalsIgnoreCase("why");
        boolean isHow = isWhy || tokenized[i].equalsIgnoreCase("how");
        boolean containQuestionMark = isHow || tokenized[tokenized.length - 1].equalsIgnoreCase("?");

        return containQuestionMark;
    }

    public static boolean isYesNoQuestion(String[] tokenized) {
        int i = 0;

        boolean isIs = tokenized[i].equalsIgnoreCase("is");
        boolean isAre = isIs || tokenized[i].equalsIgnoreCase("are");
        boolean isDid = isAre || tokenized[i].equalsIgnoreCase("did");
        boolean isDo = isDid || tokenized[i].equalsIgnoreCase("do");
        boolean isDoes = isDo || tokenized[i].equalsIgnoreCase("does");

        return isDoes;
    }
}
