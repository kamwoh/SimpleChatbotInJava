package chatbot.engine.nn.model;

import chatbot.engine.math.MatrixFunction;
import chatbot.engine.nlp.POS;
import chatbot.engine.nn.core.NeuralNetwork;
import chatbot.engine.nn.core.TrainParam;
import chatbot.engine.nn.optimizer.GradientDescentOptimizer;
import chatbot.inputs.POSInput;
import chatbot.inputs.dataset.POSDataset;
import chatbot.utils.ArrayUtils;
import chatbot.utils.Logger;
import chatbot.utils.Utils;
import chatbot.utils.json.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class POSNeuralNetwork extends NeuralNetwork {

    public POSNeuralNetwork(int inputSize, int classSize) {
        super(inputSize, classSize);
    }

    public POSNeuralNetwork(TrainParam param, int inputSize, int classSize) {
        super(param, inputSize, classSize);

        optimizer = new GradientDescentOptimizer(param.learningRate);

        weights.put("weight1", "gradWeight1");
        weights.put("weight2", "gradWeight2");
        weights.put("weight3", "gradWeight3");
        weights.put("bias1", "gradBias1");
        weights.put("bias2", "gradBias2");
        weights.put("bias3", "gradBias3");
    }

    protected void forward(Double[][] inputs, Double[][] labels) {
        Double[][] weight1 = getWeight("weight1", inputSize, param.hiddenUnits);
        Double[][] bias1 = getWeight("bias1", param.hiddenUnits, 1);
        Double[][] weight2 = getWeight("weight2", param.hiddenUnits, param.hiddenUnits);
        Double[][] bias2 = getWeight("bias2", param.hiddenUnits, 1);
        Double[][] weight3 = getWeight("weight3", param.hiddenUnits, classSize);
        Double[][] bias3 = getWeight("bias3", classSize, 1);

        Double[][] fc1 = MatrixFunction.add(MatrixFunction.dot(inputs, weight1), bias1); // (batchSize, inputSize) * (inputSize, hiddenUnits)
        Double[][] a1 = MatrixFunction.relu(fc1);
        Double[][] fc2 = MatrixFunction.add(MatrixFunction.dot(a1, weight2), bias2);
        Double[][] a2 = MatrixFunction.relu(fc2);
        Double[][] fc3 = MatrixFunction.add(MatrixFunction.dot(a2, weight3), bias3);
        Double[][] softmax4 = MatrixFunction.softmax(fc3); // (batchSize, classSize)

        // labels -> (batchSize, classSize)
        Double[][] cost = MatrixFunction.negative(MatrixFunction.multiply(labels, MatrixFunction.log(softmax4)));
        Double[][] c = ArrayUtils.makeMatrix(MatrixFunction.sumAll(cost));
        memory.put("inputs", inputs);
        memory.put("labels", labels);
        memory.put("fc1", fc1);
        memory.put("a1", a1);
        memory.put("fc2", fc2);
        memory.put("a2", a2);
        memory.put("fc3", fc3);
        memory.put("softmax4", softmax4);
        memory.put("cost", c);
    }

    protected void backward() {
        Double[][] inputs = getFromMap("inputs");
        Double[][] labels = getFromMap("labels");
        Double[][] a1 = getFromMap("a1");
        Double[][] a2 = getFromMap("a2");
        Double[][] softmax4 = getFromMap("softmax4");
        Double[][] weight2 = getFromMap("weight2");
        Double[][] weight3 = getFromMap("weight3");

        Double[][] gradFc3 = MatrixFunction.subtract(softmax4, labels);
        Double[][] gradWeight3 = MatrixFunction.dot(MatrixFunction.transpose(a2), gradFc3);
        Double[][] gradBias3 = MatrixFunction.sum(gradFc3);

        Double[][] gradA2 = MatrixFunction.dot(gradFc3, MatrixFunction.transpose(weight3));
        Double[][] gradFc2 = MatrixFunction.multiply(MatrixFunction.drelu(a2), gradA2);
        Double[][] gradWeight2 = MatrixFunction.dot(MatrixFunction.transpose(a1), gradFc2);
        Double[][] gradBias2 = MatrixFunction.sum(gradFc2);

        Double[][] gradA1 = MatrixFunction.dot(gradFc2, MatrixFunction.transpose(weight2));
        Double[][] gradFc1 = MatrixFunction.multiply(MatrixFunction.drelu(a1), gradA1);
        Double[][] gradWeight1 = MatrixFunction.dot(MatrixFunction.transpose(inputs), gradFc1);
        Double[][] gradBias1 = MatrixFunction.sum(gradFc1);

        // check shape here
        memory.put("gradWeight3", gradWeight3);
        memory.put("gradBias3", gradBias3);
        memory.put("gradWeight2", gradWeight2);
        memory.put("gradBias2", gradBias2);
        memory.put("gradWeight1", gradWeight1);
        memory.put("gradBias1", gradBias1);
    }

    public Double[][] predict(Double[][] inputs) {
        Double[][] weight1 = getWeight("weight1", inputSize, param.hiddenUnits);
        Double[][] bias1 = getWeight("bias1", param.hiddenUnits, 1);
        Double[][] weight2 = getWeight("weight2", param.hiddenUnits, param.hiddenUnits);
        Double[][] bias2 = getWeight("bias2", param.hiddenUnits, 1);
        Double[][] weight3 = getWeight("weight3", param.hiddenUnits, classSize);
        Double[][] bias3 = getWeight("bias3", classSize, 1);


        Double[][] fc1 = MatrixFunction.add(MatrixFunction.dot(inputs, weight1), bias1); // (batchSize, inputSize) * (inputSize, hiddenUnits)
        Double[][] a1 = MatrixFunction.relu(fc1);
        Double[][] fc2 = MatrixFunction.add(MatrixFunction.dot(a1, weight2), bias2);
        Double[][] a2 = MatrixFunction.relu(fc2);
        Double[][] fc3 = MatrixFunction.add(MatrixFunction.dot(a2, weight3), bias3);
        Double[][] softmax4 = MatrixFunction.softmax(fc3); // (batchSize, classSize)

        return softmax4;
    }

    public void save(String filePath) throws IOException {
        HashMap<String, Object> map = new HashMap<>();

        Double[][] weight1 = getFromMap("weight1");
        Double[][] weight2 = getFromMap("weight2");
        Double[][] weight3 = getFromMap("weight3");

        Double[][] bias1 = getFromMap("bias1");
        Double[][] bias2 = getFromMap("bias2");
        Double[][] bias3 = getFromMap("bias3");

        map.put("weight1", weight1);
        map.put("weight2", weight2);
        map.put("weight3", weight3);
        map.put("bias1", bias1);
        map.put("bias2", bias2);
        map.put("bias3", bias3);
        map.put("globalStep", globalStep);
        map.put("inputSize", inputSize);
        map.put("classSize", classSize);
        map.put("hiddenUnits", param.hiddenUnits);

        Utils.serializeHashMap(map, filePath);
        System.out.println("Saved weights in " + filePath);
    }

    public void load(String filePath) throws IOException, ClassNotFoundException, ParseException {
        Logger.println("Loading weights...");
        HashMap map = Utils.deserializeHashMap(filePath);

        Double[][] weight1 = (Double[][]) map.get("weight1");
        Double[][] weight2 = (Double[][]) map.get("weight2");
        Double[][] weight3 = (Double[][]) map.get("weight3");

        Double[][] bias1 = (Double[][]) map.get("bias1");
        Double[][] bias2 = (Double[][]) map.get("bias2");
        Double[][] bias3 = (Double[][]) map.get("bias3");

        globalStep = (int) map.get("globalStep");
        inputSize = (int) map.get("inputSize");
        classSize = (int) map.get("classSize");
        param.hiddenUnits = (int) map.get("hiddenUnits");

        memory.put("weight1", weight1);
        memory.put("weight2", weight2);
        memory.put("weight3", weight3);
        memory.put("bias1", bias1);
        memory.put("bias2", bias2);
        memory.put("bias3", bias3);

        Logger.println("Loaded weights");
    }

    public POS[] predictPOS(String[] tokenized) {
        long startTime = System.currentTimeMillis();
        Logger.println("Predicting POS...");

        Double[][] inputs = POSInput.convertToInput(POSInput.convertToFeatures(tokenized));
        Double[][] pred = predict(inputs);
        int[] index = MatrixFunction.argmax(pred);
        POS[] pos = new POS[index.length];
        for (int i = 0; i < index.length; i++) {
            pos[i] = new POS(tokenized[i], POSDataset.decodeClass(index[i]), pred[i][index[i]]);
            Logger.println(pos[i].getTerm() + " -> " + pos[i].getTag() + String.format(" -> %.2f", pos[i].getProb() * 100.0));
        }

        Logger.printf("Predicting POS used %.2f seconds\n", (System.currentTimeMillis() - startTime) / 1000.0);
        return pos;
    }

    private static ArrayList<String> getFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("data/weights16_v3_half/weight16_fc1_kernel_v3_half.csv");
        fileList.add("data/weights16_v3_half/weight16_fc1_bias_v3_half.csv");
        fileList.add("data/weights16_v3_half/weight16_fc2_kernel_v3_half.csv");
        fileList.add("data/weights16_v3_half/weight16_fc2_bias_v3_half.csv");
        fileList.add("data/weights16_v3_half/weight16_dense_1_kernel_v3_half.csv");
        fileList.add("data/weights16_v3_half/weight16_dense_1_bias_v3_half.csv");

        return fileList;
    }

    public static POSNeuralNetwork loadPosWeight16(int v) {
        if (v == 3)
            return loadPosWeight(16, v);
        else
            throw new RuntimeException("16 hidden units only have version 3");
    }

    private static POSNeuralNetwork loadPosWeight(int weightHiddenUnits, int v) {
        POSDataset.useV3();

        Logger.println("===============POS Neural network settings===============");

        POSNeuralNetwork nn = new POSNeuralNetwork(POSDataset.getVocabSize(), POSDataset.getClassesSize());

        Logger.println("Version: " + v);
        Logger.println("Hidden units: " + weightHiddenUnits);
        Logger.println("Vocab Size: " + POSDataset.getVocabSize());
        Logger.println("Classes Size: " + POSDataset.getClassesSize());

        try {
            ArrayList<String> fileList = getFileList();

            ArrayList<String> weightList = new ArrayList<>();

            weightList.add("weight1");
            weightList.add("bias1");
            weightList.add("weight2");
            weightList.add("bias2");
            weightList.add("weight3");
            weightList.add("bias3");

            for (int j = 0; j < fileList.size(); j++) {
                ArrayList<Double[]> weight = new ArrayList<>();
                BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(fileList.get(j))));
                String s;

                while ((s = f.readLine()) != null) {
                    String[] splits = s.split(",");
                    Double[] w = new Double[splits.length];

                    for (int i = 0; i < splits.length; i++) {
                        w[i] = Double.parseDouble(splits[i]);
                    }

                    weight.add(w);
                }

                Double[][] doubleWeight = weight.toArray(new Double[weight.size()][]);

                String weightName = weightList.get(j);
                nn.setWeight(weightName, doubleWeight);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        Logger.println("===============POS Neural network settings===============");

        return nn;
    }
}
