package chatbot.engine.math;

public class MathFunction {
    private static FindingPowRootExp fr = new FindingPowRootExp();

    public static double sqrt(double x) {
        return Math.sqrt(x);
//        return fr.newtonFindRt(x, 2);
    }

    public static double pow(double x, int n) {
        return Math.pow(x, n);
//        return fr.pow(x, n);
    }

    public static double pow(double x, double n) {
        return Math.pow(x, n);
//        return fr.pow(x, n);
    }

    public static double exp(double n) {
//        return fr.exp(n);
        return Math.exp(n);
    }

    public static double square(double x) {
        return x * x;
    }

    public static double log(double x) {
        return Math.log(x);
//        if (x == 0)
//            return 0;
//        return fr.lnx(x);
    }

    public static double log10(double x) {
        return Math.log10(x);
    }

    public static boolean isNumber(String s) {
        try {
            double d = Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBoolean(String s) {
        try {
            boolean d = Boolean.parseBoolean(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
