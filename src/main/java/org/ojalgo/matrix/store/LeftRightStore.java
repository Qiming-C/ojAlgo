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
 * A merger of two {@linkplain MatrixStore} instances by placing one store to the right of the other. The two
 * matrices must have the same number of rows. The rows of the two matrices are logically merged to form new
 * longer rows.
 *
 * @author apete
 */
final class LeftRightStore<N extends Comparable<N>> extends ComposingStore<N> {

    private final MatrixStore<N> myRight;
    private final int mySplit;

    LeftRightStore( MatrixStore<N> base,  MatrixStore<N> right) {

        super(base, base.countRows(), base.countColumns() + right.countColumns());

        myRight = right;
        mySplit = Math.toIntExact(base.countColumns());

        if (base.countRows() != right.countRows()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long, long)
     */
    @Override public double doubleValue( long row,  long col) {
        return (col >= mySplit) ? myRight.doubleValue(row, col - mySplit) : this.base().doubleValue(row, col);
    }

    @Override public int firstInColumn( int col) {
        return (col < mySplit) ? this.base().firstInColumn(col) : myRight.firstInColumn(col - mySplit);
    }

    @Override public int firstInRow( int row) {
         int baseFirst = this.base().firstInRow(row);
        return (baseFirst < mySplit) ? baseFirst : mySplit + myRight.firstInRow(row);
    }

    @Override public N get( long row,  long col) {
        return (col >= mySplit) ? myRight.get(row, col - mySplit) : this.base().get(row, col);
    }

    @Override
    public int limitOfColumn( int col) {
        return (col < mySplit) ? this.base().limitOfColumn(col) : myRight.limitOfColumn(col - mySplit);
    }

    @Override
    public int limitOfRow( int row) {
         int rightLimit = myRight.limitOfRow(row);
        return rightLimit == 0 ? this.base().limitOfRow(row) : mySplit + rightLimit;
    }

    @Override public void multiply( Access1D<N> right,  TransformableRegion<N> target) {
        // TODO Auto-generated method stub
        super.multiply(right, target);
    }

    @Override public MatrixStore<N> multiply( double scalar) {

         Future<MatrixStore<N>> futureLeft = this.executeMultiply(scalar);

         MatrixStore<N> right = myRight.multiply(scalar);

        try {
            return new LeftRightStore<>(futureLeft.get(), right);
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override public MatrixStore<N> multiply( MatrixStore<N> right) {
        // TODO Auto-generated method stub
        return super.multiply(right);
    }

    @Override public MatrixStore<N> multiply( N scalar) {

         Future<MatrixStore<N>> futureLeft = this.executeMultiply(scalar);

         MatrixStore<N> right = myRight.multiply(scalar);

        try {
            return new LeftRightStore<>(futureLeft.get(), right);
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

    @Override public MatrixStore<N> premultiply( Access1D<N> left) {

         Future<ElementsSupplier<N>> futureLeft = this.executePremultiply(left);

         MatrixStore<N> right = myRight.premultiply(left).collect(this.physical());

        try {
            return new LeftRightStore<>(futureLeft.get().collect(this.physical()), right);
        } catch ( InterruptedException | ExecutionException ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public void supplyTo( TransformableRegion<N> receiver) {
        this.base().supplyTo(receiver.regionByLimits(this.getRowDim(), mySplit));
        myRight.supplyTo(receiver.regionByOffsets(0, mySplit));
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        return (column >= mySplit) ? myRight.toScalar(row, column - mySplit) : this.base().toScalar(row, column);
    }

}
