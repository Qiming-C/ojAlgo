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

import org.ojalgo.array.BasicArray;
import org.ojalgo.function.VoidFunction;

public abstract class OperationVoid implements ArrayOperation {

    public static int THRESHOLD = 256;

    public static <N extends Comparable<N>> void invoke( BasicArray<N> data,  int first,  int limit,  int step,
             VoidFunction<N> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data.doubleValue(i));
        }
    }

    public static <N extends Comparable<N>> void invoke( BasicArray<N> data,  long first,  long limit,  long step,
             VoidFunction<N> visitor) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                visitor.invoke(data.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                visitor.invoke(data.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                visitor.invoke(data.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                visitor.invoke(data.intValue(i));
            }
            break;
        default:
            for (long i = first; i < limit; i += step) {
                visitor.invoke(data.get(i));
            }
            break;
        }
    }

    public static void invoke( byte[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static void invoke( double[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static void invoke( float[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static void invoke( int[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static void invoke( long[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static <N extends Comparable<N>> void invoke( N[] data,  int first,  int limit,  int step,  VoidFunction<N> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

    public static void invoke( short[] data,  int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        for (int i = first; i < limit; i += step) {
            visitor.invoke(data[i]);
        }
    }

}
