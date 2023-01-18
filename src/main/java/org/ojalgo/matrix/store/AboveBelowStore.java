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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

/**
 * A merger of two {@linkplain MatrixStore} instances by placing one store below the other. The two matrices
 * must have the same number of columns. The columns of the two matrices are logically merged to form new
 * longer columns.
 *
 * @author apete
 */
final class AboveBelowStore<N extends Comparable<N>> extends ComposingStore<N> {

    private final MatrixStore<N> myBelow;
    private final int mySplit;

    AboveBelowStore( MatrixStore<N> base,  MatrixStore<N> below) {

        super(base, base.countRows() + below.countRows(), base.countColumns());

        myBelow = below;
        mySplit = Math.toIntExact(base.countRows());

        if (base.countColumns() != below.countColumns()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long, long)
     */
    @Override public double doubleValue( long row,  long col) {
        return (row >= mySplit) ? myBelow.doubleValue(row - mySplit, col) : this.base().doubleValue(row, col);
    }

    @Override public int firstInColumn( int col) {
         int baseFirst = this.base().firstInColumn(col);
        return (baseFirst < mySplit) ? baseFirst : mySplit + myBelow.firstInColumn(col);
    }

    @Override public int firstInRow( int row) {
        return (row < mySplit) ? this.base().firstInRow(row) : myBelow.firstInRow(row - mySplit);
    }

    @Override public N get( long row,  long col) {
        return (row >= mySplit) ? myBelow.get(row - mySplit, col) : this.base().get(row, col);
    }

    @Override
    public int limitOfColumn( int col) {
         int belowLimit = myBelow.limitOfColumn(col);
        return (belowLimit == 0) ? this.base().limitOfColumn(col) : mySplit + belowLimit;
    }

    @Override
    public int limitOfRow( int row) {
        return (row < mySplit) ? this.base().limitOfRow(row) : myBelow.limitOfRow(row - mySplit);
    }

    @Override public void multiply( Access1D<N> right,  TransformableRegion<N> target) {

         Future<?> futureAbove = this.executeMultiply(right, target.regionByLimits(mySplit, this.getColDim()));

        myBelow.multiply(right, target.regionByOffsets(mySplit, 0));

        try {
            futureAbove.get();
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override public MatrixStore<N> multiply( double scalar) {

         Future<MatrixStore<N>> futureAbove = this.executeMultiply(scalar);

         MatrixStore<N> below = myBelow.multiply(scalar);

        try {
            return new AboveBelowStore<>(futureAbove.get(), below);
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public MatrixStore<N> multiply( MatrixStore<N> right) {

         Future<MatrixStore<N>> futureAbove = this.executeMultiply(right);

         MatrixStore<N> below = myBelow.multiply(right);

        try {
            return new AboveBelowStore<>(futureAbove.get(), below);
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override public MatrixStore<N> multiply( N scalar) {

         Future<MatrixStore<N>> futureAbove = this.executeMultiply(scalar);

         MatrixStore<N> below = myBelow.multiply(scalar);

        try {
            return new AboveBelowStore<>(futureAbove.get(), below);
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public N multiplyBoth( Access1D<N> leftAndRight) {
        // TODO Auto-generated method stub
        return super.multiplyBoth(leftAndRight);
    }

    @Override public ElementsSupplier<N> premultiply( Access1D<N> left) {
        // TODO Auto-generated method stub
        return super.premultiply(left);
    }

    @Override
    public void supplyTo( TransformableRegion<N> receiver) {
        this.base().supplyTo(receiver.regionByLimits(mySplit, this.getColDim()));
        myBelow.supplyTo(receiver.regionByOffsets(mySplit, 0));
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        return (row >= mySplit) ? myBelow.toScalar(row - mySplit, column) : this.base().toScalar(row, column);
    }

}
