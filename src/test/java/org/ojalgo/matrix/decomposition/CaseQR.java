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
package org.ojalgo.matrix.decomposition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.TestUtils;
import org.ojalgo.array.Array2D;
import org.ojalgo.matrix.P20030422Case;
import org.ojalgo.matrix.Primitive64Matrix;
import org.ojalgo.matrix.Primitive64Matrix.DenseReceiver;
import org.ojalgo.matrix.RationalMatrix;
import org.ojalgo.matrix.operation.MatrixOperation;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.random.Normal;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * @author apete
 */
public class CaseQR extends MatrixDecompositionTests {

    private static final NumberContext ACCURACY = NumberContext.of(7, 6);
    private static final int DIMENSION = 4;

    /**
     * An Hermitian matrix (or self-adjoint matrix) is a square matrix with complex entries that is equal to
     * its own conjugate transpose.
     */
    private static MatrixStore<ComplexNumber> makeHermitianMatrix() {
        final PhysicalStore<ComplexNumber> tmpBase = TestUtils.makeRandomComplexStore(DIMENSION, DIMENSION);
        return tmpBase.multiply(tmpBase.conjugate());
    }

    @Override
    @BeforeEach
    public void minimiseAllBranchLimits() {
        TestUtils.minimiseAllBranchLimits();
    }

    @Test
    public void testDiagonalCase() {

        PhysicalStore<Double> tmpOriginalMatrix = Primitive64Store.FACTORY
                .rows(new double[][] { { 4.0, 3.0, 2.0, 1.0 }, { 0.0, 3.0, 2.0, 1.0 }, { 0.0, 0.0, 2.0, 1.0 }, { 0.0, 0.0, 0.0, 1.0 } });

        final QR<Double> tmpDecomp = QR.PRIMITIVE.make();
        tmpDecomp.decompose(tmpOriginalMatrix);

        if (MatrixDecompositionTests.DEBUG) {
            BasicLogger.debug("This is A", tmpOriginalMatrix);
            BasicLogger.debug("Should be I", tmpDecomp.getQ());
            BasicLogger.debug("Should be A", tmpDecomp.getR());
        }

        TestUtils.assertEquals(tmpOriginalMatrix, tmpDecomp, ACCURACY);
        // TODO Fix so that Q == I when the original A is already triangular, even for RawQR
        TestUtils.assertEquals(Primitive64Store.FACTORY.makeEye(4, 4), tmpDecomp.getQ(), ACCURACY);
        TestUtils.assertEquals(tmpOriginalMatrix, tmpDecomp.getR(), ACCURACY);
    }

    @Test
    public void testCompilation() {

        // Just want to verify that this compiles and runs without throwing exceptions

        QR<Double> tmpDecomp = QR.PRIMITIVE.make();

        Array2D<Double> a2d = Array2D.PRIMITIVE64.makeFilled(3, 3, Normal.standard());

        tmpDecomp.decompose(a2d);

        DenseReceiver dr = Primitive64Matrix.FACTORY.makeDense(3, 3);
        dr.fillAll(Normal.standard());
        tmpDecomp.decompose(dr);

        Primitive64Matrix ps = dr.get();
        tmpDecomp.decompose(ps);

        Primitive64Matrix lb = dr.get().below(2);
        tmpDecomp.decompose(lb);

        Primitive64Matrix bm = lb;
        tmpDecomp.decompose(bm);
    }

    @Test
    public void testHermitian() {

        final int tmpLim = DIMENSION - 1;
        final MatrixStore<ComplexNumber> tmpOriginal = CaseQR.makeHermitianMatrix();

        if (MatrixDecompositionTests.DEBUG) {
            BasicLogger.debug("Original", tmpOriginal);
        }

        final QR<ComplexNumber> tmpDecomposition = QR.COMPLEX.make();
        tmpDecomposition.decompose(tmpOriginal);
        final MatrixStore<ComplexNumber> tmpDecompQ = tmpDecomposition.getQ();
        final MatrixStore<ComplexNumber> tmpDecompR = tmpDecomposition.getR();

        final DecompositionStore<ComplexNumber> tmpInPlace = GenericStore.COMPLEX.copy(tmpOriginal);

        final DecompositionStore<ComplexNumber> tmpNowQ = GenericStore.COMPLEX.makeEye(DIMENSION, DIMENSION);
        final DecompositionStore<ComplexNumber> tmpNowR = GenericStore.COMPLEX.copy(tmpOriginal);

        final DecompositionStore<ComplexNumber> tmpForwardQ = GenericStore.COMPLEX.makeEye(DIMENSION, DIMENSION);
        final DecompositionStore<ComplexNumber> tmpForwardR = GenericStore.COMPLEX.copy(tmpOriginal);

        final DecompositionStore<ComplexNumber> tmpReverseQ = GenericStore.COMPLEX.makeEye(DIMENSION, DIMENSION);

        final Householder.Generic<ComplexNumber>[] tmpHouseholders = new Householder.Generic[tmpLim];

        for (int ij = 0; ij < tmpLim; ij++) {

            final Householder.Generic<ComplexNumber> tmpVector = new Householder.Generic<>(ComplexNumber.FACTORY, DIMENSION);

            if (tmpInPlace.generateApplyAndCopyHouseholderColumn(ij, ij, tmpVector)) {
                tmpInPlace.transformLeft(tmpVector, ij + 1);
                tmpNowQ.transformRight(tmpVector, 0);
                tmpNowR.transformLeft(tmpVector, ij);
            }

            tmpHouseholders[ij] = tmpVector;
        }

        for (int h = 0; h < tmpHouseholders.length; h++) {

            final Householder.Generic<ComplexNumber> tmpVector = tmpHouseholders[h];

            tmpForwardQ.transformRight(tmpVector, 0);
            tmpForwardR.transformLeft(tmpVector, h);
        }

        for (int h = tmpHouseholders.length - 1; h >= 0; h--) {

            final Householder.Generic<ComplexNumber> tmpVector = tmpHouseholders[h];

            tmpReverseQ.transformLeft(tmpVector, 0);
        }

        if (MatrixDecompositionTests.DEBUG) {
            BasicLogger.debug();
            BasicLogger.debug("Decomp Q", tmpDecompQ);
            BasicLogger.debug("Now Q", tmpNowQ);
            BasicLogger.debug("Forward Q", tmpForwardQ);
            BasicLogger.debug("Reverse Q", tmpReverseQ);
            BasicLogger.debug();
            BasicLogger.debug("Decomp R", tmpDecompR);
            BasicLogger.debug("Now R", tmpNowR);
            BasicLogger.debug("Forward R", tmpForwardR);
        }

        TestUtils.assertEquals(tmpOriginal, tmpDecomposition, ACCURACY);

        TestUtils.assertEquals(tmpDecompQ, tmpNowQ, ACCURACY);
        TestUtils.assertEquals(tmpDecompQ, tmpForwardQ, ACCURACY);
        TestUtils.assertEquals(tmpDecompQ, tmpReverseQ, ACCURACY);

        TestUtils.assertEquals(tmpDecompR, tmpNowR, ACCURACY);
        TestUtils.assertEquals(tmpDecompR, tmpForwardR, ACCURACY);
    }

