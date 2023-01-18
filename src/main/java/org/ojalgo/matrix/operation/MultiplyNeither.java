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
import java.util.Arrays;
import java.util.function.IntSupplier;
import org.ojalgo.array.operation.AXPY;
import org.ojalgo.array.operation.DOT;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.concurrent.DivideAndConquer.Conquerer;
import org.ojalgo.concurrent.Parallelism;
import org.ojalgo.concurrent.ProcessingService;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;

public class MultiplyNeither implements MatrixOperation {

    @FunctionalInterface
    public interface Generic<N extends Scalar<N>> {

        void invoke(N[] product, N[] left, int complexity, N[] right, Scalar.Factory<N> scalar);

    }

    @FunctionalInterface
    public interface Primitive32 {

        void invoke(float[] product, float[] left, int complexity, float[] right);

    }

    @FunctionalInterface
    public interface Primitive64 {

        void invoke(double[] product, double[] left, int complexity, double[] right);

    }

    public static IntSupplier PARALLELISM = Parallelism.THREADS;
    public static int THRESHOLD = 32;

    private static final DivideAndConquer.Divider DIVIDER = ProcessingService.INSTANCE.divider();

    public static <N extends Scalar<N>> MultiplyNeither.Generic<N> newGeneric( long rows,  long columns) {
        if (rows > THRESHOLD && columns > THRESHOLD) {
            return MultiplyNeither::fillMxN_MT;
        }
        if (columns == 1) {
            return MultiplyNeither::fillMx1;
        }
        if (rows == 1) {
            return MultiplyNeither::fill1xN;
        }
        return MultiplyNeither::fillMxN;
    }

    public static MultiplyNeither.Primitive32 newPrimitive32( long rows,  long columns) {
        if (rows > THRESHOLD && columns > THRESHOLD) {
            return MultiplyNeither::fillMxN_MT;
        }
        if (columns == 1) {
            return MultiplyNeither::fillMx1;
        }
        if (rows == 1) {
            return MultiplyNeither::fill1xN;
        }
        return MultiplyNeither::fillMxN;
    }

    public static MultiplyNeither.Primitive64 newPrimitive64( long rows,  long columns) {
        if (rows > THRESHOLD && columns > THRESHOLD) {
            return MultiplyNeither::fillMxN_MT;
        }
        if (rows == 5 && columns == 5) {
            return MultiplyNeither::fill5x5;
        }
        if (rows == 4 && columns == 4) {
            return MultiplyNeither::fill4x4;
        }
        if (rows == 3 && columns == 3) {
            return MultiplyNeither::fill3x3;
        }
        if (rows == 2 && columns == 2) {
            return MultiplyNeither::fill2x2;
        }
        if (rows == 1 && columns == 1) {
            return MultiplyNeither::fill1x1;
        }
        if (columns == 1) {
            return MultiplyNeither::fillMx1;
        }
        if (rows == 10) {
            return MultiplyNeither::fill0xN;
        }
        if (rows == 9) {
            return MultiplyNeither::fill9xN;
        }
        if (rows == 8) {
            return MultiplyNeither::fill8xN;
        }
        if (rows == 7) {
            return MultiplyNeither::fill7xN;
        }
        if (rows == 6) {
            return MultiplyNeither::fill6xN;
        }
        if (rows == 1) {
            return MultiplyNeither::fill1xN;
        }
        return MultiplyNeither::fillMxN;
    }

    

    static void add1xN( double[] product,  double[] left,  int complexity,  double[] right) {

        for (int j = 0, nbCols = product.length; j < nbCols; j++) {
            product[j] += DOT.invoke(left, 0, right, j * complexity, 0, complexity);
        }
    }

    static void add1xN( float[] product,  float[] left,  int complexity,  float[] right) {

        for (int j = 0, nbCols = product.length; j < nbCols; j++) {
            product[j] += DOT.invoke(left, 0, right, j * complexity, 0, complexity);
        }
    }

    static void addMx1( double[] product,  double[] left,  int complexity,  double[] right) {

        int nbRows = product.length;

        for (int c = 0; c < complexity; c++) {
            AXPY.invoke(product, 0, right[c], left, c * nbRows, 0, nbRows);
        }
    }

