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
import org.ojalgo.function.FunctionSet;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Factory1D;
import org.ojalgo.structure.Mutate1D;

public final class TensorFactory1D<N extends Comparable<N>, T extends Mutate1D> implements Factory1D<T> {

    public static <N extends Comparable<N>, T extends Mutate1D> TensorFactory1D<N, T> of( Factory1D<T> factory) {
        return new TensorFactory1D<>(factory);
    }

    private final Factory1D<T> myFactory;

    TensorFactory1D( Factory1D<T> factory) {

        super();

        myFactory = factory;
    }

    public T copy( Access1D<N> elements) {

        T retVal = myFactory.make(elements);

        for (long i = 0; i < elements.count(); i++) {
            retVal.set(i, elements.get(i));
        }

        return retVal;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof TensorFactory1D)) {
            return false;
        }
        var other = (TensorFactory1D) obj;
        if (myFactory == null) {
            if (other.myFactory != null) {
                return false;
            }
        } else if (!myFactory.equals(other.myFactory)) {
            return false;
        }
        return true;
    }

    @Override public FunctionSet<N> function() {
        return (FunctionSet<N>) myFactory.function();
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + (myFactory == null ? 0 : myFactory.hashCode());
        return result;
    }

    @Override public T make( long count) {
        return myFactory.make(count);
    }

    @Override public Scalar.Factory<N> scalar() {
        return (Scalar.Factory<N>) myFactory.scalar();
    }

    /**
     * Direct sum of vectors.
     *
     * @see TensorFactoryAnyD#sum(Access1D...)
     */
    public T sum( Access1D<N>... vectors) {

        @Var long dimensions = 0;
        for (Access1D<N> vector : vectors) {
            dimensions += vector.count();
        }

        T retVal = myFactory.make(dimensions);

        @Var long offset = 0L;
        for (Access1D<N> vector : vectors) {
            long limit = vector.count();
            for (int i = 0; i < limit; i++) {
                retVal.set(offset + i, vector.doubleValue(i));
            }
            offset += limit;
        }

        return retVal;
    }

    public T values( double... elements) {

        T retVal = myFactory.make(elements.length);

        for (int i = 0; i < elements.length; i++) {
            retVal.set(i, elements[i]);
        }

        return retVal;
    }

    public T values( N... elements) {

        T retVal = myFactory.make(elements.length);

        for (int i = 0; i < elements.length; i++) {
            retVal.set(i, elements[i]);
        }

        return retVal;
    }

}
