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
        return getPhrase();
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

}
