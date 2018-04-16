package chatbot;

import chatbot.utils.ArrayUtils;
import chatbot.engine.math.MatrixFunction;

import java.util.stream.IntStream;

public class Testing {
    private static void testSigmoid() {
        System.out.println("Testing sigmoid");
        Double[][] a = {{0., 1., 2., 3., 4.}};
        Double[][] b = {{0.5, 0.73105858, 0.88079708, 0.95257413, 0.98201379}};
        Double[][] c = MatrixFunction.sigmoid(a);
        IntStream.range(0, c.length)
                .forEach(i -> IntStream.range(0, c[0].length)
                        .forEach(j -> System.out.println(b[i][j] - c[i][j])));
    }

    private static void testSigmoidGrad() {
        System.out.println("Testing sigmoid grad");
        Double[][] a = {{0.5, 0.73105858, 0.88079708, 0.95257413, 0.98201379}};
        Double[][] b = {{0.25, 0.19661193, 0.10499359, 0.04517666, 0.01766271}};
        Double[][] c = MatrixFunction.dsigmoid(a);
        IntStream.range(0, c.length)
                .forEach(i -> IntStream.range(0, c[0].length)
                        .forEach(j -> System.out.println(b[i][j] - c[i][j])));
    }

    private static void testSoftmax() {
        System.out.println("Testing softmax");
        Double[][] a = {{0., 1., 2., 3., 4.}};
        Double[][] b = {{0.01165623, 0.03168492, 0.08612854, 0.23412166, 0.63640865}};
        Double[][] c = MatrixFunction.softmax(a);
        IntStream.range(0, c.length)
                .forEach(i -> IntStream.range(0, c[0].length)
                        .forEach(j -> System.out.println(b[i][j] - c[i][j])));
    }

    public static void main(String[] args) {
//        Double[][] s = softmax(new Double[][]{{1.0, 0.0}, {-2.0, 3.0}});
//        ArrayUtils.printArray(s);
//        testSigmoid();
//        testSigmoidGrad();
//        testSoftmax();

            Double[][] a = new Double[2][2];
            ArrayUtils.printArray(a);
//        Double[][] m = MatrixFunction.negative(MatrixFunction.multiply(new Double[][]{{1.0, 0.0}, {1.0, 0.0}}, MatrixFunction.log(s)));
//        System.out.println("---");
//        ArrayUtils.printArray(m);
    }
}
