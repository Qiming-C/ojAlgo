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

import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.LDL;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive32Store;
import org.ojalgo.matrix.store.SparseStore;
import org.ojalgo.matrix.task.DeterminantTask;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.structure.Structure2D;

/**
 * @deprecated v53 Use {@link MatrixR032} instead.
 */
@Deprecated
public final class Primitive32Matrix extends BasicMatrix<Double, Primitive32Matrix> {

    public static final class DenseReceiver extends Mutator2D<Double, Primitive32Matrix, PhysicalStore<Double>> {

        DenseReceiver( PhysicalStore<Double> delegate) {
            super(delegate);
        }

        @Override
        Primitive32Matrix instantiate( MatrixStore<Double> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final class Factory extends MatrixFactory<Double, Primitive32Matrix, Primitive32Matrix.DenseReceiver, Primitive32Matrix.SparseReceiver> {

        Factory() {
            super(Primitive32Matrix.class, Primitive32Store.FACTORY);
        }

        @Override
        Primitive32Matrix.DenseReceiver dense( PhysicalStore<Double> store) {
            return new Primitive32Matrix.DenseReceiver(store);
        }

        @Override
        Primitive32Matrix.SparseReceiver sparse( SparseStore<Double> store) {
            return new Primitive32Matrix.SparseReceiver(store);
        }

    }

    public static final class SparseReceiver extends Mutator2D<Double, Primitive32Matrix, SparseStore<Double>> {

        SparseReceiver( SparseStore<Double> delegate) {
            super(delegate);
        }

        @Override
        Primitive32Matrix instantiate( MatrixStore<Double> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final Factory FACTORY = new Factory();

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    Primitive32Matrix( ElementsSupplier<Double> supplier) {
        super(FACTORY.getPhysicalFactory(), supplier);
    }

    @Override
    public Primitive32Matrix.DenseReceiver copy() {
        return new Primitive32Matrix.DenseReceiver(this.store().copy());
    }

    @Override
    Cholesky<Double> newCholesky( Structure2D typical) {
        return Cholesky.PRIMITIVE.make(typical);
    }

    @Override
    DeterminantTask<Double> newDeterminantTask( Structure2D template) {
        return DeterminantTask.PRIMITIVE.make(template, this.isHermitian(), false);
    }

    @Override
    Eigenvalue<Double> newEigenvalue( Structure2D typical) {
        return Eigenvalue.PRIMITIVE.make(typical, this.isHermitian());
    }

    @Override
    Primitive32Matrix newInstance( ElementsSupplier<Double> store) {
        return new Primitive32Matrix(store);
    }

    @Override
    InverterTask<Double> newInverterTask( Structure2D base) {
        return InverterTask.PRIMITIVE.make(base, this.isHermitian(), false);
    }

    @Override
    LDL<Double> newLDL( Structure2D typical) {
        return LDL.PRIMITIVE.make(typical);
    }

    @Override
    LU<Double> newLU( Structure2D typical) {
        return LU.PRIMITIVE.make(typical);
    }

    @Override
    QR<Double> newQR( Structure2D typical) {
        return QR.PRIMITIVE.make(typical);
    }

    @Override
    SingularValue<Double> newSingularValue( Structure2D typical) {
        return SingularValue.PRIMITIVE.make(typical);
    }

    @Override
    SolverTask<Double> newSolverTask( Structure2D templateBody,  Structure2D templateRHS) {
        return SolverTask.PRIMITIVE.make(templateBody, templateRHS, this.isHermitian(), false);
    }

}
