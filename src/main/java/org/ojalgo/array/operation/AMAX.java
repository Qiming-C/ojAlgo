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

import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import com.google.errorprone.annotations.Var;
import java.math.BigDecimal;
import org.ojalgo.array.PlainArray;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Structure2D;

/**
 * Given a vector x, the i?amax functions return the position of the vector element x[i] that has the largest
 * absolute value for real flavors, or the largest sum |Re(x[i])|+|Im(x[i])| for complex flavors. If n is not
 * positive, 0 is returned. If more than one vector element is found with the same largest absolute value, the
 * index of the first one encountered is returned.
 *
 * @author apete
 */
public abstract class AMAX implements ArrayOperation {

    public static int THRESHOLD = 128;

    public static long invoke( Access1D<?> data,  long first,  long limit,  long step) {

        @Var long retVal = first;
        @Var double largest = 0D;
        @Var double candidate;

        for (long i = first; i < limit; i += step) {
            candidate = Math.abs(data.doubleValue(i));
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( BigDecimal[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var BigDecimal largest = BigMath.ZERO;
        @Var BigDecimal candidate;

        for (int i = first; i < limit; i += step) {
            candidate = data[i].abs();
            if (candidate.compareTo(largest) > 0) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( byte[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var byte largest = 0;
        @Var byte candidate;

        for (int i = first; i < limit; i += step) {
            candidate = (byte) Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( double[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var double largest = 0D;
        @Var double candidate;

        for (int i = first; i < limit; i += step) {
            candidate = Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static long invoke( double[][] data) {

        int nbRows = data.length;
        int nbCols = nbRows != 0 ? data[0].length : 0;

        @Var long retVal = 0;
        @Var double largest = ZERO;
        @Var double candidate;

        @Var double[] tmpRow;

        for (int i = 0; i < nbRows; i++) {
            tmpRow = data[i];

            for (int j = 0; j < nbCols; j++) {
                candidate = Math.abs(tmpRow[j]);
                if (candidate > largest) {
                    largest = candidate;
                    retVal = Structure2D.index(nbRows, i, j);
                }
            }
        }

        return retVal;
    }

    public static int invoke( float[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var float largest = 0F;
        @Var float candidate;

        for (int i = first; i < limit; i += step) {
            candidate = Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( int[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var int largest = 0;
        @Var int candidate;

        for (int i = first; i < limit; i += step) {
            candidate = Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( long[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var long largest = 0L;
        @Var long candidate;

        for (int i = first; i < limit; i += step) {
            candidate = Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static <N extends Scalar<N>> int invoke( N[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var double largest = 0D;
        @Var double candidate;

        for (int i = first; i < limit; i += step) {
            candidate = data[i].norm();
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( PlainArray<?> data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var double largest = 0D;
        @Var double candidate;

        for (int i = first; i < limit; i += step) {
            candidate = Math.abs(data.doubleValue(i));
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

    public static int invoke( short[] data,  int first,  int limit,  int step) {

        @Var int retVal = first;
        @Var short largest = 0;
        @Var short candidate;

        for (int i = first; i < limit; i += step) {
            candidate = (short) Math.abs(data[i]);
            if (candidate > largest) {
                largest = candidate;
                retVal = i;
            }
        }

        return retVal;
    }

}
