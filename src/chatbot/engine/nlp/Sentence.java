package chatbot.engine.nlp;

import java.util.ArrayList;
import java.util.Arrays;

public class Sentence {

    private String sentence;
    private Phrase[] subjects = new Phrase[0];
    private Phrase[] adjectiveNouns = new Phrase[0];
    private Phrase[] locations = new Phrase[0];

    public Sentence(String sentence) {
        this.sentence = sentence;
    }

    public void setSubjects(Phrase[] subjects) {
        if (subjects != null)
            this.subjects = subjects;
    }

    public void setAdjectiveNouns(Phrase[] adjectiveNouns) {
        if (adjectiveNouns != null)
            this.adjectiveNouns = adjectiveNouns;
    }

    public void setLocations(Phrase[] locations) {
        if (locations != null)
            this.locations = locations;
    }

    public String getSentence() {
        return sentence;
    }

    public Phrase[] getSubjects() {
        return subjects;
    }

    public Phrase[] getAdjectiveNouns() {
        return adjectiveNouns;
    }

    public Phrase[] getLocations() {
        return locations;
    }

    public String toString() {
        // sentence\tn\t...\tn\t...\tn
        StringBuilder s = new StringBuilder(sentence);
        s.append("\t").append(subjects.length).append("\t");
        for (int i = 0; i < subjects.length; i++) {
            Phrase phrase = subjects[i];
            s.append(phrase.getPhrase()).append("\t");
        }
        s.append(adjectiveNouns.length).append("\t");
        for (int i = 0; i < adjectiveNouns.length; i++) {
            Phrase phrase = adjectiveNouns[i];
            s.append(phrase.getPhrase()).append("\t");
        }

        s.append(locations.length);

        if (locations.length != 0)
            s.append("\t");

        for (int i = 0; i < locations.length; i++) {
            Phrase phrase = locations[i];
            s.append(phrase.getPhrase());
            if (i != locations.length - 1)
                s.append("\t");
        }
        return s.toString();
    }

    public static Sentence parseString(String s) {
        String[] splitByTab = s.split("\t");
        int n;
        int i = 0;
        String sentenceString = splitByTab[i++];
        n = Integer.parseInt(splitByTab[i++]);
        Phrase[] subjects = new Phrase[n];
        for (int j = 0; j < n; j++) {
            subjects[j] = Phrase.parseString(splitByTab[i++]);
        }
        n = Integer.parseInt(splitByTab[i++]);
        Phrase[] adjectiveNouns = new Phrase[n];
        for (int j = 0; j < n; j++) {
            adjectiveNouns[j] = Phrase.parseString(splitByTab[i++]);
        }
        n = Integer.parseInt(splitByTab[i++]);
        Phrase[] locations = new Phrase[n];
        for (int j = 0; j < n; j++) {
            locations[j] = Phrase.parseString(splitByTab[i++]);
        }
        Sentence sentence = new Sentence(sentenceString);
        sentence.setSubjects(subjects);
        sentence.setAdjectiveNouns(adjectiveNouns);
        sentence.setLocations(locations);

        return sentence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sentence sentence1 = (Sentence) o;

        if (!sentence.equals(sentence1.sentence)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(subjects, sentence1.subjects)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(adjectiveNouns, sentence1.adjectiveNouns)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(locations, sentence1.locations);
    }

    @Override
    public int hashCode() {
        int result = sentence.hashCode();
        result = 31 * result + Arrays.hashCode(subjects);
        result = 31 * result + Arrays.hashCode(adjectiveNouns);
        result = 31 * result + Arrays.hashCode(locations);
        return result;
    }
}
