package chatbot.engine.nlp;

import java.util.ArrayList;

public class Phrase {

    private POS[] phrase;

    public Phrase(POS[] phrase) {
        this.phrase = phrase;
    }

    public Phrase(ArrayList<POS> phrase) {
        this(phrase.toArray(new POS[phrase.size()]));
    }

    public String getPhrase() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < phrase.length; i++) {
            ret.append(phrase[i].getTerm());
            if (i != phrase.length - 1)
                ret.append(" ");
        }

        return ret.toString();
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < phrase.length; i++) {
            ret.append(phrase[i].toString());
            if (i != phrase.length - 1)
                ret.append("&");
        }

        return ret.toString();
    }

    public static Phrase[] combine(Phrase[] phrase1, Phrase[] phrase2) {
        Phrase[] combined = new Phrase[phrase1.length + phrase2.length];

        for (int i = 0; i < phrase1.length; i++) {
            System.out.println("phrase 1 -> " + phrase1[i]);
            combined[i] = phrase1[i];
        }

        for (int i = phrase1.length; i < combined.length; i++) {
            System.out.println("phrase 2 -> " + phrase2[i - phrase1.length]);
            combined[i] = phrase2[i - phrase1.length];
        }

        return combined;
    }

    public static boolean phraseContains(Phrase[] phrases, String word) {
        for (Phrase phrase : phrases) {
            if (phraseContains(phrase, word))
                return true;
        }
        return false;
    }

    public static boolean phraseContains(Phrase phrase, String word) {
        boolean result = phrase.getPhrase().toLowerCase().contains(word.toLowerCase());
        System.out.println(phrase.toString().toLowerCase() + " contains " + word.toLowerCase() + " " + result);
        return phrase.getPhrase().toLowerCase().contains(word.toLowerCase());
    }

    public static Phrase parseString(String s) {
        ArrayList<POS> list = new ArrayList<>();
        String[] splitByAmpersand = s.split("&");
        for (int i = 0; i < splitByAmpersand.length; i++) {
            POS pos = POS.parseString(splitByAmpersand[i]);
            list.add(pos);
        }
        return new Phrase(list);
    }

}
