package chatbot.engine.nn.core;

public class TrainParam {

    public int batchSize;
    public int hiddenUnits;
    public double learningRate;

    public TrainParam() {

    }

    public TrainParam(int batchSize, int hiddenUnits, double learningRate) {
        this.batchSize = batchSize;
        this.hiddenUnits = hiddenUnits;
        this.learningRate = learningRate;
    }

}
