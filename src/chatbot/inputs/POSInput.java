package chatbot.inputs;

import chatbot.engine.math.MathFunction;
import chatbot.engine.math.MatrixFunction;
import chatbot.inputs.dataset.POSDataset;
import chatbot.utils.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class POSInput {

    public static String[][][] loadDataset(String filePath) {
        ArrayList<String[][]> taggedDocuments = new ArrayList<>();
        try {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String s;
            while ((s = f.readLine()) != null) {
                ArrayList<String[]> taggedSentences = new ArrayList<>();

                String[] splits = s.split("\t");

                for (String s1 : splits) {
                    taggedSentences.add(s1.split("="));
                }

                taggedDocuments.add(taggedSentences.toArray(new String[taggedSentences.size()][]));
            }

        } catch (IOException e) {
            throw new RuntimeException(filePath + " not found");
        }

        return taggedDocuments.toArray(new String[taggedDocuments.size()][][]);
    }

    public static POSDataset transformToDataset(String[][][] taggedDocuments) {
        ArrayList<String[]> X = new ArrayList<>();
        ArrayList<String> y = new ArrayList<>();

        for (String[][] posTags : taggedDocuments) {
            String[] untags = untag(posTags);
            for (int i = 0; i < posTags.length; i++) {
                String tag = posTags[i][1];

                X.add(convertToFeatures(untags, i));
                y.add(tag);
            }
        }

        return new POSDataset(X, y);
    }

    public static String[] untag(String[][] taggedSentences) {
        String[] out = new String[taggedSentences.length];
        for (int i = 0; i < taggedSentences.length; i++) {
            out[i] = taggedSentences[i][0];
        }
        return out;
    }

    public static String[] convertToFeatures(String[] sentenceTerms, int index) {
        String term = sentenceTerms[index];
        ArrayList<String> keys = new ArrayList<>();

        keys.add("word=" + term);
        keys.add("is_first=" + (index == 0));
        keys.add("is_last=" + (index == sentenceTerms.length - 1));
        keys.add("is_capitalized=" + (term.substring(0, 1).toUpperCase()).equals(term.substring(0, 1)));
        keys.add("is_all_caps=" + term.toUpperCase().equals(term));
        keys.add("is_all_lower=" + term.toLowerCase().equals(term));
        keys.add("prefix-1=" + term.substring(0, 1));
        keys.add("prefix-2=" + (term.length() <= 1 ? term : term.substring(0, 2)));
        keys.add("prefix-3=" + (term.length() <= 2 ? term : term.substring(0, 3)));
        keys.add("suffix-1=" + term.substring(term.length() - 1, term.length()));
        keys.add("suffix-2=" + (term.length() <= 1 ? term : term.substring(term.length() - 2, term.length())));
        keys.add("suffix-3=" + (term.length() <= 2 ? term : term.substring(term.length() - 3, term.length())));
        keys.add("prev_word=" + (index == 0 ? "" : sentenceTerms[index - 1]));
        keys.add("next_Word=" + (index == sentenceTerms.length - 1 ? "" : sentenceTerms[index + 1]));
        keys.add("has_hyphen=" + term.contains("-"));
        keys.add("is_numeric=" + MathFunction.isNumber(term));
        keys.add("capitals_inside=" + !term.substring(1).toLowerCase().equals(term.substring(1)));

        return keys.toArray(new String[keys.size()]);
    }

    public static String[][] convertToFeatures(String[] sentenceTerms) {
        /* {
         'word': sentence_terms[index],
         'is_first': index == 0,
         'is_last': index == len(sentence_terms) - 1,
         'is_capitalized': sentence_terms[index][0].upper() == sentence_terms[index][0],
         'is_all_caps': sentence_terms[index].upper() == sentence_terms[index],
         'is_all_lower': sentence_terms[index].lower() == sentence_terms[index],
         'prefix-1': sentence_terms[index][0],
         'prefix-2': sentence_terms[index][:2],
         'prefix-3': sentence_terms[index][:3],
         'suffix-1': sentence_terms[index][-1],
         'suffix-2': sentence_terms[index][-2:],
         'suffix-3': sentence_terms[index][-3:],
         'prev_word': '' if index == 0 else sentence_terms[index - 1],
         'next_word': '' if index == len(sentence_terms) - 1 else sentence_terms[index + 1],
         'has_hyphen': '-' in sentence_terms[index],
         'is_numeric': sentence_terms[index].isdigit(),
         'capitals_inside': sentence_terms[index][1:].lower() != sentence_terms[index][1:]
         }
         */
        Logger.println("Converting input to features...");

        String[][] out = new String[sentenceTerms.length][POSDataset.getVocabSize()];

        for (int index = 0; index < sentenceTerms.length; index++) {
            out[index] = convertToFeatures(sentenceTerms, index);
            Logger.println(sentenceTerms[index] + " -> " + Arrays.toString(out[index]));
        }

        Logger.println("Done...");

        return out;
    }

    public static Double[][] convertToInput(String[][] features) {
        Double[][] transformed = MatrixFunction.initialiseDoubleMatrix(features.length, POSDataset.getVocabSize());

        for (int i = 0; i < features.length; i++) {
            String[] sampleX = features[i];

            for (String s : sampleX) {
                if (POSDataset.getVocab().containsKey(s)) {
                    transformed[i][POSDataset.getVocab().get(s)] = 1.0;
                }
            }
        }

        return transformed;
    }

}
