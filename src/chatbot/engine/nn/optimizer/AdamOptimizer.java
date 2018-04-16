package chatbot.engine.nn.optimizer;

import chatbot.engine.math.MathFunction;
import chatbot.engine.math.MatrixFunction;

import java.util.HashMap;
import java.util.Map;

public class AdamOptimizer extends Optimizer {

    public double beta1 = 0.9;
    public double beta2 = 0.999;
    public double epsilon = 1e-8;

    private HashMap<String, Double[][]> mT;
    private HashMap<String, Double[][]> vT;

    public AdamOptimizer(double lr) {
        super(lr);

        mT = new HashMap<>();
        vT = new HashMap<>();
    }

    @Override
    public void update(HashMap<String, String> keysToUpdate, HashMap<String, Double[][]> weights, int t) {
        for (Map.Entry<String, String> pair : keysToUpdate.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue();

            Double[][] weight = weights.get(key);
            Double[][] grad = weights.get(value);

            Double[][] m = mT.get(key);
            Double[][] v = vT.get(key);

            if (m == null)
                m = MatrixFunction.initialiseDoubleMatrix(grad.length, grad[0].length); // moving average of gradient
            if (v == null)
                v = MatrixFunction.initialiseDoubleMatrix(grad.length, grad[0].length); // square gradient

            m = MatrixFunction.add(MatrixFunction.multiply(m, beta1), MatrixFunction.multiply(grad, 1.0 - beta1));
            v = MatrixFunction.add(MatrixFunction.multiply(v, beta2), MatrixFunction.multiply(MatrixFunction.square(grad), 1.0 - beta2));
            Double[][] mCap = MatrixFunction.divide(m, 1.0 - (MathFunction.pow(beta1, t)));
            Double[][] vCap = MatrixFunction.divide(v, 1.0 - (MathFunction.pow(beta2, t)));

            Double[][] operand = MatrixFunction.multiply(mCap, lr);
            Double[][] divider = MatrixFunction.add(MatrixFunction.sqrt(vCap), epsilon);
            Double[][] result = MatrixFunction.divide(operand, divider);

            weight = MatrixFunction.subtract(weight, result);

            mT.put(key, m);
            vT.put(key, v);
            weights.put(key, weight);
        }
    }
}
