package chatbot.engine.nlp;

import chatbot.utils.Logger;

import java.util.Arrays;

public class SentenceV2 implements Sentence {

    private String sentence;
    private POS[] posTags;

    public SentenceV2(String sentence, POS[] posTags) {
        this.sentence = sentence;
        this.posTags = posTags;
    }

    public POS[] getPosTags() {
        return posTags;
    }

    public String getSentence() {
        return sentence;
    }

    public String toString() {
        // sentence\tn\tpos1\tpos2
        StringBuilder s = new StringBuilder(sentence);
        int n = posTags.length;
        s.append("\t").append(n).append("\t");
        for (int i = 0; i < posTags.length; i++) {
            s.append(posTags[i].toString());
            if (i < posTags.length - 1)
                s.append("\t");
        }
        return s.toString();
    }

    public boolean containsIgnoreCase(String word) {
        word = word.toLowerCase();
        return sentence.toLowerCase().contains(word);
    }

    public int indexOf(String word, boolean ignoreCase) {
        String target = sentence;
        if (ignoreCase)
            target = target.toLowerCase();
        return target.indexOf(word);
    }

    public int indexPosOf(String word, boolean ignoreCase) {
        for (int i = 0; i < posTags.length; i++) {
            POS pos = posTags[i];
            String term = pos.getTerm();
            if (ignoreCase) {
                term = term.toLowerCase();
                word = word.toLowerCase();
            }

            double similarity = StringSimilarity.similarity(term, word);
//            Logger.println("Similarity of " + term + " & " + word + ": " + similarity);
            if (similarity >= 0.6) {
                return i;
            }
        }

        return -1;
    }

    public POS posOf(String word, boolean ignoreCase) {
        for (POS pos : posTags) {
            String term = pos.getTerm();
            if (ignoreCase) {
                term = term.toLowerCase();
                word = word.toLowerCase();
            }

            double similarity = StringSimilarity.similarity(term, word);
            if (similarity >= 0.6) {
                return pos;
            }
        }

        return null;
    }

    public static SentenceV2 parseString(String s) {
        Logger.println("Parsing string into SentenceV2...");
        String[] splitByTab = s.split("\t");
        int n;
        int i = 0;
        String sentenceString = splitByTab[i++];
        n = Integer.parseInt(splitByTab[i++]);
        POS[] posTags = new POS[n];

        for (int j = 0; j < n; j++) {
            posTags[j] = POS.parseString(splitByTab[i++]);
        }
        Logger.println("Done...");

        return new SentenceV2(sentenceString, posTags);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentenceV2 that = (SentenceV2) o;

        if (!sentence.equals(that.sentence)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(posTags, that.posTags);
    }

    @Override
    public int hashCode() {
        int result = sentence.hashCode();
        result = 31 * result + Arrays.hashCode(posTags);
        return result;
    }
}
