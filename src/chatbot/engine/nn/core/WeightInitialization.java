package chatbot.engine.nn.core;

import chatbot.engine.math.MathFunction;

import java.util.Random;

public class WeightInitialization {

    public static Double[][] initWeight(Random random, int inSize, int outSize) {
        // xavier initialization
        // s = sqrt(6/(fanIn + fanOut))
        // out = r * s

        Double[][] weight = new Double[inSize][outSize];

        for (int i = 0; i < inSize; i++) {
            for (int j = 0; j < outSize; j++) {
                weight[i][j] = random.nextDouble() * MathFunction.sqrt(6. / (inSize + outSize));
            }
        }

        return weight;
    }

}
