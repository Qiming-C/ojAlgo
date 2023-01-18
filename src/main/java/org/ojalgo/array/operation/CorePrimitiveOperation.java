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
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

public abstract class CorePrimitiveOperation implements ArrayOperation {

    public static int THRESHOLD = 256;

    public static <N extends Comparable<N>> void add( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) + right.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) + right.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) + right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) + right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void add( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Comparable<?> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) + doubleValue);
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) + floatValue);
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) + longValue);
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) + intValue);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void add( BasicArray<N> data,  long first,  long limit,  long step,  Comparable<?> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, doubleValue + right.doubleValue(i));
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, floatValue + right.floatValue(i));
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, longValue + right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, intValue + right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void add( byte[] data,  int first,  int limit,  int step,  byte left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left + right[i]);
        }
    }

    public static void add( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] + right);
        }
    }

    public static void add( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] + right[i]);
        }
    }

    public static void add( double[] data,  int first,  int limit,  int step,  double left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    public static void add( double[] data,  int first,  int limit,  int step,  double[] left,  double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    public static void add( double[] data,  int first,  int limit,  int step,  double[] left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    public static void add( float[] data,  int first,  int limit,  int step,  float left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    public static void add( float[] data,  int first,  int limit,  int step,  float[] left,  float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    public static void add( float[] data,  int first,  int limit,  int step,  float[] left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    public static void add( int[] data,  int first,  int limit,  int step,  int left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    public static void add( int[] data,  int first,  int limit,  int step,  int[] left,  int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    public static void add( int[] data,  int first,  int limit,  int step,  int[] left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    public static void add( long[] data,  int first,  int limit,  int step,  long left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    public static void add( long[] data,  int first,  int limit,  int step,  long[] left,  long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    public static void add( long[] data,  int first,  int limit,  int step,  long[] left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    public static void add( short[] data,  int first,  int limit,  int step,  short left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left + right[i]);
        }
    }

    public static void add( short[] data,  int first,  int limit,  int step,  short[] left,  short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] + right);
        }
    }

    public static void add( short[] data,  int first,  int limit,  int step,  short[] left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] + right[i]);
        }
    }

    public static <N extends Comparable<N>> void divide( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) / right.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) / right.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) / right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) / right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void divide( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Comparable<?> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) / doubleValue);
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) / floatValue);
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) / longValue);
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) / intValue);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void divide( BasicArray<N> data,  long first,  long limit,  long step,  Comparable<?> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, doubleValue / right.doubleValue(i));
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, floatValue / right.floatValue(i));
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, longValue / right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, intValue / right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void divide( byte[] data,  int first,  int limit,  int step,  byte left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left / right[i]);
        }
    }

    public static void divide( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] / right);
        }
    }

    public static void divide( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] / right[i]);
        }
    }

    public static void divide( double[] data,  int first,  int limit,  int step,  double left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    public static void divide( double[] data,  int first,  int limit,  int step,  double[] left,  double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    public static void divide( double[] data,  int first,  int limit,  int step,  double[] left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    public static void divide( float[] data,  int first,  int limit,  int step,  float left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    public static void divide( float[] data,  int first,  int limit,  int step,  float[] left,  float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    public static void divide( float[] data,  int first,  int limit,  int step,  float[] left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    public static void divide( int[] data,  int first,  int limit,  int step,  int left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    public static void divide( int[] data,  int first,  int limit,  int step,  int[] left,  int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    public static void divide( int[] data,  int first,  int limit,  int step,  int[] left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    public static void divide( long[] data,  int first,  int limit,  int step,  long left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    public static void divide( long[] data,  int first,  int limit,  int step,  long[] left,  long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    public static void divide( long[] data,  int first,  int limit,  int step,  long[] left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    public static void divide( short[] data,  int first,  int limit,  int step,  short left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left / right[i]);
        }
    }

    public static void divide( short[] data,  int first,  int limit,  int step,  short[] left,  short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] / right);
        }
    }

    public static void divide( short[] data,  int first,  int limit,  int step,  short[] left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] / right[i]);
        }
    }

    public static <N extends Comparable<N>> void multiply( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) * right.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) * right.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) * right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) * right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void multiply( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Comparable<?> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) * doubleValue);
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) * floatValue);
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) * longValue);
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) * intValue);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void multiply( BasicArray<N> data,  long first,  long limit,  long step,
             Comparable<?> left,  Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, doubleValue * right.doubleValue(i));
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, floatValue * right.floatValue(i));
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, longValue * right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, intValue * right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void multiply( byte[] data,  int first,  int limit,  int step,  byte left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left * right[i]);
        }
    }

    public static void multiply( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] * right);
        }
    }

    public static void multiply( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] * right[i]);
        }
    }

    public static void multiply( double[] data,  int first,  int limit,  int step,  double left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    public static void multiply( double[] data,  int first,  int limit,  int step,  double[] left,  double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    public static void multiply( double[] data,  int first,  int limit,  int step,  double[] left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    public static void multiply( float[] data,  int first,  int limit,  int step,  float left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    public static void multiply( float[] data,  int first,  int limit,  int step,  float[] left,  float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    public static void multiply( float[] data,  int first,  int limit,  int step,  float[] left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    public static void multiply( int[] data,  int first,  int limit,  int step,  int left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    public static void multiply( int[] data,  int first,  int limit,  int step,  int[] left,  int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    public static void multiply( int[] data,  int first,  int limit,  int step,  int[] left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    public static void multiply( long[] data,  int first,  int limit,  int step,  long left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    public static void multiply( long[] data,  int first,  int limit,  int step,  long[] left,  long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    public static void multiply( long[] data,  int first,  int limit,  int step,  long[] left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    public static void multiply( short[] data,  int first,  int limit,  int step,  short left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left * right[i]);
        }
    }

    public static void multiply( short[] data,  int first,  int limit,  int step,  short[] left,  short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] * right);
        }
    }

    public static void multiply( short[] data,  int first,  int limit,  int step,  short[] left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] * right[i]);
        }
    }

    public static <N extends Comparable<N>> void negate( BasicArray<N> data,  long first,  long limit,  long step,
             Access1D<N> values) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, -values.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, -values.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, -values.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, -values.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void negate( byte[] data,  int first,  int limit,  int step,  byte[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) -values[i];
        }
    }

    public static void negate( double[] data,  int first,  int limit,  int step,  double[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate( float[] data,  int first,  int limit,  int step,  float[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate( int[] data,  int first,  int limit,  int step,  int[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate( long[] data,  int first,  int limit,  int step,  long[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate( short[] data,  int first,  int limit,  int step,  short[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) -values[i];
        }
    }

    public static <N extends Comparable<N>> void subtract( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) - right.doubleValue(i));
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) - right.floatValue(i));
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) - right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) - right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void subtract( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> left,
             Comparable<?> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.doubleValue(i) - doubleValue);
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.floatValue(i) - floatValue);
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.longValue(i) - longValue);
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(right);
            for (long i = first; i < limit; i += step) {
                data.set(i, left.intValue(i) - intValue);
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static <N extends Comparable<N>> void subtract( BasicArray<N> data,  long first,  long limit,  long step,
             Comparable<?> left,  Access1D<N> right) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, doubleValue - right.doubleValue(i));
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, floatValue - right.floatValue(i));
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, longValue - right.longValue(i));
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(left);
            for (long i = first; i < limit; i += step) {
                data.set(i, intValue - right.intValue(i));
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    public static void subtract( byte[] data,  int first,  int limit,  int step,  byte left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left - right[i]);
        }
    }

    public static void subtract( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] - right);
        }
    }

    public static void subtract( byte[] data,  int first,  int limit,  int step,  byte[] left,  byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] - right[i]);
        }
    }

    public static void subtract( double[] data,  int first,  int limit,  int step,  double left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    public static void subtract( double[] data,  int first,  int limit,  int step,  double[] left,  double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    public static void subtract( double[] data,  int first,  int limit,  int step,  double[] left,  double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    public static void subtract( float[] data,  int first,  int limit,  int step,  float left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    public static void subtract( float[] data,  int first,  int limit,  int step,  float[] left,  float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    public static void subtract( float[] data,  int first,  int limit,  int step,  float[] left,  float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    public static void subtract( int[] data,  int first,  int limit,  int step,  int left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    public static void subtract( int[] data,  int first,  int limit,  int step,  int[] left,  int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    public static void subtract( int[] data,  int first,  int limit,  int step,  int[] left,  int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    public static void subtract( long[] data,  int first,  int limit,  int step,  long left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    public static void subtract( long[] data,  int first,  int limit,  int step,  long[] left,  long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    public static void subtract( long[] data,  int first,  int limit,  int step,  long[] left,  long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    public static void subtract( short[] data,  int first,  int limit,  int step,  short left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left - right[i]);
        }
    }

    public static void subtract( short[] data,  int first,  int limit,  int step,  short[] left,  short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] - right);
        }
    }

    public static void subtract( short[] data,  int first,  int limit,  int step,  short[] left,  short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] - right[i]);
        }
    }

}
