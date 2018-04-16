package chatbot.engine.nn.optimizer;

import chatbot.engine.math.MatrixFunction;

import java.util.HashMap;
import java.util.Map;

public class GradientDescentOptimizer extends Optimizer {

    public GradientDescentOptimizer(double lr) {
        super(lr);
    }

    public void update(HashMap<String, String> keysToUpdate, HashMap<String, Double[][]> weights, int t) {
        for (Map.Entry<String, String> pair : keysToUpdate.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue();

            Double[][] weight = weights.get(key);
            Double[][] grad = weights.get(value);

            weight = MatrixFunction.add(weight, MatrixFunction.multiply(grad, -lr));

            weights.put(key, weight);
        }
    }

}
