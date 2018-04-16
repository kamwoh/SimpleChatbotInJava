package chatbot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class ArrayUtils {
    public static <T> void printArray(T[] os) {
        System.out.println(Arrays.toString(os));
    }

    public static <T> void printArray(T[][] os) {
        for (T[] o : os)
            printArray(o);
    }

    public static Double[][] makeMatrix(Double[] a) {
        return new Double[][]{a};
    }

    public static Double[][] makeMatrix(Double a) {
        return new Double[][]{{a}};
    }

    public static Double[][] getMatrixByIndex(Double[][] matrix, int start, int end) {
        Double[][] newMatrix = new Double[end - start][matrix[0].length];

        for (int i = start; i < end; i++) {
            newMatrix[i - start] = matrix[i];
        }

        return newMatrix;
    }

    public static <T, K> void shuffle(T[] a, K[] b) {
        assert a.length == b.length;
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < a.length; i++) {
            list.add(i);
        }

        Collections.shuffle(list, new Random(System.currentTimeMillis()));

        for (int i = 0; i < a.length; i++) {
            int randIndex = list.get(i);
            swap(a, i, randIndex);
            swap(b, i, randIndex);
        }
    }

    public static <T, K> void shuffle(ArrayList<T> a, ArrayList<K> b) {
        if (a.size() != b.size())
            throw new RuntimeException("a.size() != b.size()");

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {
            list.add(i);
        }

        Collections.shuffle(list, new Random(System.currentTimeMillis()));

        for (int i = 0; i < a.size(); i++) {
            int randIndex = list.get(i);
            swap(a, i, randIndex);
            swap(b, i, randIndex);
        }
    }


    private static <T> void swap(T[] a, int i, int j) {
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    private static <T> void swap(ArrayList<T> a, int i, int j) {
        T t = a.get(i);
        a.set(i, a.get(j));
        a.set(j, t);
    }


    public static <T extends Comparable<T>> boolean search(T[] a, T t) {
        for (int i = 0; i < a.length; i++) {
            if (a[i].compareTo(t) == 0)
                return true;
        }
        return false;
    }

    public static <T extends Comparable<T>> boolean search(T[][] a, T t) {
        for (int i = 0; i < a.length; i++) {
            if (search(a[i], t))
                return true;
        }
        return false;
    }


}
