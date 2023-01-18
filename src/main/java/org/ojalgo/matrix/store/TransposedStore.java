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

import com.google.errorprone.annotations.Var;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

final class TransposedStore<N extends Comparable<N>> extends TransjugatedStore<N> {

    TransposedStore( MatrixStore<N> base) {
        super(base);
    }

    @Override public N aggregateColumn( long col,  Aggregator aggregator) {
        return this.base().aggregateRow(col, aggregator);
    }

    @Override public N aggregateRow( long row,  Aggregator aggregator) {
        return this.base().aggregateColumn(row, aggregator);
    }

    @Override public N get( long aRow,  long aCol) {
        return this.base().get(aCol, aRow);
    }

    @Override public void loopColumn( long col,  RowColumnCallback callback) {
        this.base().loopRow(col, callback);
    }

    @Override public void loopRow( long row,  RowColumnCallback callback) {
        this.base().loopColumn(row, callback);
    }

    @Override public MatrixStore<N> multiply( MatrixStore<N> right) {

        @Var MatrixStore<N> retVal;

        if (right instanceof TransposedStore<?>) {

            retVal = ((TransposedStore<N>) right).getOriginal().multiply(this.base());

            retVal = new TransposedStore<>(retVal);

        } else {

            retVal = super.multiply(right);
        }

        return retVal;
    }

    @Override public Access1D<N> sliceColumn( long col) {
        return this.base().sliceRow(col);
    }

    @Override public Access1D<N> sliceRow( long row) {
        return this.base().sliceColumn(row);
    }

    @Override
    public void supplyTo( TransformableRegion<N> receiver) {
        this.base().supplyTo(receiver.regionByTransposing());
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        return this.base().toScalar(column, row);
    }

    @Override
    public MatrixStore<N> transpose() {
        return this.base();
    }

    @Override public void visitColumn( long col,  VoidFunction<N> visitor) {
        this.base().visitRow(col, visitor);
    }

    @Override public void visitRow( long row,  VoidFunction<N> visitor) {
        this.base().visitColumn(row, visitor);
    }

}
