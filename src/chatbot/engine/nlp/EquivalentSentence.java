package chatbot.engine.nlp;

import java.util.StringTokenizer;

public class EquivalentSentence {

    public final static String[] equivalentVerb = {"is", "are"};

    public static Sentence getEquivalentSentence(Sentence sentence) {
        int count = 0;
        String occurVerb = "";
        for (String eVerb : equivalentVerb) {
            StringTokenizer st = new StringTokenizer(sentence.getSentence());
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equalsIgnoreCase(eVerb)) {
                    occurVerb = eVerb;
                    count++;
                }
            }

            if (count == 1)
                break;
        }

        if (count == 1) {
            String s = "";
            int i = 0;
            for (Phrase p : sentence.getSubjects()) {
                s += p.getPhrase();
                if (i != sentence.getSubjects().length)
                    s += " ";
                i += 1;
            }

            s += occurVerb + " ";

            i = 0;
            for (Phrase p : sentence.getAdjectiveNouns()) {
                s += p.getPhrase();
                if (i != sentence.getSubjects().length)
                    s += " ";
                i += 1;
            }

            Sentence newSentence = new Sentence(s);
            newSentence.setAdjectiveNouns(sentence.getSubjects());
            newSentence.setSubjects(sentence.getAdjectiveNouns());
            return newSentence;
        }

        return null;
    }


}
