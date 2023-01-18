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
package org.ojalgo.matrix.operation;

import com.google.errorprone.annotations.Var;
import java.util.function.IntSupplier;
import org.ojalgo.array.operation.AXPY;
import org.ojalgo.array.operation.DOT;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.concurrent.DivideAndConquer.Conquerer;
import org.ojalgo.concurrent.Parallelism;
import org.ojalgo.concurrent.ProcessingService;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.Scalar;

public final class HouseholderLeft implements MatrixOperation {

    public static IntSupplier PARALLELISM = Parallelism.THREADS;
    public static int THRESHOLD = 128;

    private static final DivideAndConquer.Divider DIVIDER = ProcessingService.INSTANCE.divider();

    public static void call( double[] data,  int structure,  int first,  double[] hVector,  int hFirst,  double hBeta) {

        int nbCols = data.length / structure;

        if (nbCols > THRESHOLD) {
            HouseholderLeft.divide(first, nbCols, (f, l) -> HouseholderLeft.invoke(data, structure, f, l, hVector, hFirst, hBeta));
        } else {
            HouseholderLeft.invoke(data, structure, first, nbCols, hVector, hFirst, hBeta);
        }
    }

    public static void call( double[] data,  int structure,  int first,  Householder.Primitive64 householder) {
        HouseholderLeft.call(data, structure, first, householder.vector, householder.first, householder.beta);
    }

    public static void call( double[][] data,  int structure,  int first,  double[] hVector,  int hFirst,  double hBeta) {

        int nbCols = data.length;

        if (nbCols > THRESHOLD) {
            HouseholderLeft.divide(first, nbCols, (f, l) -> HouseholderLeft.invoke(data, structure, f, l, hVector, hFirst, hBeta));
        } else {
            HouseholderLeft.invoke(data, structure, first, nbCols, hVector, hFirst, hBeta);
        }
    }

    public static void call( double[][] data,  int structure,  int first,  Householder.Primitive64 householder) {
        HouseholderLeft.call(data, structure, first, householder.vector, householder.first, householder.beta);
    }

    public static void call( float[] data,  int structure,  int first,  Householder.Primitive32 householder) {

        int nbCols = data.length / structure;

        if (nbCols > THRESHOLD) {
            HouseholderLeft.divide(first, nbCols, (f, l) -> HouseholderLeft.invoke(data, structure, f, l, householder));
        } else {
            HouseholderLeft.invoke(data, structure, first, nbCols, householder);
        }
    }

    public static <N extends Scalar<N>> void call( N[] data,  int structure,  int first,  Householder.Generic<N> householder,
             Scalar.Factory<N> scalar) {

        int nbCols = data.length / structure;

        if (nbCols > THRESHOLD) {
            HouseholderLeft.divide(first, nbCols, (f, l) -> HouseholderLeft.invoke(data, structure, f, l, householder, scalar));
        } else {
            HouseholderLeft.invoke(data, structure, first, nbCols, householder, scalar);
        }
    }

    private static void doColumn( double[] data,  int offset,  double[] vector,  double beta,  int first,  int limit) {
        double scale = beta * DOT.invoke(data, offset, vector, 0, first, limit);
        AXPY.invoke(data, offset, -scale, vector, 0, first, limit);
    }

    private static void doColumn( float[] data,  int offset,  float[] vector,  float beta,  int first,  int limit) {
        float scale = beta * DOT.invoke(data, offset, vector, 0, first, limit);
        AXPY.invoke(data, offset, -scale, vector, 0, first, limit);
    }

    static void divide( int first,  int limit,  Conquerer conquerer) {
        DIVIDER.parallelism(PARALLELISM).threshold(THRESHOLD).divide(first, limit, conquerer);
    }

    static void invoke( double[] data,  int structure,  int first,  int limit,  double[] hVector,  int hFirst,
             double hBeta) {
        for (int j = first; j < limit; j++) {
            HouseholderLeft.doColumn(data, j * structure, hVector, hBeta, hFirst, structure);
        }
    }

    static void invoke( double[][] data,  int structure,  int first,  int limit,  double[] hVector,  int hFirst,
             double hBeta) {
        for (int j = first; j < limit; j++) {
            HouseholderLeft.doColumn(data[j], 0, hVector, hBeta, hFirst, structure);
        }
    }

    static void invoke( float[] data,  int structure,  int first,  int limit,  Householder.Primitive32 householder) {

        float[] hVector = householder.vector;
        int hFirst = householder.first;
        float hBeta = householder.beta;

        for (int j = first; j < limit; j++) {
            HouseholderLeft.doColumn(data, j * structure, hVector, hBeta, hFirst, structure);
        }
    }

    static <N extends Scalar<N>> void invoke( N[] data,  int structure,  int first,  int limit,  Householder.Generic<N> householder,
             Scalar.Factory<N> scalar) {

        N[] hVector = householder.vector;
        int hFirst = householder.first;
        N hBeta = householder.beta;

        @Var Scalar<N> tmpScale;
        @Var int tmpIndex;
        for (int j = first; j < limit; j++) {
            tmpScale = scalar.zero();
            tmpIndex = hFirst + j * structure;
            for (int i = hFirst; i < structure; i++) {
                tmpScale = tmpScale.add(hVector[i].conjugate().multiply(data[tmpIndex++]));
            }
            tmpScale = tmpScale.multiply(hBeta);
            tmpIndex = hFirst + j * structure;
            for (int i = hFirst; i < structure; i++) {
                data[tmpIndex] = data[tmpIndex].subtract(tmpScale.multiply(hVector[i])).get();
                tmpIndex++;
            }
        }
    }

    private HouseholderLeft() {
        super();
    }

}
