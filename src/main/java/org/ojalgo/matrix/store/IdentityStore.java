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
import org.ojalgo.structure.Access2D;

/**
 * IdentityStore
 *
 * @author apete
 */
final class IdentityStore<N extends Comparable<N>> extends FactoryStore<N> {

    IdentityStore( PhysicalStore.Factory<N, ?> factory,  int dimension) {
        super(factory, dimension, dimension);
    }

    IdentityStore( PhysicalStore.Factory<N, ?> factory,  long dimension) {
        super(factory, dimension, dimension);
    }

    @Override
    public MatrixStore<N> conjugate() {
        return this;
    }

    @Override public double doubleValue( long aRow,  long aCol) {
        if (aRow == aCol) {
            return PrimitiveMath.ONE;
        }
        return PrimitiveMath.ZERO;
    }

    @Override public int firstInColumn( int col) {
        return col;
    }

    @Override public int firstInRow( int row) {
        return row;
    }

    @Override public N get( long aRow,  long aCol) {
        if (aRow == aCol) {
            return this.one().get();
        }
        return this.zero().get();
    }

    @Override
    public int limitOfColumn( int col) {
        return col + 1;
    }

    @Override
    public int limitOfRow( int row) {
        return row + 1;
    }

    @Override @SuppressWarnings("unchecked")
    public void multiply( Access1D<N> right,  TransformableRegion<N> target) {
        if (right instanceof Access2D.Collectable) {
            ((Access2D.Collectable<N, TransformableRegion<N>>) right).supplyTo(target);
        } else {
            super.multiply(right, target);
        }
    }

    @Override public MatrixStore<N> multiply( double scalar) {

         SparseStore<N> retVal = SparseStore.makeSparse(this.physical(), this);

        retVal.fillDiagonal(this.physical().scalar().cast(scalar));

        return retVal;
    }

    @Override
    public MatrixStore<N> multiply( MatrixStore<N> right) {
        return right.copy();
    }

    @Override public MatrixStore<N> multiply( N scalar) {

         SparseStore<N> retVal = SparseStore.makeSparse(this.physical(), this);

        retVal.fillDiagonal(scalar);

        return retVal;
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

    @Override public void supplyTo( TransformableRegion<N> receiver) {

        receiver.reset();

        receiver.fillDiagonal(this.one().get());
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        if (row == column) {
            return this.one();
        }
        return this.zero();
    }

    @Override
    public MatrixStore<N> transpose() {
        return this;
    }

}
