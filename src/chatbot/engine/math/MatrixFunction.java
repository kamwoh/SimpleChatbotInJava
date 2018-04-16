package chatbot.engine.math;

import chatbot.utils.ArrayUtils;

public class MatrixFunction {

    public static Double[][] norm(Double[][] a) {
        Double[] sum = new Double[a.length];
        Double[] length = new Double[a.length];
        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            sum[i] = 0.;
            for (int j = 0; j < a[0].length; j++) {
                sum[i] += MathFunction.square(a[i][j]);
            }
        }


        for (int i = 0; i < a.length; i++) {
            length[i] = MathFunction.sqrt(sum[i]);
        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] / length[i];
            }
        }

        return out;
    }

    public static Double[][] transpose(Double[] b) {
        return transpose(ArrayUtils.makeMatrix(b));
    }

    public static Double[][] transpose(Double[][] a) {
        Double[][] out = new Double[a[0].length][a.length];

        for (int r = 0; r < a.length; r++) {
            for (int c = 0; c < a[0].length; c++) {
                out[c][r] = a[r][c];
            }
        }

        return out;
    }

    public static Double[][] dot(Double[] a, Double[][] b) {
        return dot(transpose(a), b);
    }

    public static Double[][] dot(Double[][] a, Double[] b) {
        return dot(a, transpose(b));
    }

    public static Double[][] dot(Double[][] a, Double[][] b) {
        assert a[0].length == b.length;

        int newRow = a.length;
        int n1 = a[0].length;
        int newCol = b[0].length;

        Double[][] c = new Double[newRow][newCol];

        for (int i = 0; i < newRow; i++) {
            for (int j = 0; j < newCol; j++) {
                c[i][j] = 0.;
                for (int k = 0; k < n1; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return c;
    }

    public static Double[][] multiply(Double[][] a, Double[][] b) {
        assert a.length == b.length && a[0].length == b[0].length;

        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] * b[i][j];
            }
        }

        return out;
    }

    public static Double[][] multiply(Double[][] a, Double b) {
        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] * b;
            }
        }

        return out;
    }

    public static Double[][] sum(Double[][] a) {
        Double[][] out = new Double[a.length][1];

        for (int i = 0; i < a.length; i++) {
            out[i][0] = 0.;
            for (int j = 0; j < a[0].length; j++) {
                out[i][0] += a[i][j];
            }
        }

        return out;
    }

    public static double sumAll(Double[][] a) {
        double out = 0;

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out += a[i][j];
            }
        }

        return out;
    }


    public static Double sum(Double[] a) {
        double out = 0;

        for (int i = 0; i < a.length; i++) {
            out += a[i];
        }

        return out;
    }


    public static Double[][] log(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = MathFunction.log(a[i][j]);
            }
        }

        return out;
    }

    public static Double[][] negative(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = -a[i][j];
            }
        }

        return out;
    }

    public static Double[][] subtract(Double[][] a, Double[][] b) {
        if (!(a.length == b.length && a[0].length == b[0].length)) {
            if (b[0].length != 1)
                throw new RuntimeException("Invalid size");
        }

        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (b[0].length != 1)
                    out[i][j] = a[i][j] - b[i][j];
                else
                    out[i][j] = a[i][j] - b[j][0];
            }
        }

        return out;
    }

    public static Double[] subtract(Double[] a, Double[] b) {
        assert a.length == b.length;

        Double[] out = new Double[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] - b[i];
        }

        return out;
    }

    public static Double[] subtract(Double[] a, double b) {
        Double[] out = new Double[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] - b;
        }

        return out;
    }

    public static Double[][] subtract(Double[][] a, double b) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] - b;
            }
        }
        return out;
    }

    public static Double[][] subtract(double b, Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = b - a[i][j];
            }
        }
        return out;
    }

    public static Double[][] add(Double[][] a, Double[][] b) {
        if (!(a.length == b.length && a[0].length == b[0].length)) {
            if (b[0].length != 1)
                throw new RuntimeException("Invalid size");
        }

        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (b[0].length != 1)
                    out[i][j] = a[i][j] + b[i][j];
                else
                    out[i][j] = a[i][j] + b[j][0];
            }
        }

        return out;
    }

    public static Double[][] add(Double[][] a, double b) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] + b;
            }
        }

        return out;
    }

    public static Double[][] divide(Double[][] a, Double[][] b) {
        assert a.length == b.length && a[0].length == b[0].length;

        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] / b[i][j];
            }
        }

        return out;
    }

    public static Double[][] divide(Double[][] a, Double b) {
        Double[][] out = new Double[a.length][a[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                out[i][j] = a[i][j] / b;
            }
        }

        return out;
    }

    public static Double[] divide(Double[] a, Double b) {
        Double[] out = new Double[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] / b;
        }

        return out;
    }

    public static Double max(Double[] vector) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < vector.length; i++) {
            if (max < vector[i])
                max = vector[i];
        }
        return max;
    }

    public static Double max(Double[][] a) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            double vmax = max(a[i]);
            if (max < vmax)
                max = vmax;
        }
        return max;
    }

    public static Double min(Double[] vector) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < vector.length; i++) {
            if (min > vector[i])
                min = vector[i];
        }
        return min;
    }

    public static int[] argmax(Double[][] a) {
        int[] indices = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            indices[i] = argmax(a[i]);
        }
        return indices;
    }

    public static int argmax(Double[] a) {
        double max = Double.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < a.length; i++) {
            if (max < a[i]) {
                max = a[i];
                index = i;
            }
        }
        return index;
    }

    public static double mean(Double[][] a) {
        double t = 0;
        int count = a.length * a[0].length;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                t += a[i][j];
            }
        }
        return t / count;
    }

    public static double exp(double a) {
        return MathFunction.exp(a);
    }

    public static Double[] exp(Double[] a) {
        Double[] out = new Double[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = MathFunction.exp(a[i]);
        }
        return out;
    }

    public static Double[][] exp(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            out[i] = exp(a[i]);
        }
        return out;
    }

    public static Double[][] maximum(Double[][] a, double maxValue) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = Math.max(a[i][j], maxValue);
            }
        }
        return out;
    }

    public static Double[][] sigmoid(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = 1. / (1. + exp(-a[i][j]));
            }
        }
        return out;
    }

    public static Double[][] dsigmoid(Double[][] a) {
        return multiply(a, subtract(1., a));
    }

    public static Double[][] softmax(Double[][] x) {
        int batchSize = x.length;
        int units = x[0].length;
        Double[][] softmaxMatrix = new Double[batchSize][units];

        for (int i = 0; i < batchSize; i++) {
            Double[] exps = subtract(x[i], max(x[i]));
            exps = exp(exps);
            softmaxMatrix[i] = divide(exps, sum(exps));
        }

        return softmaxMatrix;
    }

    public static Double[][] relu(Double[][] a) {
        return maximum(a, 0);
    }

    public static Double[][] drelu(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                if (a[i][j] > 0)
                    out[i][j] = 1.;
                else
                    out[i][j] = 0.;
            }
        }
        return out;
    }

    public static Double[][] sqrt(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = MathFunction.sqrt(a[i][j]);
            }
        }
        return out;
    }

    public static Double[][] square(Double[][] a) {
        Double[][] out = new Double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = a[i][j] * a[i][j];
            }
        }
        return out;
    }



    public static Double[][] initialiseDoubleMatrix(int axis1, int axis2) {
        Double[][] out = new Double[axis1][axis2];

        for (int i = 0; i < axis1; i++) {
            for (int j = 0; j < axis2; j++) {
                out[i][j] = 0.0;
            }
        }

        return out;
    }

}
