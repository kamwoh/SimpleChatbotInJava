package chatbot.inputs.dataset;

import chatbot.utils.ArrayUtils;
import chatbot.engine.math.MatrixFunction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class POSDataset implements Dataset {

    private static HashMap<String, Integer> vocab;
    private static HashMap<String, Integer> classes;
    private static ArrayList<String> classesList;
    private static boolean loaded = false;
    private static int version = 1; // default load version 1
    private static int loadedVersion = 1;

    private static void load(String vocabFile, String classesFile) {
        if (loaded && version == loadedVersion)
            return;

        vocab = new HashMap<>();
        classes = new HashMap<>();
        classesList = new ArrayList<>();

        try {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(vocabFile)));

            String s;
            while ((s = f.readLine()) != null) {
                String[] splits = s.split("\t");

                String key = splits[0];
                String value = splits[1];

                vocab.put(key, Integer.parseInt(value));
            }

            int i = 0;
            f = new BufferedReader(new InputStreamReader(new FileInputStream(classesFile)));
            while ((s = f.readLine()) != null) {
                classes.put(s, i);
                classesList.add(s);
                i++;
            }

            loaded = true;
            loadedVersion = version;
            System.out.println("Vocab size: " + getVocabSize());
            System.out.println("Classes size: " + getClassesSize());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void loadV1() {
        load("data/inputVocab.csv", "data/posClasses.csv");
    }

    private static void loadV2() {
        load("data/inputVocab_v2.csv", "data/posClasses_v2.csv");
    }

    public static void useV1() {
        version = 1;
        loadV1();
    }

    public static void useV2() {
        version = 2;
        loadV2();
    }

    public static int getVocabSize() {
        return vocab.size();
    }

    public static int getClassesSize() {
        return classes.size();
    }

    public static String decodeClass(int classIndex) {
        return classesList.get(classIndex);
    }

    public static HashMap<String, Integer> getVocab() {
        return vocab;
    }

    public static int getVersion() {
        return version;
    }

    private ArrayList<String> y;
    private ArrayList<String[]> X;

    public POSDataset(ArrayList<String[]> X, ArrayList<String> y) {
        this.X = X;
        this.y = y;
    }

    public void shuffle() {
        ArrayUtils.shuffle(X, y);
    }

    public Double[][] getX(int start, int end) {
        Double[][] transformed = MatrixFunction.initialiseDoubleMatrix(end - start, getVocabSize());

        for (int i = start; i < end; i++) {
            String[] sampleX = X.get(i);

            for (String s : sampleX) {
                if (vocab.containsKey(s)) {
                    transformed[i - start][vocab.get(s)] = 1.0;
                }
            }
        }

        return transformed;
    }

    public Double[][] getY(int start, int end) {
        Double[][] transformed = MatrixFunction.initialiseDoubleMatrix(end - start, getClassesSize());

        for (int i = start; i < end; i++) {
            String sampleY = y.get(i);
            int j = classes.get(sampleY);

            transformed[i - start][j] = 1.0;
        }

        return transformed;
    }

    public int size() {
        return this.X.size();
    }

    public int getClassSize() {
        return getClassesSize();
    }

    public int getInputSize() {
        return getVocabSize();
    }

}
