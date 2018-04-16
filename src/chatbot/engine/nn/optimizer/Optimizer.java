package chatbot.engine.nn.optimizer;

import java.util.HashMap;

public abstract class Optimizer {

    double lr;

    public Optimizer(double lr) {
        this.lr = lr;
    }

    public abstract void update(HashMap<String, String> keysToUpdate, HashMap<String, Double[][]> weights, int t);

}
