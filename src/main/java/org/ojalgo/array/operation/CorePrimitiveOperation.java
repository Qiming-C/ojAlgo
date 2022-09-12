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

public final class CorePrimitiveOperation implements ArrayOperation {

    public static int THRESHOLD = 256;

    public static void add(final double[] data, final int first, final int limit, final double right) {
        CorePrimitiveOperation.add(data, first, limit, 1, data, right);
    }

    public static void divide(final double[] data, final int first, final int limit, final double right) {
        CorePrimitiveOperation.divide(data, first, limit, 1, data, right);
    }

    public static void multiply(final double[] data, final int first, final int limit, final double right) {
        CorePrimitiveOperation.multiply(data, first, limit, 1, data, right);
    }

    public static void negate(final byte[] data, final int first, final int limit, final int step, final byte[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) -values[i];
        }
    }

    public static void negate(final double[] data, final int first, final int limit, final int step, final double[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate(final float[] data, final int first, final int limit, final int step, final float[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate(final int[] data, final int first, final int limit, final int step, final int[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate(final long[] data, final int first, final int limit, final int step, final long[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = -values[i];
        }
    }

    public static void negate(final short[] data, final int first, final int limit, final int step, final short[] values) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) -values[i];
        }
    }

    public static void subtract(final double[] data, final int first, final int limit, final double right) {
        CorePrimitiveOperation.subtract(data, first, limit, 1, data, right);
    }

    static void add(final byte[] data, final int first, final int limit, final int step, final byte left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left + right[i]);
        }
    }

    static void add(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] + right);
        }
    }

    static void add(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] + right[i]);
        }
    }

    static void add(final double[] data, final int first, final int limit, final int step, final double left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    static void add(final double[] data, final int first, final int limit, final int step, final double[] left, final double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    static void add(final double[] data, final int first, final int limit, final int step, final double[] left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    static void add(final float[] data, final int first, final int limit, final int step, final float left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    static void add(final float[] data, final int first, final int limit, final int step, final float[] left, final float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    static void add(final float[] data, final int first, final int limit, final int step, final float[] left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    static void add(final int[] data, final int first, final int limit, final int step, final int left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    static void add(final int[] data, final int first, final int limit, final int step, final int[] left, final int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    static void add(final int[] data, final int first, final int limit, final int step, final int[] left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    static void add(final long[] data, final int first, final int limit, final int step, final long left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left + right[i];
        }
    }

    static void add(final long[] data, final int first, final int limit, final int step, final long[] left, final long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right;
        }
    }

    static void add(final long[] data, final int first, final int limit, final int step, final long[] left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] + right[i];
        }
    }

    static void add(final short[] data, final int first, final int limit, final int step, final short left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left + right[i]);
        }
    }

    static void add(final short[] data, final int first, final int limit, final int step, final short[] left, final short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] + right);
        }
    }

    static void add(final short[] data, final int first, final int limit, final int step, final short[] left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] + right[i]);
        }
    }

    static void divide(final byte[] data, final int first, final int limit, final int step, final byte left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left / right[i]);
        }
    }

    static void divide(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] / right);
        }
    }

    static void divide(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] / right[i]);
        }
    }

    static void divide(final double[] data, final int first, final int limit, final int step, final double left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    static void divide(final double[] data, final int first, final int limit, final int step, final double[] left, final double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    static void divide(final double[] data, final int first, final int limit, final int step, final double[] left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    static void divide(final float[] data, final int first, final int limit, final int step, final float left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    static void divide(final float[] data, final int first, final int limit, final int step, final float[] left, final float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    static void divide(final float[] data, final int first, final int limit, final int step, final float[] left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    static void divide(final int[] data, final int first, final int limit, final int step, final int left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    static void divide(final int[] data, final int first, final int limit, final int step, final int[] left, final int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    static void divide(final int[] data, final int first, final int limit, final int step, final int[] left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    static void divide(final long[] data, final int first, final int limit, final int step, final long left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left / right[i];
        }
    }

    static void divide(final long[] data, final int first, final int limit, final int step, final long[] left, final long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right;
        }
    }

    static void divide(final long[] data, final int first, final int limit, final int step, final long[] left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] / right[i];
        }
    }

    static void divide(final short[] data, final int first, final int limit, final int step, final short left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left / right[i]);
        }
    }

    static void divide(final short[] data, final int first, final int limit, final int step, final short[] left, final short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] / right);
        }
    }

    static void divide(final short[] data, final int first, final int limit, final int step, final short[] left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] / right[i]);
        }
    }

    static void multiply(final byte[] data, final int first, final int limit, final int step, final byte left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left * right[i]);
        }
    }

    static void multiply(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] * right);
        }
    }

    static void multiply(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] * right[i]);
        }
    }

    static void multiply(final double[] data, final int first, final int limit, final int step, final double left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    static void multiply(final double[] data, final int first, final int limit, final int step, final double[] left, final double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    static void multiply(final double[] data, final int first, final int limit, final int step, final double[] left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    static void multiply(final float[] data, final int first, final int limit, final int step, final float left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    static void multiply(final float[] data, final int first, final int limit, final int step, final float[] left, final float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    static void multiply(final float[] data, final int first, final int limit, final int step, final float[] left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    static void multiply(final int[] data, final int first, final int limit, final int step, final int left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    static void multiply(final int[] data, final int first, final int limit, final int step, final int[] left, final int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    static void multiply(final int[] data, final int first, final int limit, final int step, final int[] left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    static void multiply(final long[] data, final int first, final int limit, final int step, final long left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left * right[i];
        }
    }

    static void multiply(final long[] data, final int first, final int limit, final int step, final long[] left, final long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right;
        }
    }

    static void multiply(final long[] data, final int first, final int limit, final int step, final long[] left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] * right[i];
        }
    }

    static void multiply(final short[] data, final int first, final int limit, final int step, final short left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left * right[i]);
        }
    }

    static void multiply(final short[] data, final int first, final int limit, final int step, final short[] left, final short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] * right);
        }
    }

    static void multiply(final short[] data, final int first, final int limit, final int step, final short[] left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] * right[i]);
        }
    }

    static void subtract(final byte[] data, final int first, final int limit, final int step, final byte left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left - right[i]);
        }
    }

    static void subtract(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] - right);
        }
    }

    static void subtract(final byte[] data, final int first, final int limit, final int step, final byte[] left, final byte[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (byte) (left[i] - right[i]);
        }
    }

    static void subtract(final double[] data, final int first, final int limit, final int step, final double left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    static void subtract(final double[] data, final int first, final int limit, final int step, final double[] left, final double right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    static void subtract(final double[] data, final int first, final int limit, final int step, final double[] left, final double[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    static void subtract(final float[] data, final int first, final int limit, final int step, final float left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    static void subtract(final float[] data, final int first, final int limit, final int step, final float[] left, final float right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    static void subtract(final float[] data, final int first, final int limit, final int step, final float[] left, final float[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    static void subtract(final int[] data, final int first, final int limit, final int step, final int left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    static void subtract(final int[] data, final int first, final int limit, final int step, final int[] left, final int right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    static void subtract(final int[] data, final int first, final int limit, final int step, final int[] left, final int[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    static void subtract(final long[] data, final int first, final int limit, final int step, final long left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left - right[i];
        }
    }

    static void subtract(final long[] data, final int first, final int limit, final int step, final long[] left, final long right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right;
        }
    }

    static void subtract(final long[] data, final int first, final int limit, final int step, final long[] left, final long[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = left[i] - right[i];
        }
    }

    static void subtract(final short[] data, final int first, final int limit, final int step, final short left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left - right[i]);
        }
    }

    static void subtract(final short[] data, final int first, final int limit, final int step, final short[] left, final short right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] - right);
        }
    }

    static void subtract(final short[] data, final int first, final int limit, final int step, final short[] left, final short[] right) {
        for (int i = first; i < limit; i += step) {
            data[i] = (short) (left[i] - right[i]);
        }
    }

}
