package chatbot.engine.nlp;

import chatbot.engine.Engine;
import chatbot.engine.math.MathFunction;
import chatbot.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;

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
                if (nonZeroScores[j] < nonZeroScores[j + 1]) { // reverse sorting!
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

    public static SentenceV2[] calculateV2(ArrayList<SentenceV2> documents, String[] target_, boolean stemming) {
        // if filtered target words are empty, then return empty array
        if (target_.length == 0) {
            return new SentenceV2[0];
        }

        String[] target = new String[target_.length];

        // copy and to lower case
        for (int i = 0; i < target_.length; i++) {
            target[i] = target_[i].toLowerCase();
        }

        Logger.println("TFIDF input -> " + Arrays.toString(target));

        double[] tfScores = new double[documents.size()];
        double idfCount = 0;

        // count tf and idf
        for (int i = 0; i < documents.size(); i++) {
            int tfCount = 0;
            String sentence = documents.get(i).getSentence().toLowerCase();
            String[] tokenized = Engine.tokenize(sentence);

            if (stemming)
                tokenized = Engine.stem(tokenized);

            for (String word : tokenized) {
                for (String t : target) {
                    if (StringSimilarity.similarity(t.toLowerCase(), word.toLowerCase()) >= 0.63) {// more than 63% similar
                        tfCount++;
                    }
                }
            }

            Logger.println(sentence + " " + tfCount);

//            if (tfCount < 2) // minimum is 2 element
//                tfCount = 0;

            idfCount += (double) tfCount / target.length;

            double tfScore = (double) tfCount / tokenized.length;

            tfScores[i] = tfScore;
        }

        // find the score
        double idfScore = MathFunction.log10((double) (documents.size() + 1) / (idfCount + 1e-9)); // +1 is to avoid first information being ignored

        for (int i = 0; i < documents.size(); i++) {
            tfScores[i] *= idfScore;
        }

        Logger.println("TFIDF score ->" + Arrays.toString(tfScores));
        // find sorted indices
        ArrayList<SentenceV2> filtered = new ArrayList<>();
        int[] sortedIndices = getNonZeroSortedIndex(tfScores);
        int getTop = 5; // get only top 5 results

        for (int i = 0; i < sortedIndices.length; i++) {
            if (i == getTop)
                break;
            filtered.add(documents.get(sortedIndices[i]));
        }

        return filtered.toArray(new SentenceV2[filtered.size()]);
    }

}
