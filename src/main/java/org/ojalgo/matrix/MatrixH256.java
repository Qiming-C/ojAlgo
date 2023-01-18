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
package org.ojalgo.matrix;

import org.ojalgo.algebra.NumberSet;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.LDL;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.SparseStore;
import org.ojalgo.matrix.task.DeterminantTask;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.structure.Structure2D;

/**
 * A matrix (linear algebra) with Quaternion {@link NumberSet#H} elements, implemented using four 64-bit
 * double values. (4 x 64 = 256)
 *
 * @see BasicMatrix
 * @see Quaternion
 * @author apete
 */
public final class MatrixH256 extends BasicMatrix<Quaternion, MatrixH256> {

    public static final class DenseReceiver extends Mutator2D<Quaternion, MatrixH256, PhysicalStore<Quaternion>> {

        DenseReceiver( PhysicalStore<Quaternion> delegate) {
            super(delegate);
        }

        @Override
        MatrixH256 instantiate( MatrixStore<Quaternion> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final class Factory extends MatrixFactory<Quaternion, MatrixH256, MatrixH256.DenseReceiver, MatrixH256.SparseReceiver> {

        Factory() {
            super(MatrixH256.class, GenericStore.H256);
        }

        @Override
        MatrixH256.DenseReceiver dense( PhysicalStore<Quaternion> store) {
            return new MatrixH256.DenseReceiver(store);
        }

        @Override
        MatrixH256.SparseReceiver sparse( SparseStore<Quaternion> store) {
            return new MatrixH256.SparseReceiver(store);
        }

    }

    public static final class SparseReceiver extends Mutator2D<Quaternion, MatrixH256, SparseStore<Quaternion>> {

        SparseReceiver( SparseStore<Quaternion> delegate) {
            super(delegate);
        }

        @Override
        MatrixH256 instantiate( MatrixStore<Quaternion> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final Factory FACTORY = new Factory();

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    MatrixH256( ElementsSupplier<Quaternion> supplier) {
        super(FACTORY.getPhysicalFactory(), supplier);
    }

    @Override
    public MatrixH256.DenseReceiver copy() {
        return new MatrixH256.DenseReceiver(this.store().copy());
    }

    @Override
    Cholesky<Quaternion> newCholesky( Structure2D typical) {
        return Cholesky.QUATERNION.make(typical);
    }

    @Override
    DeterminantTask<Quaternion> newDeterminantTask( Structure2D template) {
        return DeterminantTask.QUATERNION.make(template, this.isHermitian(), false);
    }

    @Override
    Eigenvalue<Quaternion> newEigenvalue( Structure2D typical) {
        return Eigenvalue.QUATERNION.make(typical, this.isHermitian());
    }

    @Override
    MatrixH256 newInstance( ElementsSupplier<Quaternion> store) {
        return new MatrixH256(store);
    }

    @Override
    InverterTask<Quaternion> newInverterTask( Structure2D template) {
        return InverterTask.QUATERNION.make(template, this.isHermitian(), false);
    }

    @Override
    LDL<Quaternion> newLDL( Structure2D typical) {
        return LDL.QUATERNION.make(typical);
    }

    @Override
    LU<Quaternion> newLU( Structure2D typical) {
        return LU.QUATERNION.make(typical);
    }

    @Override
    QR<Quaternion> newQR( Structure2D typical) {
        return QR.QUATERNION.make(typical);
    }

    @Override
    SingularValue<Quaternion> newSingularValue( Structure2D typical) {
        return SingularValue.QUATERNION.make(typical);
    }

    @Override
    SolverTask<Quaternion> newSolverTask( Structure2D templateBody,  Structure2D templateRHS) {
        return SolverTask.QUATERNION.make(templateBody, templateRHS, this.isHermitian(), false);
    }

}
