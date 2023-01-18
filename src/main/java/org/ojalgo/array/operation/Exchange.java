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

import com.google.errorprone.annotations.Var;
import org.ojalgo.array.BasicArray;

public abstract class Exchange implements ArrayOperation {

    public static int THRESHOLD = 256;

    public static <N extends Comparable<N>> void exchange( BasicArray<N> data,  long firstA,  long firstB,  long step,  long count) {

        @Var long indexA = firstA;
        @Var long indexB = firstB;

        @Var N tmpVal;

        for (long i = 0L; i < count; i++) {

            tmpVal = data.get(indexA);
            data.set(indexA, data.get(indexB));
            data.set(indexB, tmpVal);

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( byte[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var byte tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( double[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var double tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( float[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var float tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( int[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var int tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( long[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var long tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static <N extends Comparable<N>> void exchange( N[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var N tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

    public static void exchange( short[] data,  int firstA,  int firstB,  int step,  int count) {

        @Var int indexA = firstA;
        @Var int indexB = firstB;

        @Var short tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = data[indexA];
            data[indexA] = data[indexB];
            data[indexB] = tmpVal;

            indexA += step;
            indexB += step;
        }
    }

}
