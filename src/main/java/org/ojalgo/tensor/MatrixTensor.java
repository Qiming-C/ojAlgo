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
package org.ojalgo.tensor;

import com.google.errorprone.annotations.Var;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.DenseArray;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Factory2D;
import org.ojalgo.structure.Mutate2D;

public final class MatrixTensor<N extends Comparable<N>> extends ArrayBasedTensor<N, MatrixTensor<N>> implements Access2D<N>, Mutate2D.Receiver<N> {

    static final class Factory<N extends Comparable<N>> extends ArrayBasedTensor.Factory<N> implements Factory2D<MatrixTensor<N>> {

        private final Array2D.Factory<N> myFactory;

        Factory( DenseArray.Factory<N> arrayFactory) {

            super(arrayFactory);

            myFactory = Array2D.factory(arrayFactory);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj) || !(obj instanceof Factory)) {
                return false;
            }
            var other = (Factory) obj;
            if (myFactory == null) {
                if (other.myFactory != null) {
                    return false;
                }
            } else if (!myFactory.equals(other.myFactory)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            int result = super.hashCode();
            return prime * result + (myFactory == null ? 0 : myFactory.hashCode());
        }

        @Override public MatrixTensor<N> make( long rows,  long columns) {
            if (rows != columns) {
                throw new IllegalArgumentException();
            }
            return new MatrixTensor<>(myFactory, Math.toIntExact(rows));
        }

    }

    public static <N extends Comparable<N>> TensorFactory2D<N, MatrixTensor<N>> factory( DenseArray.Factory<N> arrayFactory) {
        return new TensorFactory2D<>(new MatrixTensor.Factory<>(arrayFactory));
    }

    private final Array2D<N> myArray;
    private final Array2D.Factory<N> myFactory;

    MatrixTensor( Array2D.Factory<N> factory,  int dimensions) {

        super(2, dimensions, factory.function(), factory.scalar());

        myFactory = factory;
        myArray = factory.make(dimensions, dimensions);
    }

    @Override public MatrixTensor<N> add( MatrixTensor<N> addend) {

        MatrixTensor<N> retVal = this.newSameShape();

        this.add(retVal.getArray(), myArray, addend);

        return retVal;
    }

    @Override public byte byteValue( long row,  long col) {
        return myArray.byteValue(row, col);
    }

    @Override public MatrixTensor<N> conjugate() {

        MatrixTensor<N> retVal = this.newSameShape();
        Array2D<N> array = retVal.getArray();

        for (int j = 0; j < this.dimensions(); j++) {
            for (int i = 0; i < this.dimensions(); i++) {
                array.set(i, j, myArray.get(j, i));
            }
        }

        return retVal;
    }

    @Override public long count() {
        return myArray.count();
    }

    @Override public long countColumns() {
        return myArray.countColumns();
    }

    @Override public long countRows() {
        return myArray.countRows();
    }

    @Override public double doubleValue( long row,  long col) {
        return myArray.doubleValue(row, col);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof MatrixTensor)) {
            return false;
        }
        var other = (MatrixTensor) obj;
        if (myArray == null) {
            if (other.myArray != null) {
                return false;
            }
        } else if (!myArray.equals(other.myArray)) {
            return false;
        }
        if (myFactory == null) {
            if (other.myFactory != null) {
                return false;
            }
        } else if (!myFactory.equals(other.myFactory)) {
            return false;
        }
        return true;
    }

    @Override public float floatValue( long row,  long col) {
        return myArray.floatValue(row, col);
    }

    @Override public N get( long row,  long col) {
        return myArray.get(row, col);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + (myArray == null ? 0 : myArray.hashCode());
        return prime * result + (myFactory == null ? 0 : myFactory.hashCode());
    }

    @Override public int intValue( long row,  long col) {
        return myArray.intValue(row, col);
    }

    @Override public long longValue( long row,  long col) {
        return myArray.longValue(row, col);
    }

    @Override public MatrixTensor<N> multiply( double scalarMultiplicand) {

        MatrixTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public MatrixTensor<N> multiply( N scalarMultiplicand) {

        MatrixTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public MatrixTensor<N> negate() {

        MatrixTensor<N> retVal = this.newSameShape();

        this.negate(retVal.getArray(), myArray);

        return retVal;
    }

    @Override public double norm() {
        return this.norm(myArray);
    }

    @Override public void set( long row,  long col,  byte value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  Comparable<?> value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  double value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  float value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  int value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  long value) {
        myArray.set(row, col, value);
    }

    @Override public void set( long row,  long col,  short value) {
        myArray.set(row, col, value);
    }

    @Override public short shortValue( long row,  long col) {
        return myArray.shortValue(row, col);
    }

    @Override
    public String toString() {
        return Access2D.toString(myArray);
    }

    Array2D<N> getArray() {
        return myArray;
    }

    @Override
    MatrixTensor<N> newSameShape() {
        return new MatrixTensor<>(myFactory, this.dimensions());
    }

}
