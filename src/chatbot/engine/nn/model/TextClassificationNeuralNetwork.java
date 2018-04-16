package chatbot.engine.nn.model;

import chatbot.utils.ArrayUtils;
import chatbot.engine.math.MatrixFunction;
import chatbot.engine.nn.core.TrainParam;
import chatbot.engine.nn.core.NeuralNetwork;
import chatbot.engine.nn.optimizer.AdamOptimizer;
import chatbot.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

public class TextClassificationNeuralNetwork extends NeuralNetwork {

    public TextClassificationNeuralNetwork() {
        super();
    }

    public TextClassificationNeuralNetwork(int inputSize, int classSize) {
        super(inputSize, classSize);
    }

    public TextClassificationNeuralNetwork(TrainParam param, int inputSize, int classSize) {
        super(param, inputSize, classSize);

        optimizer = new AdamOptimizer(param.learningRate);

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
        Double[][] a1 = MatrixFunction.sigmoid(fc1);
        Double[][] fc2 = MatrixFunction.add(MatrixFunction.dot(a1, weight2), bias2);
        Double[][] a2 = MatrixFunction.sigmoid(fc2);
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
        Double[][] gradFc2 = MatrixFunction.multiply(MatrixFunction.dsigmoid(a2), gradA2);
        Double[][] gradWeight2 = MatrixFunction.dot(MatrixFunction.transpose(a1), gradFc2);
        Double[][] gradBias2 = MatrixFunction.sum(gradFc2);

        Double[][] gradA1 = MatrixFunction.dot(gradFc2, MatrixFunction.transpose(weight2));
        Double[][] gradFc1 = MatrixFunction.multiply(MatrixFunction.dsigmoid(a1), gradA1);
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
        Double[][] a1 = MatrixFunction.sigmoid(fc1);
        Double[][] fc2 = MatrixFunction.add(MatrixFunction.dot(a1, weight2), bias2);
        Double[][] a2 = MatrixFunction.sigmoid(fc2);
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

        Utils.serializeHashMap(map, filePath);
        System.out.println("Saved weights in " + filePath);
    }

    public void load(String filePath) throws IOException, ClassNotFoundException {
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

        memory.put("weight1", weight1);
        memory.put("weight2", weight2);
        memory.put("weight3", weight3);
        memory.put("bias1", bias1);
        memory.put("bias2", bias2);
        memory.put("bias3", bias3);

        System.out.println("Loaded weights");
    }
}
