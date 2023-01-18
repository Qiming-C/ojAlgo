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
import java.util.Arrays;
import org.ojalgo.array.ArrayAnyD;
import org.ojalgo.array.DenseArray;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.structure.AccessAnyD;
import org.ojalgo.structure.FactoryAnyD;
import org.ojalgo.structure.MutateAnyD;

public final class AnyTensor<N extends Comparable<N>> extends ArrayBasedTensor<N, AnyTensor<N>> implements AccessAnyD<N>, MutateAnyD.Receiver<N> {

    static final class Factory<N extends Comparable<N>> extends ArrayBasedTensor.Factory<N> implements FactoryAnyD<AnyTensor<N>> {

        private final ArrayAnyD.Factory<N> myFactory;

        Factory( DenseArray.Factory<N> arrayFactory) {

            super(arrayFactory);

            myFactory = ArrayAnyD.factory(arrayFactory);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Factory)) {
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
            int result = 1;
            return prime * result + (myFactory == null ? 0 : myFactory.hashCode());
        }

        @Override public AnyTensor<N> make( long... structure) {

            int rank = structure.length;
            long dimensions = structure[0];

            if (rank <= 0 || dimensions <= 0L) {
                throw new IllegalArgumentException();
            }
            for (int i = 1; i < rank; i++) {
                if (structure[i] != dimensions) {
                    throw new IllegalArgumentException();
                }
            }

            return new AnyTensor<>(myFactory, rank, Math.toIntExact(dimensions));
        }

    }

    public static <N extends Comparable<N>> TensorFactoryAnyD<N, AnyTensor<N>> factory( DenseArray.Factory<N> arrayFactory) {
        return new TensorFactoryAnyD<>(new AnyTensor.Factory<>(arrayFactory));
    }

    private final ArrayAnyD<N> myArray;
    private final ArrayAnyD.Factory<N> myFactory;

    AnyTensor( ArrayAnyD.Factory<N> factory,  int rank,  int dimensions) {

        super(rank, dimensions, factory.function(), factory.scalar());

        long[] shape = new long[rank];
        Arrays.fill(shape, dimensions);

        myFactory = factory;
        myArray = factory.make(shape);
    }

    @Override public AnyTensor<N> add( AnyTensor<N> addend) {

        AnyTensor<N> retVal = this.newSameShape();

        this.add(retVal.getArray(), myArray, addend);

        return retVal;
    }

    @Override public byte byteValue( long... ref) {
        return myArray.byteValue(ref);
    }

    @Override public AnyTensor<N> conjugate() {

        AnyTensor<N> retVal = this.newSameShape();
        ArrayAnyD<N> array = retVal.getArray();

        long[] transp = retVal.shape().clone();
        int max = this.rank() - 1;

        array.loopAllReferences(ref -> {
            for (int i = 0; i < transp.length; i++) {
                transp[max - i] = ref[i];
            }
            array.set(transp, myArray.doubleValue(ref));
        });

        return retVal;
    }

    @Override public long count( int dimension) {
        return myArray.count(dimension);
    }

    @Override public double doubleValue( long... ref) {
        return myArray.doubleValue(ref);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof AnyTensor)) {
            return false;
        }
        var other = (AnyTensor) obj;
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

    @Override public void fillSet( int dimension,  long dimensionalIndex,  N value) {
        myArray.fillSet(dimension, dimensionalIndex, value);
    }

    @Override public void fillSet( int dimension,  long dimensionalIndex,  NullaryFunction<?> supplier) {
        myArray.fillSet(dimension, dimensionalIndex, supplier);
    }

    @Override public void fillSet( long[] initial,  int dimension,  N value) {
        myArray.fillSet(initial, dimension, value);
    }

    @Override public void fillSet( long[] initial,  int dimension,  NullaryFunction<?> supplier) {
        myArray.fillSet(initial, dimension, supplier);
    }

    @Override public float floatValue( long... ref) {
        return myArray.floatValue(ref);
    }

    @Override public N get( long... ref) {
        return myArray.get(ref);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + (myArray == null ? 0 : myArray.hashCode());
        return prime * result + (myFactory == null ? 0 : myFactory.hashCode());
    }

    @Override public int intValue( long... ref) {
        return myArray.intValue(ref);
    }

    @Override public long longValue( long... ref) {
        return myArray.longValue(ref);
    }

    @Override public AnyTensor<N> multiply( double scalarMultiplicand) {

        AnyTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public AnyTensor<N> multiply( N scalarMultiplicand) {

        AnyTensor<N> retVal = this.newSameShape();

        this.multiply(retVal.getArray(), scalarMultiplicand, myArray);

        return retVal;
    }

    @Override public AnyTensor<N> negate() {

        AnyTensor<N> retVal = this.newSameShape();

        this.negate(retVal.getArray(), myArray);

        return retVal;
    }

    @Override public double norm() {
        return this.norm(myArray);
    }

    @Override public void set( long[] reference,  byte value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  Comparable<?> value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  double value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  float value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  int value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  long value) {
        myArray.set(reference, value);
    }

    @Override public void set( long[] reference,  short value) {
        myArray.set(reference, value);
    }

    @Override public long[] shape() {
        return myArray.shape();
    }

    @Override public short shortValue( long... ref) {
        return myArray.shortValue(ref);
    }

    @Override
    public String toString() {
        return AccessAnyD.toString(myArray);
    }

    ArrayAnyD<N> getArray() {
        return myArray;
    }

    @Override
    AnyTensor<N> newSameShape() {
        return new AnyTensor<>(myFactory, this.rank(), this.dimensions());
    }

}
