package chatbot.engine.nlp;

import chatbot.engine.Engine;
import chatbot.engine.math.MathFunction;
import chatbot.engine.math.MatrixFunction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TFIDF {
    private static int[] getNonZeroSortedIndex(double[] tfScores) {
        ArrayList<Integer> nonZeroScoreIndices = new ArrayList<>();

        // get only non zero scores for sorting
        for (int i = 0; i < tfScores.length; i++) {
            if (tfScores[i] > 0) {
                nonZeroScoreIndices.add(i);
            }
        }

        // assign into the array
        double[] nonZeroScores = new double[nonZeroScoreIndices.size()];

        for (int i = 0; i < nonZeroScoreIndices.size(); i++) {
            nonZeroScores[i] = tfScores[nonZeroScoreIndices.get(i)];
        }

        // selection sort
        for (int i = 0; i < nonZeroScores.length; i++) {
            for (int j = 0; j < nonZeroScores.length - i - 1; j++) {
                if (nonZeroScores[j] > nonZeroScores[j + 1]) {
                    double temp = nonZeroScores[j];
                    nonZeroScores[j] = nonZeroScores[j + 1];
                    nonZeroScores[j + 1] = temp;

                    int temp2 = nonZeroScoreIndices.get(j);
                    nonZeroScoreIndices.set(j, nonZeroScoreIndices.get(j + 1));
                    nonZeroScoreIndices.set(j + 1, temp2);
                }
            }
        }

        // reassign into int[]
        int[] r = new int[nonZeroScoreIndices.size()];

        for (int i = 0; i < r.length; i++) {
            r[i] = nonZeroScoreIndices.get(i);
        }

        return r;
    }

    public static Sentence[] calculate(ArrayList<Sentence> documents, String[] target_) {
        // if filtered target words are empty, then return empty array
        if (target_.length == 0) {
            return new Sentence[0];
        }

        String[] target = new String[target_.length];

        // copy and to lower case
        for (int i = 0; i < target_.length; i++) {
            target[i] = target_[i].toLowerCase();
        }

        System.out.println("tfidf input -> " + Arrays.toString(target));

        double[] tfScores = new double[documents.size()];
        double idfCount = 0;

        // count tf and idf
        for (int i = 0; i < documents.size(); i++) {
            int tfCount = 0;
            String sentence = documents.get(i).getSentence().toLowerCase();
            String[] tokenized = Engine.tokenize(sentence);

            for (String word : tokenized) {
                for (String t : target) {
                    if (t.equalsIgnoreCase(word))
                        tfCount++;
                }
            }

            int count = 0;
            for (String t : target) {
                if (sentence.contains(t)) {
                    count++;
                }
            }

            idfCount += (double) count / target.length;

            double tfScore = (double) tfCount / tokenized.length;

            tfScores[i] = tfScore;
        }

        // find the score
        double idfScore = MathFunction.log10((double) (documents.size() + 1) / (idfCount + 1e-9)); // +1 is to avoid first information being ignored

        for (int i = 0; i < documents.size(); i++) {
            tfScores[i] *= idfScore;
        }

        System.out.println("tfidf score ->" + Arrays.toString(tfScores));
        // find sorted indices
        ArrayList<Sentence> filtered = new ArrayList<>();
        int[] sortedIndices = getNonZeroSortedIndex(tfScores);
        int getTop = 5; // get only top 5 results

        for (int i = 0; i < sortedIndices.length; i++) {
            if (i == getTop)
                break;
            filtered.add(documents.get(sortedIndices[i]));
        }

        return filtered.toArray(new Sentence[filtered.size()]);
    }

    public static boolean[] findTF(Sentence sentence, String[] importantWords) {
        Phrase[][] phrases = {sentence.getSubjects(), sentence.getAdjectiveNouns(), sentence.getLocations()};

        boolean[] canTake = {true, true, true};
        int[] countTF = {0, 0, 0}; // check tf category

        for (int i = 0; i < countTF.length; i++) {
            for (int j = 0; j < importantWords.length; j++) {
                if (Phrase.phraseContains(phrases[i], importantWords[j])) {
                    canTake[i] = false;
                    countTF[i]++;
                }
            }
        }

        return canTake;
    }
}
