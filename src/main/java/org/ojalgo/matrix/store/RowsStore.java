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

/**
 * A selection (re-ordering) of rows.
 *
 * @author apete
 */
final class RowsStore<N extends Comparable<N>> extends SelectingStore<N> {

    private final int[] myRows;

    RowsStore( MatrixStore<N> base,  int... rows) {

        super(base, rows.length, (int) base.countColumns());

        myRows = rows;
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long, long)
     */
    @Override public double doubleValue( long row,  long col) {
        int rowIndex = this.toBaseIndex(row);
        if (rowIndex >= 0) {
            return this.base().doubleValue(rowIndex, col);
        } else {
            return PrimitiveMath.ZERO;
        }
    }

    @Override public int firstInRow( int row) {
        int rowIndex = this.toBaseIndex(row);
        if (rowIndex >= 0) {
            return this.base().firstInRow(rowIndex);
        } else {
            return this.getColDim();
        }
    }

    @Override public N get( long row,  long col) {
        int rowIndex = this.toBaseIndex(row);
        if (rowIndex >= 0) {
            return this.base().get(rowIndex, col);
        } else {
            return this.zero().get();
        }
    }

    @Override
    public int limitOfRow( int row) {
        int rowIndex = this.toBaseIndex(row);
        if (rowIndex >= 0) {
            return this.base().limitOfRow(rowIndex);
        } else {
            return 0;
        }
    }

    @Override public void supplyTo( TransformableRegion<N> consumer) {
         MatrixStore<N> base = this.base();
        for (int i = 0; i < myRows.length; i++) {
            int rowIndex = this.toBaseIndex(i);
            if (rowIndex >= 0) {
                consumer.fillRow(i, base.sliceRow(rowIndex));
            } else {
                consumer.fillColumn(i, this.zero().get());
            }
        }
    }

    @Override public Scalar<N> toScalar( long row,  long col) {
        int rowIndex = this.toBaseIndex(row);
        if (rowIndex >= 0) {
            return this.base().toScalar(rowIndex, col);
        } else {
            return this.zero();
        }
    }

    private int toBaseIndex( int row) {
        return myRows[row];
    }

    private int toBaseIndex( long row) {
        return myRows[Math.toIntExact(row)];
    }

}
