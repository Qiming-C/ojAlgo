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
package org.ojalgo.matrix.store;

import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

/**
 * ZeroStore
 *
 * @author apete
 */
final class ZeroStore<N extends Comparable<N>> extends FactoryStore<N> {

    ZeroStore( PhysicalStore.Factory<N, ?> factory,  int rowsCount,  int columnsCount) {
        super(factory, rowsCount, columnsCount);
    }

    ZeroStore( PhysicalStore.Factory<N, ?> factory,  long rowsCount,  long columnsCount) {
        super(factory, rowsCount, columnsCount);
    }

    @Override
    public MatrixStore<N> add( MatrixStore<N> addend) {
        return addend;
    }

    @Override
    public MatrixStore<N> conjugate() {
        return new ZeroStore<>(this.physical(), this.getColDim(), this.getRowDim());
    }

    @Override
    public double doubleValue( long anInd) {
        return PrimitiveMath.ZERO;
    }

    @Override public double doubleValue( long aRow,  long aCol) {
        return PrimitiveMath.ZERO;
    }

    @Override public int firstInColumn( int col) {
        return this.getRowDim();
    }

    @Override public int firstInRow( int row) {
        return this.getColDim();
    }

    @Override public N get( long aRow,  long aCol) {
        return this.zero().get();
    }

    @Override
    public int limitOfColumn( int col) {
        return 0;
    }

    @Override
    public int limitOfRow( int row) {
        return 0;
    }

    @Override public void multiply( Access1D<N> right,  TransformableRegion<N> target) {
        target.reset();
    }

    @Override public ZeroStore<N> multiply( double scalar) {
        return new ZeroStore<>(this.physical(), this.getRowDim(), this.getColDim());
    }

    @Override
    public ZeroStore<N> multiply( MatrixStore<N> right) {
        return new ZeroStore<>(this.physical(), this.getRowDim(), (int) (right.count() / this.getColDim()));
    }

    @Override public ZeroStore<N> multiply( N scalar) {
        return new ZeroStore<>(this.physical(), this.getRowDim(), this.getColDim());
    }

    @Override
    public N multiplyBoth( Access1D<N> leftAndRight) {
        return this.zero().get();
    }

    @Override public ZeroStore<N> premultiply( Access1D<N> left) {
        return new ZeroStore<>(this.physical(), (int) (left.count() / this.getRowDim()), this.getColDim());
    }

    @Override public void supplyTo( TransformableRegion<N> receiver) {
        receiver.reset();
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        return this.zero();
    }

    @Override
    public MatrixStore<N> transpose() {
        return new ZeroStore<>(this.physical(), this.getColDim(), this.getRowDim());
    }

}
