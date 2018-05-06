package chatbot.engine.nn.core;

import chatbot.engine.nn.optimizer.Optimizer;
import chatbot.inputs.dataset.Dataset;
import chatbot.utils.Logger;
import chatbot.utils.json.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public abstract class NeuralNetwork {

    protected TrainParam param;
    protected int inputSize;
    protected int classSize;
    protected int globalStep = 1;

    protected HashMap<String, Double[][]> memory;
    protected HashMap<String, String> weights;
    protected Random random;
    protected Optimizer optimizer;

    protected Double[][] getFromMap(String name) {
        return memory.get(name);
    }

    protected Double[][] getWeight(String name, int inSize, int outSize) {
        Double[][] weight = memory.get(name);

        if (weight == null) {
            weight = WeightInitialization.initWeight(random, inSize, outSize);
        }

        memory.put(name, weight);

        return weight;
    }

    public Double[][] getWeight(String name) {
        return getFromMap(name);
    }

    public void setWeight(String name, Double[][] weight) {
        Logger.println("Set weight for -> " + name + " with shape (" + weight.length + "," + weight[0].length + ")");
        memory.put(name, weight);
    }

    protected abstract void forward(Double[][] input, Double[][] labels);

    protected abstract void backward();

    public abstract Double[][] predict(Double[][] input);


    public abstract void save(String filePath) throws IOException;

    public abstract void load(String filePath) throws IOException, ClassNotFoundException, ParseException;

    public NeuralNetwork() {
        memory = new HashMap<>();
        weights = new HashMap<>();
        random = new Random(1234); // for reproducibility
        param = new TrainParam();
    }

    public NeuralNetwork(int inputSize, int classSize) {
        this();
        this.inputSize = inputSize;
        this.classSize = classSize;
    }

    public NeuralNetwork(TrainParam param, int inputSize, int classSize) {
        this(inputSize, classSize);
        this.param = param;
    }

    private void update() {
        optimizer.update(weights, memory, globalStep);
    }

    private void details(int step, int totalSteps) {
        System.out.printf("\r%s/%s - Loss: %s", step, totalSteps, memory.get("cost")[0][0]);
    }

    private void validate(Dataset valDataset) {
        // no need to validate if no dataset available
        if (valDataset == null)
            return;

        int totalSteps = valDataset.size() / param.batchSize;
        double cost = 0;

        for (int step = 0; step < totalSteps; step++) {
            int start = step * param.batchSize;
            int end = start + param.batchSize;

            Double[][] inputs = valDataset.getX(start, end);
            Double[][] labels = valDataset.getY(start, end);

            forward(inputs, labels);

            cost += memory.get("cost")[0][0];
        }

        cost /= totalSteps;

        System.out.printf("\nvalidation loss -> %s\n", cost);
    }

    public void fit(Dataset trainDataset, int epoch) {
        fit(trainDataset, null, epoch);
    }

    public void fit(Dataset trainDataset, Dataset valDataset, int epoch) {
        assert epoch > 0;

        for (int e = 0; e < epoch; e++) {
            int totalSteps = trainDataset.size() / param.batchSize;
            trainDataset.shuffle();

            for (int step = 0; step < totalSteps; step++) {
                int start = step * param.batchSize;
                int end = start + param.batchSize;

                Double[][] inputs = trainDataset.getX(start, end);
                Double[][] labels = trainDataset.getY(start, end);
                forward(inputs, labels);
                backward();
                update();
                details(step + totalSteps * e, totalSteps * epoch);
                validate(valDataset);

                globalStep++;
            }
        }
    }


}
