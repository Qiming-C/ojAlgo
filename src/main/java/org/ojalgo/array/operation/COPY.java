/*
 * Copyright 1997-2022 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.array.operation;

import java.lang.reflect.Array;

import org.ojalgo.structure.Access2D;

/**
 * The ?copy routines perform a vector-vector operation defined as y = x, where x and y are vectors.
 *
 * @author apete
 */
public abstract class COPY implements ArrayOperation {

    public static int THRESHOLD = 128;

    public static void column( Access2D<?> source,  long col,  double[] destination,  int first,  int limit) {
        for (int i = first; i < limit; i++) {
            destination[i] = source.doubleValue(i, col);
        }
    }

    public static double[] copyOf( double[] original) {
        int tmpLength = original.length;
        double[] retVal = new double[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static float[] copyOf( float[] original) {
        int tmpLength = original.length;
        float[] retVal = new float[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static int[] copyOf( int[] original) {
        int tmpLength = original.length;
        int[] retVal = new int[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static long[] copyOf( long[] original) {
        int tmpLength = original.length;
        long[] retVal = new long[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf( T[] original) {
        int tmpLength = original.length;
        var retVal = (T[]) Array.newInstance(original.getClass().getComponentType(), tmpLength);
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static int[] invoke( int[] source,  int[] destination) {
        int limit = Math.min(source.length, destination.length);
        System.arraycopy(source, 0, destination, 0, limit);
        return destination;
    }

    public static void row( Access2D<?> source,  long row,  double[] destination,  int first,  int limit) {
        for (int j = first; j < limit; j++) {
            destination[j] = source.doubleValue(row, j);
        }
    }

}
