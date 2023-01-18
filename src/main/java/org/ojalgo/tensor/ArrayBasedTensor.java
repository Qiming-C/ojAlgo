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
import org.ojalgo.array.DenseArray;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;

abstract class ArrayBasedTensor<N extends Comparable<N>, T extends ArrayBasedTensor<N, T>> implements Tensor<N, T> {

    public static abstract class Factory<N extends Comparable<N>> {

        private final DenseArray.Factory<N> myArrayFactory;

        Factory( DenseArray.Factory<N> arrayFactory) {
            super();
            myArrayFactory = arrayFactory;
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
            if (myArrayFactory == null) {
                if (other.myArrayFactory != null) {
                    return false;
                }
            } else if (!myArrayFactory.equals(other.myArrayFactory)) {
                return false;
            }
            return true;
        }

        public FunctionSet<N> function() {
            return myArrayFactory.function();
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myArrayFactory == null ? 0 : myArrayFactory.hashCode());
            return result;
        }

        public Scalar.Factory<N> scalar() {
            return myArrayFactory.scalar();
        }

        DenseArray.Factory<N> getArrayFactory() {
            return myArrayFactory;
        }

    }

    private final int myDimensions;
    private final FunctionSet<N> myFunctionSet;
    private final int myRank;
    private final Scalar.Factory<N> myScalarFactory;

    ArrayBasedTensor( int rank,  int dimensions,  FunctionSet<N> functionSet,  Scalar.Factory<N> scalarFactory) {
        super();
        myRank = rank;
        myDimensions = dimensions;
        myFunctionSet = functionSet;
        myScalarFactory = scalarFactory;
    }

    @Override public final int dimensions() {
        return myDimensions;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArrayBasedTensor)) {
            return false;
        }
        var other = (ArrayBasedTensor) obj;
        if (myDimensions != other.myDimensions) {
            return false;
        }
        if (myFunctionSet == null) {
            if (other.myFunctionSet != null) {
                return false;
            }
        } else if (!myFunctionSet.equals(other.myFunctionSet)) {
            return false;
        }
        if (myRank != other.myRank) {
            return false;
        }
        if (myScalarFactory == null) {
            if (other.myScalarFactory != null) {
                return false;
            }
        } else if (!myScalarFactory.equals(other.myScalarFactory)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = 1;
        result = prime * result + myDimensions;
        result = prime * result + (myFunctionSet == null ? 0 : myFunctionSet.hashCode());
        result = prime * result + myRank;
        result = prime * result + (myScalarFactory == null ? 0 : myScalarFactory.hashCode());
        return result;
    }

    @Override public final int rank() {
        return myRank;
    }

    @Override public T signum() {
        return this.multiply(PrimitiveMath.ONE / this.norm());
    }

    void add( Mutate1D.Fillable<N> receiver,  Access1D<N> left,  Access1D<N> right) {
        receiver.fillMatching(left, myFunctionSet.add(), right);
    }

    void multiply( Mutate1D.Fillable<N> receiver,  double left,  Access1D<N> right) {
        receiver.fillMatching(myFunctionSet.multiply().first(left), right);
    }

    void multiply( Mutate1D.Fillable<N> receiver,  N left,  Access1D<N> right) {
        receiver.fillMatching(myFunctionSet.multiply().first(left), right);
    }

    void negate( Mutate1D.Fillable<N> receiver,  Access1D<N> argument) {
        receiver.fillMatching(myFunctionSet.negate(), argument);
    }

    abstract T newSameShape();

    double norm( Access1D.Aggregatable<N> array) {

        double frobeniusNorm = NumberDefinition.doubleValue(array.aggregateAll(Aggregator.NORM2));

        switch (this.rank()) {
        case 1:
            return frobeniusNorm;
        case 2:
            return frobeniusNorm / PrimitiveMath.SQRT.invoke(this.dimensions());
        default:
            return frobeniusNorm / PrimitiveMath.ROOT.invoke(this.dimensions(), this.rank());
        }
    }

}
