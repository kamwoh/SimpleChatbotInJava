package chatbot;

import chatbot.engine.nn.core.TrainParam;
import chatbot.engine.nn.model.POSNeuralNetwork;
import chatbot.inputs.POSInput;
import chatbot.inputs.dataset.POSDataset;

public class TrainingMain {


    public static void posMain(String[] args) throws Exception {
        POSDataset trainDataset = POSInput.transformToDataset(POSInput.loadDataset("data/posTrain.csv"));
        POSNeuralNetwork nn = new POSNeuralNetwork(
                new TrainParam(1, 256, 0.0001),
                trainDataset.getInputSize(),
                trainDataset.getClassSize());
        nn.fit(trainDataset, 2);
        nn.save("posWeights1.data");
    }


    public static void main(String[] args) throws Exception {
        posMain(args);
    }
}
