package chatbot.engine.math;

import java.util.HashMap;

public class FindingPowRootExp {

    private HashMap<DualKey, Double> memoizePow = new HashMap<>();
    private FindingFactorial ff = new FindingFactorial();
    private double precision = 1e-7;
    private double expTimes = 20;

    public double pow(double x, int n) {
        if (n < 0)
            throw new AssertionError("n must be >= 0");

        DualKey<Double, Integer> dualKey = new DualKey<>(x, n);

        if (n == 0)
            return 1;
        else if (memoizePow.containsKey(dualKey))
            return memoizePow.get(dualKey);

        double ans;

        if (n % 2 == 0)
            ans = pow(x, n / 2) * pow(x, n / 2);
        else
            ans = x * pow(x, n - 1);

        memoizePow.put(dualKey, ans);
        return ans;
    }


    public double lnx(double x) {
        // using simpsons rule
        int subintervals = 1000;
        double a = 1;
        double b = x;
        double h = (b - a) / subintervals;
        double s = oneOverX(b) + oneOverX(a);

        for (int i = 1; i < subintervals; i += 2) {
            s += 4 * oneOverX(a + i * h);
        }
        for (int i = 2; i < subintervals; i += 2) {
            s += 2 * oneOverX(a + i * h);
        }

        return s * h / 3;
    }

    /**
     * a^b = exp(b ln(a)) = e ^ (b ln a)
     */
    public double pow(double a, double b) {
        DualKey<Double, Double> dualKey = new DualKey<>(a, b);
        double lna = lnx(a);
        double blna = b * lna;
        double ans = exp(blna);
        memoizePow.put(dualKey, ans);
        return ans;
    }

    public double oneOverX(double x) {
        return 1 / x;
    }

    public double exp(double x) {
        double ret = 0;
        for (int i = 0; i < expTimes; i++) {
            ret += pow(x, i) / ff.factorial(i);
        }
        return ret;
    }

    public boolean checkGuess(double guess, double x, int n) {
        double d = Math.abs(pow(guess, n) - x); // difference between guess^n and x
        return d < precision;
    }

    private double findInitGuess(double x) {
        if (x >= 1.0)
            return 1.0;

        double scale = 1.0;
        while (scale > x) {
            x *= 10;
            scale /= 10;
        }
        return scale;
    }

    private double findDerivative(double x, int n) {
        return n * pow(x, n - 1);
    }

    private double newtonFindRt(double guess, double x, int n) {
        return guess - (pow(guess, n) - x) / findDerivative(guess, n);
    }

    public double newtonFindRt(double x, int n) {
        double guess = findInitGuess(x);
        while (!checkGuess(guess, x, n)) {
            guess = newtonFindRt(guess, x, n);
        }
        return guess;
    }
}