    @Test
    public void testLeastSquaresInvert() {

        MatrixOperation.setThresholdsMinValue(100000);

        final int tmpDim = 3;
        final MatrixStore<Double> tmpA = Primitive64Store.FACTORY.makeSPD(tmpDim).below(Primitive64Store.FACTORY.makeIdentity(tmpDim));

        final QR<Double> tmpDenseQR = new QRDecomposition.Primitive();
        final QR<Double> tmpRawQR = new RawQR();

        final PhysicalStore<Double> tmpDenseAlloc = tmpDenseQR.preallocate(tmpA);
        final PhysicalStore<Double> tmpRawAlloc = tmpRawQR.preallocate(tmpA);

        MatrixStore<Double> tmpDenseInv;
        try {
            tmpDenseInv = tmpDenseQR.invert(tmpA, tmpDenseAlloc);
            final MatrixStore<Double> tmpRawInv = tmpRawQR.invert(tmpA, tmpRawAlloc);

            TestUtils.assertEquals(tmpDenseInv, tmpRawInv);

            final MatrixStore<Double> tmpIdentity = Primitive64Store.FACTORY.makeIdentity(tmpDim);
            TestUtils.assertEquals(tmpIdentity, tmpDenseInv.multiply(tmpA));
            TestUtils.assertEquals(tmpIdentity, tmpRawInv.multiply(tmpA));

        } catch (final RecoverableCondition anException) {
            anException.printStackTrace();
            TestUtils.fail(anException.toString());
        }

    }

    @Test
    public void testP20030422Case() {

        final RationalMatrix tmpOriginal = P20030422Case.getProblematic();

        final QR<RationalNumber> tmpBigDecomp = QR.RATIONAL.make();
        final QR<ComplexNumber> tmpComplexDecomp = QR.COMPLEX.make();
        final QR<Double> tmpPrimitiveDecomp = QR.PRIMITIVE.make();

        tmpBigDecomp.decompose(GenericStore.RATIONAL.copy(tmpOriginal));
        tmpComplexDecomp.decompose(GenericStore.COMPLEX.copy(tmpOriginal));
        tmpPrimitiveDecomp.decompose(Primitive64Store.FACTORY.copy(tmpOriginal));

        final MatrixStore<RationalNumber> tmpBigQ = tmpBigDecomp.getQ();
        final MatrixStore<ComplexNumber> tmpComplexQ = tmpComplexDecomp.getQ();
        final MatrixStore<Double> tmpPrimitiveQ = tmpPrimitiveDecomp.getQ();

        if (MatrixDecompositionTests.DEBUG) {
            BasicLogger.debug("Big Q", tmpBigQ);
            BasicLogger.debug("Complex Q", tmpComplexQ);
            BasicLogger.debug("Primitive Q", tmpPrimitiveQ);
        }

        final MatrixStore<RationalNumber> tmpBigR = tmpBigDecomp.getR();
        final MatrixStore<ComplexNumber> tmpComplexR = tmpComplexDecomp.getR();
        final MatrixStore<Double> tmpPrimitiveR = tmpPrimitiveDecomp.getR();

        if (MatrixDecompositionTests.DEBUG) {
            BasicLogger.debug("Big R", tmpBigR);
            BasicLogger.debug("Complex R", tmpComplexR);
            BasicLogger.debug("Primitive R", tmpPrimitiveR);
        }

        TestUtils.assertEquals(GenericStore.RATIONAL.copy(tmpOriginal), tmpBigDecomp, new NumberContext(7, 14));
        TestUtils.assertEquals(GenericStore.COMPLEX.copy(tmpOriginal), tmpComplexDecomp, new NumberContext(7, 14));
        TestUtils.assertEquals(Primitive64Store.FACTORY.copy(tmpOriginal), tmpPrimitiveDecomp, new NumberContext(7, 14));
    }

}
