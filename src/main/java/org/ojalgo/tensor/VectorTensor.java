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
import org.ojalgo.array.Array1D;
import org.ojalgo.array.DenseArray;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Factory1D;
import org.ojalgo.structure.Mutate1D;

public final class VectorTensor<N extends Comparable<N>> extends ArrayBasedTensor<N, VectorTensor<N>> implements Access1D<N>, Mutate1D.Receiver<N> {

    static final class Factory<N extends Comparable<N>> extends ArrayBasedTensor.Factory<N> implements Factory1D<VectorTensor<N>> {

        private final Array1D.Factory<N> myFactory;

        Factory( DenseArray.Factory<N> arrayFactory) {

            super(arrayFactory);

            myFactory = Array1D.factory(arrayFactory);
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

        @Override public VectorTensor<N> make( long count) {
            return new VectorTensor<>(myFactory, Math.toIntExact(count));
        }

    }

    public static <N extends Comparable<N>> TensorFactory1D<N, VectorTensor<N>> factory( DenseArray.Factory<N> arrayFactory) {
        return new TensorFactory1D<>(new VectorTensor.Factory<>(arrayFactory));
    }

    private final Array1D<N> myArray;
    private final Array1D.Factory<N> myFactory;

    VectorTensor( Array1D.Factory<N> factory,  int dimensions) {

        super(1, dimensions, factory.function(), factory.scalar());

        myFactory = factory;
        myArray = factory.make(dimensions);
    }

    @Override public VectorTensor<N> add( VectorTensor<N> addend) {

        VectorTensor<N> retVal = this.newSameShape();

        this.add(retVal.getArray(), myArray, addend);

        return retVal;
    }

    @Override public byte byteValue( long index) {
        return myArray.byteValue(index);
    }

    @Override public VectorTensor<N> conjugate() {
        return this;
    }

    @Override public long count() {
        return myArray.count();
    }

    @Override public double doubleValue( long index) {
        return myArray.doubleValue(index);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof VectorTensor)) {
            return false;
        }
        var other = (VectorTensor) obj;
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

    @Override public float floatValue( long index) {
        return myArray.floatValue(index);
    }

    @Override public N get( long index) {
        return myArray.get(index);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + (myArray == null ? 0 : myArray.hashCode());
        return prime * result + (myFactory == null ? 0 : myFactory.hashCode());
    }

    @Override public int intValue( long index) {
        return myArray.intValue(index);
    }

    @Override public long longValue( long index) {
        return myArray.longValue(index);
    }

    @Override public VectorTensor<N> multiply( double scalarMultiplicand) {

        VectorTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public VectorTensor<N> multiply( N scalarMultiplicand) {

        VectorTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public VectorTensor<N> negate() {

        VectorTensor<N> retVal = this.newSameShape();

        this.negate(retVal.getArray(), myArray);

        return retVal;
    }

    @Override public double norm() {
        return this.norm(myArray);
    }

    @Override public void set( long index,  byte value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  Comparable<?> value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  double value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  float value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  int value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  long value) {
        myArray.set(index, value);
    }

    @Override public void set( long index,  short value) {
        myArray.set(index, value);
    }

    @Override public short shortValue( long index) {
        return myArray.shortValue(index);
    }

    @Override
    public String toString() {
        return Access1D.toString(myArray);
    }

    Array1D<N> getArray() {
        return myArray;
    }

    @Override
    VectorTensor<N> newSameShape() {
        return new VectorTensor<>(myFactory, this.dimensions());
    }

}
