package chatbot.engine.math;

import java.util.HashMap;

public class FindingFactorial {

    private HashMap<Integer, Long> memoize = new HashMap<>();

    public long factorial(int n) {
        if (memoize.containsKey(n))
            return memoize.get(n);

        if (n <= 1)
            return 1;

        long ans = n * factorial(n - 1);
        memoize.put(n, ans);

        return ans;
    }

}
