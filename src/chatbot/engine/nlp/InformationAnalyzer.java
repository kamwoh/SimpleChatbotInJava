package chatbot.engine.nlp;

import chatbot.inputs.Stopwords;
import chatbot.inputs.dataset.POSDataset;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This analyzer assumes sentences are in correct grammar.
 */
public class InformationAnalyzer {

    private static ArrayList<POS> specialPOS = new ArrayList<>();

    static {
        specialPOS.add(new POS("in front of", POS.ADP));
        specialPOS.add(new POS("close to", POS.ADP));
        specialPOS.add(new POS("next to", POS.ADP));
        specialPOS.add(new POS("out of", POS.ADP));
        specialPOS.add(new POS("on the way to", POS.ADP));
    }

    public static Phrase[] findAdjectiveNoun(POS[] tokenizedTags) {
        // After verb
        ArrayList<Integer> verbLocation = new ArrayList<>();
        int i, j;

        for (i = 0; i < tokenizedTags.length; i++) {
            POS token = tokenizedTags[i];
            if (token.isVerb()) {
                // check if next one if particle or not
                if (i < tokenizedTags.length - 1) {
                    POS nextToken = tokenizedTags[i + 1];
                    if (nextToken.isPrt()) {
                        token = new POS(token.getTerm() + " " + nextToken.getTerm(), POS.VERB);
                        i += 1;
                    }
                }

                verbLocation.add(i);
            }
        }

        if (verbLocation.isEmpty()) {
            return new Phrase[0];
        } else {
            ArrayList<Phrase> subjects = new ArrayList<>();

            for (int j2 = 0; j2 < verbLocation.size(); j2++) {
                ArrayList<POS> posList = new ArrayList<>();

                int currentVerbIndex = verbLocation.get(j2);
                int nextVerbIndex = tokenizedTags.length;

                if (j2 != verbLocation.size() - 1) {
                    nextVerbIndex = verbLocation.get(j2);
                }

                for (int k = currentVerbIndex + 1; k < nextVerbIndex; k++) {
                    POS token = tokenizedTags[k];
                    // do i need to filter?
                    if (!(token.isConj() || token.isDet() || token.isPunc() || token.isAdp())) {
                        System.out.println(token.getTerm() + " <---------");
                        posList.add(token);
                    } else if (!posList.isEmpty()) {
                        Phrase phrase = new Phrase(posList);
                        subjects.add(phrase);
                        posList.clear();
                    }
                }

                if (!posList.isEmpty()) {
                    Phrase phrase = new Phrase(posList);
                    subjects.add(phrase);
                    posList.clear();
                }
            }

            return subjects.toArray(new Phrase[subjects.size()]);
        }
    }

    public static Phrase[] findSubject(POS[] tokenizedTags, boolean isQuestion) {
        // The subject of a sentence is the person, place, thing, or idea that is doing or being something.
        // 1. find verb
        // 2. check if it is passive - hard!!
        // 3. get subject
        if (isQuestion)
            return findAdjectiveNoun(tokenizedTags);

        ArrayList<Integer> verbLocation = new ArrayList<>();
        int i;
        for (i = 0; i < tokenizedTags.length; i++) {
            POS token = tokenizedTags[i];
            if (token.isVerb()) {
                // check if next one if particle or not
                if (i < tokenizedTags.length - 1) {
                    POS nextToken = tokenizedTags[i + 1];
                    if (nextToken.isPrt()) {
                        token = new POS(token.getTerm() + " " + nextToken.getTerm(), POS.VERB);
                        i += 1;
                    }
                }

                verbLocation.add(i);
            }
        }

        if (verbLocation.isEmpty()) {
            ArrayList<Phrase> subjects = new ArrayList<>();
            subjects.add(new Phrase(tokenizedTags));
            return subjects.toArray(new Phrase[1]);
        } else {
            ArrayList<Phrase> subjects = new ArrayList<>();
            i = 0;
            for (Integer j : verbLocation) {
                ArrayList<POS> posList = new ArrayList<>();
                for (int k = i; k < j; k++) {
                    POS token = tokenizedTags[k];
                    // do i need to filter?
                    if (!(token.isConj() || token.isDet() || token.isPunc()) && !Stopwords.contains(token.getTerm()))
                        posList.add(token);
                }

                if (!posList.isEmpty()) {
                    Phrase phrase = new Phrase(posList);
                    subjects.add(phrase);
                }
                i = j + 1;
            }

            return subjects.toArray(new Phrase[subjects.size()]);
        }
    }

    private static POS tryToFormSpecialPOS(POS[] tokenizedTags, int currentI) {
        int left = tokenizedTags.length - currentI;
        if (left >= 2) {
            POS first = tokenizedTags[currentI];
            POS second = tokenizedTags[currentI + 1];

            String firstTerm = first.getTerm();
            String secondTerm = second.getTerm();
            String combineTerm = firstTerm + " " + secondTerm;
            POS firstCheck = new POS(combineTerm, POS.ADP);

            if (specialPOS.contains(firstCheck))
                return firstCheck;

            if (left >= 3) {
                POS third = tokenizedTags[currentI + 2];
                String thirdTerm = third.getTerm();
                combineTerm = combineTerm + " " + thirdTerm;
                POS secondCheck = new POS(combineTerm, POS.ADP);

                if (specialPOS.contains(secondCheck))
                    return secondCheck;
            }

            if (left >= 4) {
                POS forth = tokenizedTags[currentI + 3];
                String forthTerm = forth.getTerm();
                combineTerm = combineTerm + " " + forthTerm;
                POS thirdCheck = new POS(combineTerm, POS.ADP);

                if (specialPOS.contains(thirdCheck))
                    return thirdCheck;
            }
        }

        return null;
    }

    public static Phrase[] findWhenAndWhere(POS[] tokenizedTags) {
        ArrayList<Integer> whereLocation = new ArrayList<>();
        int i;
        boolean afterAdp = false;
        for (i = 0; i < tokenizedTags.length; i++) {
            POS specialCheck = tryToFormSpecialPOS(tokenizedTags, i);
            boolean canAdd;

            if (specialCheck != null) {
                // Our house is close to the supermarket.
                //              i=3
                //                    i=4
                i += specialCheck.getTermLength() - 1;
                canAdd = i < tokenizedTags.length - 1;
            } else {
                POS tag = tokenizedTags[i];
                canAdd = tag.isAdp() && i < tokenizedTags.length - 1;
            }

            if (canAdd) {
                whereLocation.add(i);
                afterAdp = true;
            }

            if (afterAdp) { // check for conjunction
                POS tag = tokenizedTags[i];
                if (tag.isConj()) {
                    whereLocation.add(i);
                    afterAdp = false; // continue search for next adp
                }
            }
        }

        if (whereLocation.isEmpty()) {
            // if last word of an information is adp, probably not a correct sentence.
            // or not found
            return new Phrase[0];
        } else {
            ArrayList<Phrase> target = new ArrayList<>();

            for (Integer j : whereLocation) {
                ArrayList<POS> posList = new ArrayList<>();
                POS adp = tokenizedTags[j];
                POS nextToken = tokenizedTags[j + 1];

                while (nextToken.isNoun() || nextToken.isDet() || nextToken.isPron() || nextToken.isNum()) {
                    posList.add(nextToken);

                    j++;

                    if (j == tokenizedTags.length - 1)
                        break;

                    nextToken = tokenizedTags[j + 1];
                }

                Phrase phrase = new Phrase(posList);
                target.add(phrase);
            }

            return target.toArray(new Phrase[target.size()]);
        }
    }

}
