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
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.store.SparseStore;
import org.ojalgo.matrix.task.DeterminantTask;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.structure.Structure2D;

/**
 * @deprecated v53 Use {@link MatrixC128} instead.
 */
@Deprecated
public final class ComplexMatrix extends BasicMatrix<ComplexNumber, ComplexMatrix> {

    public static final class DenseReceiver extends Mutator2D<ComplexNumber, ComplexMatrix, PhysicalStore<ComplexNumber>> {

        DenseReceiver( PhysicalStore<ComplexNumber> delegate) {
            super(delegate);
        }

        @Override
        ComplexMatrix instantiate( MatrixStore<ComplexNumber> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final class Factory extends MatrixFactory<ComplexNumber, ComplexMatrix, ComplexMatrix.DenseReceiver, ComplexMatrix.SparseReceiver> {

        Factory() {
            super(ComplexMatrix.class, GenericStore.C128);
        }

        @Override
        ComplexMatrix.DenseReceiver dense( PhysicalStore<ComplexNumber> store) {
            return new ComplexMatrix.DenseReceiver(store);
        }

        @Override
        ComplexMatrix.SparseReceiver sparse( SparseStore<ComplexNumber> store) {
            return new ComplexMatrix.SparseReceiver(store);
        }

    }

    public static final class SparseReceiver extends Mutator2D<ComplexNumber, ComplexMatrix, SparseStore<ComplexNumber>> {

        SparseReceiver( SparseStore<ComplexNumber> delegate) {
            super(delegate);
        }

        @Override
        ComplexMatrix instantiate( MatrixStore<ComplexNumber> store) {
            return FACTORY.instantiate(store);
        }

    }

    public static final Factory FACTORY = new Factory();

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    ComplexMatrix( ElementsSupplier<ComplexNumber> supplier) {
        super(FACTORY.getPhysicalFactory(), supplier);
    }

    @Override
    public ComplexMatrix.DenseReceiver copy() {
        return new ComplexMatrix.DenseReceiver(this.store().copy());
    }

    /**
     *Returns a primitive double valued matrix containg this matrix' element arguments.
 
     */
    public Primitive64Matrix getArgument() {
        return Primitive64Matrix.FACTORY.instantiate(Primitive64Store.getComplexArgument(this.store()));
    }

    /**
     *Returns a primitive double valued matrix containg this matrix' element imaginary parts.
 
     */
    public Primitive64Matrix getImaginary() {
        return Primitive64Matrix.FACTORY.instantiate(Primitive64Store.getComplexImaginary(this.store()));
    }

    /**
     *Returns a primitive double valued matrix containg this matrix' element modulus.
 
     */
    public Primitive64Matrix getModulus() {
        return Primitive64Matrix.FACTORY.instantiate(Primitive64Store.getComplexModulus(this.store()));
    }

    /**
     *Returns a primitive double valued matrix containg this matrix' element real parts.
 
     */
    public Primitive64Matrix getReal() {
        return Primitive64Matrix.FACTORY.instantiate(Primitive64Store.getComplexReal(this.store()));
    }

    @Override
    Cholesky<ComplexNumber> newCholesky( Structure2D typical) {
        return Cholesky.COMPLEX.make(typical);
    }

    @Override
    DeterminantTask<ComplexNumber> newDeterminantTask( Structure2D template) {
        return DeterminantTask.COMPLEX.make(template, this.isHermitian(), false);
    }

    @Override
    Eigenvalue<ComplexNumber> newEigenvalue( Structure2D typical) {
        return Eigenvalue.COMPLEX.make(typical, this.isHermitian());
    }

    @Override
    ComplexMatrix newInstance( ElementsSupplier<ComplexNumber> store) {
        return new ComplexMatrix(store);
    }

    @Override
    InverterTask<ComplexNumber> newInverterTask( Structure2D base) {
        return InverterTask.COMPLEX.make(base, this.isHermitian(), false);
    }

    @Override
    LDL<ComplexNumber> newLDL( Structure2D typical) {
        return LDL.COMPLEX.make(typical);
    }

    @Override
    LU<ComplexNumber> newLU( Structure2D typical) {
        return LU.COMPLEX.make(typical);
    }

    @Override
    QR<ComplexNumber> newQR( Structure2D typical) {
        return QR.COMPLEX.make(typical);
    }

    @Override
    SingularValue<ComplexNumber> newSingularValue( Structure2D typical) {
        return SingularValue.COMPLEX.make(typical);
    }

    @Override
    SolverTask<ComplexNumber> newSolverTask( Structure2D templateBody,  Structure2D templateRHS) {
        return SolverTask.COMPLEX.make(templateBody, templateRHS, this.isHermitian(), false);
    }

}
