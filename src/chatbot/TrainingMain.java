package chatbot;

import chatbot.utils.ArrayUtils;
import chatbot.engine.math.MatrixFunction;
import chatbot.engine.nn.core.NeuralNetwork;
import chatbot.engine.nn.core.TrainParam;
import chatbot.engine.nn.model.POSNeuralNetwork;
import chatbot.inputs.dataset.TextClassificationDataset;
import chatbot.engine.nn.model.TextClassificationNeuralNetwork;
import chatbot.inputs.Input;
import chatbot.inputs.dataset.POSDataset;
import chatbot.inputs.POSInput;

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


    public static void textMain(String[] args) throws Exception {
        TextClassificationDataset dataset = new TextClassificationDataset("data/trainingset.json");

        NeuralNetwork nn = new TextClassificationNeuralNetwork(
                new TrainParam(8, 8, 0.1),
                dataset.getInputSize(),
                dataset.getClassSize()
        );

        nn.fit(dataset, 1000);

        Double[][] inputs = ArrayUtils.makeMatrix(Input.createInput("how are you", dataset.getWords()));

        Double[] pred = nn.predict(inputs)[0];

        System.out.println();
        int index = MatrixFunction.argmax(pred);
        ArrayUtils.printArray(pred);
        System.out.println("---> " + index + " " + dataset.getClasses().get(index) + " " + MatrixFunction.max(pred));

        nn.save("data/textweights/weights2.data");
    }

    public static void main(String[] args) throws Exception {
        textMain(args);
    }
}
