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
import org.ojalgo.scalar.Scalar;

public abstract class RotateRight implements ArrayOperation {

    public static int THRESHOLD = 128;

    public static void invoke( double[] data,  int structure,  int colA,  int colB,  double cos,  double sin) {

        @Var double oldA;
        @Var double oldB;

        @Var int indexA = colA * structure;
        @Var int indexB = colB * structure;

        for (int i = 0; i < structure; i++) {

            oldA = data[indexA];
            oldB = data[indexB];

            data[indexA] = cos * oldA - sin * oldB;
            data[indexB] = cos * oldB + sin * oldA;

            indexA++;
            indexB++;
        }
    }

    public static void invoke( float[] data,  int structure,  int colA,  int colB,  float cos,  float sin) {

        @Var float oldA;
        @Var float oldB;

        @Var int indexA = colA * structure;
        @Var int indexB = colB * structure;

        for (int i = 0; i < structure; i++) {

            oldA = data[indexA];
            oldB = data[indexB];

            data[indexA] = cos * oldA - sin * oldB;
            data[indexB] = cos * oldB + sin * oldA;

            indexA++;
            indexB++;
        }
    }

    public static <N extends Scalar<N>> void invoke( N[] data,  int structure,  int colA,  int colB,  N cos,  N sin) {

        @Var N oldA;
        @Var N oldB;

        @Var int indexA = colA * structure;
        @Var int indexB = colB * structure;

        for (int i = 0; i < structure; i++) {

            oldA = data[indexA];
            oldB = data[indexB];

            data[indexA] = cos.multiply(oldA).subtract(sin.multiply(oldB)).get();
            data[indexB] = cos.multiply(oldB).add(sin.multiply(oldA)).get();

            indexA++;
            indexB++;
        }
    }

}