    static void addMx1( float[] product,  float[] left,  int complexity,  float[] right) {

        int nbRows = product.length;

        for (int c = 0; c < complexity; c++) {
            AXPY.invoke(product, 0, right[c], left, c * nbRows, 0, nbRows);
        }
    }

    static <N extends Scalar<N>> void addMx1( N[] product,  N[] left,  int complexity,  N[] right) {

        int nbRows = product.length;

        for (int c = 0; c < complexity; c++) {
            AXPY.invoke(product, 0, right[c], left, c * nbRows, 0, nbRows);
        }
    }

    static void addMxC( double[] product,  int firstColumn,  int columnLimit,  double[] left,  int complexity,  double[] right) {

        int nbRows = left.length / complexity;

        for (int j = firstColumn; j < columnLimit; j++) {
            for (int c = 0; c < complexity; c++) {
                AXPY.invoke(product, j * nbRows, right[c + j * complexity], left, c * nbRows, 0, nbRows);
            }
        }
    }

    static void addMxC( float[] product,  int firstColumn,  int columnLimit,  float[] left,  int complexity,  float[] right) {

        int nbRows = left.length / complexity;

        for (int j = firstColumn; j < columnLimit; j++) {
            for (int c = 0; c < complexity; c++) {
                AXPY.invoke(product, j * nbRows, right[c + j * complexity], left, c * nbRows, 0, nbRows);
            }
        }
    }

    static <N extends Scalar<N>> void addMxC( N[] product,  int firstColumn,  int columnLimit,  N[] left,  int complexity,
             N[] right) {

        int nbRows = left.length / complexity;

        for (int j = firstColumn; j < columnLimit; j++) {
            for (int c = 0; c < complexity; c++) {
                AXPY.invoke(product, j * nbRows, right[c + j * complexity], left, c * nbRows, 0, nbRows);
            }
        }
    }

    static void addMxN_MT( double[] product,  double[] left,  int complexity,  double[] right) {
        MultiplyNeither.divide(0, right.length / complexity, (f, l) -> MultiplyNeither.addMxC(product, f, l, left, complexity, right));
    }

    static void addMxN_MT( float[] product,  float[] left,  int complexity,  float[] right) {
        MultiplyNeither.divide(0, right.length / complexity, (f, l) -> MultiplyNeither.addMxC(product, f, l, left, complexity, right));
    }

    static <N extends Scalar<N>> void addMxN_MT( N[] product,  N[] left,  int complexity,  N[] right) {
        MultiplyNeither.divide(0, right.length / complexity, (f, l) -> MultiplyNeither.addMxC(product, f, l, left, complexity, right));
    }

    static void divide( int first,  int limit,  Conquerer conquerer) {
        DIVIDER.parallelism(PARALLELISM).threshold(THRESHOLD).divide(first, limit, conquerer);
    }

