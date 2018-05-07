package chatbot;

import chatbot.engine.Engine;
import chatbot.engine.math.MatrixFunction;
import chatbot.engine.nn.model.POSNeuralNetwork;
import chatbot.inputs.POSInput;
import chatbot.inputs.dataset.POSDataset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TestingMain {
    public static POSNeuralNetwork loadPosWeight() {
        POSNeuralNetwork nn = new POSNeuralNetwork(POSDataset.getVocabSize(), POSDataset.getClassesSize());

        try {
            ArrayList<String> fileList = new ArrayList<>();

            fileList.add("data/weights32/weight32_fc1_kernel.csv");
            fileList.add("data/weights32/weight32_fc1_bias.csv");
            fileList.add("data/weights32/weight32_fc2_kernel.csv");
            fileList.add("data/weights32/weight32_fc2_bias.csv");
            fileList.add("data/weights32/weight32_dense_1_kernel.csv");
            fileList.add("data/weights32/weight32_dense_1_bias.csv");

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
                System.out.println("loaded -> " + weightName);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return nn;
    }

    public static void posMain() throws Exception {
        POSNeuralNetwork nn = loadPosWeight();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Input a line");
            String s = scanner.nextLine();

            if (s.trim().equalsIgnoreCase("quit"))
                break;

            Double[][] inputs = POSInput.convertToInput(POSInput.convertToFeatures(Engine.tokenize(s)));
            Double[][] pred = nn.predict(inputs);
            int[] index = MatrixFunction.argmax(pred);

            System.out.println("--------------------------------------------------------");
//            ArrayUtils.printArray(pred);

            int count = 0;
            for (int i : index) {
                System.out.println("---> " + i + " " + POSDataset.decodeClass(i) + " " + pred[count][i]);
                count++;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        posMain();
    }
}