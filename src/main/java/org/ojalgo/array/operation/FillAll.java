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
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

public abstract class FillAll implements ArrayOperation {

    public static int THRESHOLD = 128;

    public static <N extends Comparable<N>> void fill( BasicArray<N> data,  int first,  int limit,  int step,  double value) {
        for (int i = first; i < limit; i += step) {
            data.set(i, value);
        }
    }

    public static <N extends Comparable<N>> void fill( BasicArray<N> data,  int first,  int limit,  int step,
             NullaryFunction<N> supplier) {
        for (int i = first; i < limit; i += step) {
            data.set(i, supplier.doubleValue());
        }
    }

    public static <N extends Comparable<N>> void fill( BasicArray<N> data,  long first,  long limit,  long step,  double value) {
        for (long i = first; i < limit; i += step) {
            data.set(i, value);
        }
    }

    public static <N extends Comparable<N>> void fill( BasicArray<N> data,  long first,  long limit,  long step,  N value) {

        switch (data.getMathType()) {
        case R064:
            double doubleValue = Scalar.doubleValue(value);
            for (long i = first; i < limit; i += step) {
                data.set(i, doubleValue);
            }
            break;
        case R032:
            float floatValue = Scalar.floatValue(value);
            for (long i = first; i < limit; i += step) {
                data.set(i, floatValue);
            }
            break;
        case Z064:
            long longValue = Scalar.longValue(value);
            for (long i = first; i < limit; i += step) {
                data.set(i, longValue);
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            int intValue = Scalar.intValue(value);
            for (long i = first; i < limit; i += step) {
                data.set(i, intValue);
            }
            break;
        default:
            for (long i = first; i < limit; i += step) {
                data.set(i, value);
            }
            break;
        }
    }

    public static <N extends Comparable<N>> void fill( BasicArray<N> data,  long first,  long limit,  long step,
             NullaryFunction<?> supplier) {

        switch (data.getMathType()) {
        case R064:
            for (long i = first; i < limit; i += step) {
                data.set(i, supplier.doubleValue());
            }
            break;
        case R032:
            for (long i = first; i < limit; i += step) {
                data.set(i, supplier.floatValue());
            }
            break;
        case Z064:
            for (long i = first; i < limit; i += step) {
                data.set(i, supplier.longValue());
            }
            break;
        case Z032:
        case Z016:
        case Z008:
            for (long i = first; i < limit; i += step) {
                data.set(i, supplier.intValue());
            }
            break;
        default:
            for (long i = first; i < limit; i += step) {
                data.set(i, supplier.get());
            }
            break;
        }
    }

    public static void fill( byte[] data,  int first,  int limit,  int step,  byte value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static void fill( byte[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.byteValue();
        }
    }

    public static void fill( double[] data,  int first,  int limit,  int step,  double value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static void fill( double[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.doubleValue();
        }
    }

    public static void fill( float[] data,  int first,  int limit,  int step,  float value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static void fill( float[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.floatValue();
        }
    }

    public static void fill( int[] data,  int first,  int limit,  int step,  int value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static void fill( int[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.intValue();
        }
    }

    public static void fill( long[] data,  int first,  int limit,  int step,  long value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static void fill( long[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.longValue();
        }
    }

    public static <N extends Comparable<N>> void fill( N[] data,  int first,  int limit,  int step,  N value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    public static <N extends Comparable<N>> void fill( N[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier,
             Scalar.Factory<N> scalar) {
        for (int i = first; i < limit; i += step) {
            data[i] = scalar.cast(supplier.invoke());
        }
    }

    public static void fill( short[] data,  int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        for (int i = first; i < limit; i += step) {
            data[i] = supplier.shortValue();
        }
    }

    public static void fill( short[] data,  int first,  int limit,  int step,  short value) {
        for (int i = first; i < limit; i += step) {
            data[i] = value;
        }
    }

    protected static void fill( BasicArray<?> data,  Access1D<?> value) {
        var tmpLimit = (int) Math.min(data.count(), value.count());
        for (int i = 0; i < tmpLimit; i++) {
            data.set(i, value.doubleValue(i));
        }
    }

}
