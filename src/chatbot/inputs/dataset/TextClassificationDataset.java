package chatbot.inputs.dataset;

import chatbot.utils.ArrayUtils;
import chatbot.engine.Engine;
import chatbot.utils.json.JSONArray;
import chatbot.utils.json.JSONObject;
import chatbot.utils.json.JSONUtils;
import chatbot.utils.json.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class TextClassificationDataset implements Dataset {

    private Double[][] X, y;
    private ArrayList<String> words;
    private ArrayList<String> classes;
    private ArrayList documents;

    public TextClassificationDataset(String filePath) throws IOException, ParseException {
        words = new ArrayList<>();
        classes = new ArrayList<>();
        documents = new ArrayList();

        load(filePath);
        stemming();
        prepareData();
    }

    private void load(String filePath) throws IOException, ParseException {
        JSONArray intents = (JSONArray) ((JSONObject) JSONUtils.parseJsonFile(filePath)).get("intents");
        for (int i = 0; i < intents.size(); i++) {
            JSONObject intent = (JSONObject) intents.get(i);
            JSONArray patterns = (JSONArray) intent.get("patterns");
            String tag = intent.get("tag").toString();

            for (int j = 0; j < patterns.size(); j++) {
                String pattern = (String) patterns.get(j);
                String[] w = Engine.tokenize(pattern);

                words.addAll(Arrays.asList(w));

                if (!classes.contains(tag))
                    classes.add(tag);


                documents.add(new Object[]{w, tag});
            }
        }
    }

    private void stemming() {
        ArrayList<String> stemmedWords = new ArrayList<>();

        for (String word : words) {
            String stemmed = Engine.stem(word.toLowerCase());

            if (!stemmedWords.contains(stemmed))
                stemmedWords.add(stemmed);
        }

        stemmedWords.sort(String::compareTo);
        words = stemmedWords;
    }

    private void prepareData() {
        X = new Double[documents.size()][words.size()];
        y = new Double[documents.size()][classes.size()];

        for (int i = 0; i < documents.size(); i++) {
            Object[] o = (Object[]) documents.get(i);
            String[] patternWords = (String[]) o[0];
            String tag = (String) o[1];
            Double[] outputRow = new Double[classes.size()];
            Double[] bag = new Double[words.size()];

            // stem words
            for (int j = 0; j < patternWords.length; j++) {
                patternWords[j] = Engine.stem(patternWords[j].toLowerCase());
            }

            // set input vector
            List<String> patternWordsList = Arrays.asList(patternWords);

            for (int j = 0; j < words.size(); j++) {
                if (patternWordsList.contains(words.get(j)))
                    bag[j] = 1.;
                else
                    bag[j] = 0.;
            }

            // set labels
            for (int j = 0; j < outputRow.length; j++) {
                outputRow[j] = 0.;
            }

            int tagClass = classes.indexOf(tag);
            outputRow[tagClass] = 1.;

            X[i] = bag;
            y[i] = outputRow;
        }
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void shuffle() {
        ArrayUtils.shuffle(X, y);
    }

    public Double[][] getX(int start, int end) {
        return ArrayUtils.getMatrixByIndex(X, start, end);
    }

    public Double[][] getY(int start, int end) {
        return ArrayUtils.getMatrixByIndex(y, start, end);
    }

    public int size() {
        return X.length;
    }

    public int getInputSize() {
        return X[0].length;
    }

    public int getClassSize() {
        return y[0].length;
    }
}