    static void fill0xN( double[] product,  double[] left,  int complexity,  double[] right) {

        int tmpRowDim = 10;
        int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            @Var double tmp0J = PrimitiveMath.ZERO;
            @Var double tmp1J = PrimitiveMath.ZERO;
            @Var double tmp2J = PrimitiveMath.ZERO;
            @Var double tmp3J = PrimitiveMath.ZERO;
            @Var double tmp4J = PrimitiveMath.ZERO;
            @Var double tmp5J = PrimitiveMath.ZERO;
            @Var double tmp6J = PrimitiveMath.ZERO;
            @Var double tmp7J = PrimitiveMath.ZERO;
            @Var double tmp8J = PrimitiveMath.ZERO;
            @Var double tmp9J = PrimitiveMath.ZERO;

            @Var int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                double tmpRightCJ = right[c + j * complexity];
                tmp0J += left[tmpIndex++] * tmpRightCJ;
                tmp1J += left[tmpIndex++] * tmpRightCJ;
                tmp2J += left[tmpIndex++] * tmpRightCJ;
                tmp3J += left[tmpIndex++] * tmpRightCJ;
                tmp4J += left[tmpIndex++] * tmpRightCJ;
                tmp5J += left[tmpIndex++] * tmpRightCJ;
                tmp6J += left[tmpIndex++] * tmpRightCJ;
                tmp7J += left[tmpIndex++] * tmpRightCJ;
                tmp8J += left[tmpIndex++] * tmpRightCJ;
                tmp9J += left[tmpIndex++] * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
            product[++tmpIndex] = tmp8J;
            product[++tmpIndex] = tmp9J;
        }
    }

    static void fill1x1( double[] product,  double[] left,  int complexity,  double[] right) {

        @Var double tmp00 = PrimitiveMath.ZERO;

        int nbRows = left.length / complexity;

        for (int c = 0; c < complexity; c++) {
            tmp00 += left[c * nbRows] * right[c];
        }

        product[0] = tmp00;
    }

    static void fill1xN( double[] product,  double[] left,  int complexity,  double[] right) {

        for (int j = 0, nbCols = product.length; j < nbCols; j++) {
            product[j] = DOT.invoke(left, 0, right, j * complexity, 0, complexity);
        }
    }

    static void fill1xN( float[] product,  float[] left,  int complexity,  float[] right) {

        for (int j = 0, nbCols = product.length; j < nbCols; j++) {
            product[j] = DOT.invoke(left, 0, right, j * complexity, 0, complexity);
        }
    }

    static <N extends Scalar<N>> void fill1xN( N[] product,  N[] left,  int complexity,  N[] right,  Scalar.Factory<N> scalar) {

        for (int j = 0, nbCols = product.length; j < nbCols; j++) {
            product[j] = DOT.invoke(left, 0, right, j * complexity, 0, complexity, scalar);
        }
    }

    static void fill2x2( double[] product,  double[] left,  int complexity,  double[] right) {

        @Var double tmp00 = PrimitiveMath.ZERO;
        @Var double tmp10 = PrimitiveMath.ZERO;
        @Var double tmp01 = PrimitiveMath.ZERO;
        @Var double tmp11 = PrimitiveMath.ZERO;

        @Var int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 2;
            double tmpLeft0 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft1 = left[tmpIndex];
            tmpIndex = c;
            double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight1 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp01;
        product[3] = tmp11;
    }

    static void fill3x3( double[] product,  double[] left,  int complexity,  double[] right) {

        @Var double tmp00 = PrimitiveMath.ZERO;
        @Var double tmp10 = PrimitiveMath.ZERO;
        @Var double tmp20 = PrimitiveMath.ZERO;
        @Var double tmp01 = PrimitiveMath.ZERO;
        @Var double tmp11 = PrimitiveMath.ZERO;
        @Var double tmp21 = PrimitiveMath.ZERO;
        @Var double tmp02 = PrimitiveMath.ZERO;
        @Var double tmp12 = PrimitiveMath.ZERO;
        @Var double tmp22 = PrimitiveMath.ZERO;

        @Var int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 3;
            double tmpLeft0 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft1 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft2 = left[tmpIndex];
            tmpIndex = c;
            double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight2 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp01;
        product[4] = tmp11;
        product[5] = tmp21;
        product[6] = tmp02;
        product[7] = tmp12;
        product[8] = tmp22;
    }

    static void fill4x4( double[] product,  double[] left,  int complexity,  double[] right) {

        @Var double tmp00 = PrimitiveMath.ZERO;
        @Var double tmp10 = PrimitiveMath.ZERO;
        @Var double tmp20 = PrimitiveMath.ZERO;
        @Var double tmp30 = PrimitiveMath.ZERO;
        @Var double tmp01 = PrimitiveMath.ZERO;
        @Var double tmp11 = PrimitiveMath.ZERO;
        @Var double tmp21 = PrimitiveMath.ZERO;
        @Var double tmp31 = PrimitiveMath.ZERO;
        @Var double tmp02 = PrimitiveMath.ZERO;
        @Var double tmp12 = PrimitiveMath.ZERO;
        @Var double tmp22 = PrimitiveMath.ZERO;
        @Var double tmp32 = PrimitiveMath.ZERO;
        @Var double tmp03 = PrimitiveMath.ZERO;
        @Var double tmp13 = PrimitiveMath.ZERO;
        @Var double tmp23 = PrimitiveMath.ZERO;
        @Var double tmp33 = PrimitiveMath.ZERO;

        @Var int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 4;
            double tmpLeft0 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft1 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft2 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft3 = left[tmpIndex];
            tmpIndex = c;
            double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight2 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight3 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp30 += tmpLeft3 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp31 += tmpLeft3 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
            tmp32 += tmpLeft3 * tmpRight2;
            tmp03 += tmpLeft0 * tmpRight3;
            tmp13 += tmpLeft1 * tmpRight3;
            tmp23 += tmpLeft2 * tmpRight3;
            tmp33 += tmpLeft3 * tmpRight3;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp30;
        product[4] = tmp01;
        product[5] = tmp11;
        product[6] = tmp21;
        product[7] = tmp31;
        product[8] = tmp02;
        product[9] = tmp12;
        product[10] = tmp22;
        product[11] = tmp32;
        product[12] = tmp03;
        product[13] = tmp13;
        product[14] = tmp23;
        product[15] = tmp33;
    }

    static void fill5x5( double[] product,  double[] left,  int complexity,  double[] right) {

        @Var double tmp00 = PrimitiveMath.ZERO;
        @Var double tmp10 = PrimitiveMath.ZERO;
        @Var double tmp20 = PrimitiveMath.ZERO;
        @Var double tmp30 = PrimitiveMath.ZERO;
        @Var double tmp40 = PrimitiveMath.ZERO;
        @Var double tmp01 = PrimitiveMath.ZERO;
        @Var double tmp11 = PrimitiveMath.ZERO;
        @Var double tmp21 = PrimitiveMath.ZERO;
        @Var double tmp31 = PrimitiveMath.ZERO;
        @Var double tmp41 = PrimitiveMath.ZERO;
        @Var double tmp02 = PrimitiveMath.ZERO;
        @Var double tmp12 = PrimitiveMath.ZERO;
        @Var double tmp22 = PrimitiveMath.ZERO;
        @Var double tmp32 = PrimitiveMath.ZERO;
        @Var double tmp42 = PrimitiveMath.ZERO;
        @Var double tmp03 = PrimitiveMath.ZERO;
        @Var double tmp13 = PrimitiveMath.ZERO;
        @Var double tmp23 = PrimitiveMath.ZERO;
        @Var double tmp33 = PrimitiveMath.ZERO;
        @Var double tmp43 = PrimitiveMath.ZERO;
        @Var double tmp04 = PrimitiveMath.ZERO;
        @Var double tmp14 = PrimitiveMath.ZERO;
        @Var double tmp24 = PrimitiveMath.ZERO;
        @Var double tmp34 = PrimitiveMath.ZERO;
        @Var double tmp44 = PrimitiveMath.ZERO;

        @Var int tmpIndex;
        for (int c = 0; c < complexity; c++) {

            tmpIndex = c * 5;
            double tmpLeft0 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft1 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft2 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft3 = left[tmpIndex];
            tmpIndex++;
            double tmpLeft4 = left[tmpIndex];
            tmpIndex = c;
            double tmpRight0 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight1 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight2 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight3 = right[tmpIndex];
            tmpIndex += complexity;
            double tmpRight4 = right[tmpIndex];

            tmp00 += tmpLeft0 * tmpRight0;
            tmp10 += tmpLeft1 * tmpRight0;
            tmp20 += tmpLeft2 * tmpRight0;
            tmp30 += tmpLeft3 * tmpRight0;
            tmp40 += tmpLeft4 * tmpRight0;
            tmp01 += tmpLeft0 * tmpRight1;
            tmp11 += tmpLeft1 * tmpRight1;
            tmp21 += tmpLeft2 * tmpRight1;
            tmp31 += tmpLeft3 * tmpRight1;
            tmp41 += tmpLeft4 * tmpRight1;
            tmp02 += tmpLeft0 * tmpRight2;
            tmp12 += tmpLeft1 * tmpRight2;
            tmp22 += tmpLeft2 * tmpRight2;
            tmp32 += tmpLeft3 * tmpRight2;
            tmp42 += tmpLeft4 * tmpRight2;
            tmp03 += tmpLeft0 * tmpRight3;
            tmp13 += tmpLeft1 * tmpRight3;
            tmp23 += tmpLeft2 * tmpRight3;
            tmp33 += tmpLeft3 * tmpRight3;
            tmp43 += tmpLeft4 * tmpRight3;
            tmp04 += tmpLeft0 * tmpRight4;
            tmp14 += tmpLeft1 * tmpRight4;
            tmp24 += tmpLeft2 * tmpRight4;
            tmp34 += tmpLeft3 * tmpRight4;
            tmp44 += tmpLeft4 * tmpRight4;
        }

        product[0] = tmp00;
        product[1] = tmp10;
        product[2] = tmp20;
        product[3] = tmp30;
        product[4] = tmp40;
        product[5] = tmp01;
        product[6] = tmp11;
        product[7] = tmp21;
        product[8] = tmp31;
        product[9] = tmp41;
        product[10] = tmp02;
        product[11] = tmp12;
        product[12] = tmp22;
        product[13] = tmp32;
        product[14] = tmp42;
        product[15] = tmp03;
        product[16] = tmp13;
        product[17] = tmp23;
        product[18] = tmp33;
        product[19] = tmp43;
        product[20] = tmp04;
        product[21] = tmp14;
        product[22] = tmp24;
        product[23] = tmp34;
        product[24] = tmp44;
    }

    static void fill6xN( double[] product,  double[] left,  int complexity,  double[] right) {

        int tmpRowDim = 6;
        int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            @Var double tmp0J = PrimitiveMath.ZERO;
            @Var double tmp1J = PrimitiveMath.ZERO;
            @Var double tmp2J = PrimitiveMath.ZERO;
            @Var double tmp3J = PrimitiveMath.ZERO;
            @Var double tmp4J = PrimitiveMath.ZERO;
            @Var double tmp5J = PrimitiveMath.ZERO;

            @Var int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                double tmpRightCJ = right[c + j * complexity];
                tmp0J += left[tmpIndex++] * tmpRightCJ;
                tmp1J += left[tmpIndex++] * tmpRightCJ;
                tmp2J += left[tmpIndex++] * tmpRightCJ;
                tmp3J += left[tmpIndex++] * tmpRightCJ;
                tmp4J += left[tmpIndex++] * tmpRightCJ;
                tmp5J += left[tmpIndex++] * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
        }
    }

    static void fill7xN( double[] product,  double[] left,  int complexity,  double[] right) {

        int tmpRowDim = 7;
        int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            @Var double tmp0J = PrimitiveMath.ZERO;
            @Var double tmp1J = PrimitiveMath.ZERO;
            @Var double tmp2J = PrimitiveMath.ZERO;
            @Var double tmp3J = PrimitiveMath.ZERO;
            @Var double tmp4J = PrimitiveMath.ZERO;
            @Var double tmp5J = PrimitiveMath.ZERO;
            @Var double tmp6J = PrimitiveMath.ZERO;

            @Var int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                double tmpRightCJ = right[c + j * complexity];
                tmp0J += left[tmpIndex++] * tmpRightCJ;
                tmp1J += left[tmpIndex++] * tmpRightCJ;
                tmp2J += left[tmpIndex++] * tmpRightCJ;
                tmp3J += left[tmpIndex++] * tmpRightCJ;
                tmp4J += left[tmpIndex++] * tmpRightCJ;
                tmp5J += left[tmpIndex++] * tmpRightCJ;
                tmp6J += left[tmpIndex++] * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
        }
    }

    static void fill8xN( double[] product,  double[] left,  int complexity,  double[] right) {

        int tmpRowDim = 8;
        int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            @Var double tmp0J = PrimitiveMath.ZERO;
            @Var double tmp1J = PrimitiveMath.ZERO;
            @Var double tmp2J = PrimitiveMath.ZERO;
            @Var double tmp3J = PrimitiveMath.ZERO;
            @Var double tmp4J = PrimitiveMath.ZERO;
            @Var double tmp5J = PrimitiveMath.ZERO;
            @Var double tmp6J = PrimitiveMath.ZERO;
            @Var double tmp7J = PrimitiveMath.ZERO;

            @Var int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                double tmpRightCJ = right[c + j * complexity];
                tmp0J += left[tmpIndex++] * tmpRightCJ;
                tmp1J += left[tmpIndex++] * tmpRightCJ;
                tmp2J += left[tmpIndex++] * tmpRightCJ;
                tmp3J += left[tmpIndex++] * tmpRightCJ;
                tmp4J += left[tmpIndex++] * tmpRightCJ;
                tmp5J += left[tmpIndex++] * tmpRightCJ;
                tmp6J += left[tmpIndex++] * tmpRightCJ;
                tmp7J += left[tmpIndex++] * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
        }
    }

    static void fill9xN( double[] product,  double[] left,  int complexity,  double[] right) {

        int tmpRowDim = 9;
        int tmpColDim = right.length / complexity;

        for (int j = 0; j < tmpColDim; j++) {

            @Var double tmp0J = PrimitiveMath.ZERO;
            @Var double tmp1J = PrimitiveMath.ZERO;
            @Var double tmp2J = PrimitiveMath.ZERO;
            @Var double tmp3J = PrimitiveMath.ZERO;
            @Var double tmp4J = PrimitiveMath.ZERO;
            @Var double tmp5J = PrimitiveMath.ZERO;
            @Var double tmp6J = PrimitiveMath.ZERO;
            @Var double tmp7J = PrimitiveMath.ZERO;
            @Var double tmp8J = PrimitiveMath.ZERO;

            @Var int tmpIndex = 0;
            for (int c = 0; c < complexity; c++) {
                double tmpRightCJ = right[c + j * complexity];
                tmp0J += left[tmpIndex++] * tmpRightCJ;
                tmp1J += left[tmpIndex++] * tmpRightCJ;
                tmp2J += left[tmpIndex++] * tmpRightCJ;
                tmp3J += left[tmpIndex++] * tmpRightCJ;
                tmp4J += left[tmpIndex++] * tmpRightCJ;
                tmp5J += left[tmpIndex++] * tmpRightCJ;
                tmp6J += left[tmpIndex++] * tmpRightCJ;
                tmp7J += left[tmpIndex++] * tmpRightCJ;
                tmp8J += left[tmpIndex++] * tmpRightCJ;
            }

            product[tmpIndex = j * tmpRowDim] = tmp0J;
            product[++tmpIndex] = tmp1J;
            product[++tmpIndex] = tmp2J;
            product[++tmpIndex] = tmp3J;
            product[++tmpIndex] = tmp4J;
            product[++tmpIndex] = tmp5J;
            product[++tmpIndex] = tmp6J;
            product[++tmpIndex] = tmp7J;
            product[++tmpIndex] = tmp8J;
        }
    }

    static void fillMx1( double[] product,  double[] left,  int complexity,  double[] right) {

        Arrays.fill(product, 0D);

        MultiplyNeither.addMx1(product, left, complexity, right);
    }

    static void fillMx1( float[] product,  float[] left,  int complexity,  float[] right) {

        Arrays.fill(product, 0F);

        MultiplyNeither.addMx1(product, left, complexity, right);
    }

    static <N extends Scalar<N>> void fillMx1( N[] product,  N[] left,  int complexity,  N[] right,  Scalar.Factory<N> scalar) {

        Arrays.fill(product, scalar.zero().get());

        MultiplyNeither.addMx1(product, left, complexity, right);
    }

    static void fillMxN( double[] product,  double[] left,  int complexity,  double[] right) {

        Arrays.fill(product, 0D);

        MultiplyNeither.addMxC(product, 0, right.length / complexity, left, complexity, right);
    }

    static void fillMxN( float[] product,  float[] left,  int complexity,  float[] right) {

        Arrays.fill(product, 0F);

        MultiplyNeither.addMxC(product, 0, right.length / complexity, left, complexity, right);
    }

    static <N extends Scalar<N>> void fillMxN( N[] product,  N[] left,  int complexity,  N[] right,  Scalar.Factory<N> scalar) {

        Arrays.fill(product, scalar.zero().get());

        MultiplyNeither.addMxC(product, 0, right.length / complexity, left, complexity, right);
    }

    static void fillMxN_MT( double[] product,  double[] left,  int complexity,  double[] right) {

        Arrays.fill(product, 0D);

        MultiplyNeither.addMxN_MT(product, left, complexity, right);
    }

    static void fillMxN_MT( float[] product,  float[] left,  int complexity,  float[] right) {

        Arrays.fill(product, 0F);

        MultiplyNeither.addMxN_MT(product, left, complexity, right);
    }

    static <N extends Scalar<N>> void fillMxN_MT( N[] product,  N[] left,  int complexity,  N[] right,  Scalar.Factory<N> scalar) {

        Arrays.fill(product, scalar.zero().get());

        MultiplyNeither.addMxN_MT(product, left, complexity, right);
    }

}
