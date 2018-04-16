package chatbot.engine.nlp;

import java.util.ArrayList;

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

}
