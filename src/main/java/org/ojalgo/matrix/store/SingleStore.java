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

import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

final class SingleStore<N extends Comparable<N>> extends FactoryStore<N> {

    private final N myNumber;
    private final double myValue;

    

    SingleStore( PhysicalStore.Factory<N, ?> factory,  Comparable<N> element) {

        super(factory, 1, 1);

        Scalar<N> converted = factory.scalar().convert(element);
        myNumber = converted.get();
        myValue = converted.doubleValue();
    }

    @Override
    public MatrixStore<N> conjugate() {
        return new SingleStore<>(this.physical(), this.physical().scalar().convert(myNumber).conjugate().get());
    }

    @Override
    public double doubleValue( long anInd) {
        return myValue;
    }

    @Override public double doubleValue( long aRow,  long aCol) {
        return myValue;
    }

    @Override public N get( long aRow,  long aCol) {
        return myNumber;
    }

    @Override public void multiply( Access1D<N> right,  TransformableRegion<N> target) {
        // TODO Auto-generated method stub
        super.multiply(right, target);
    }

    @Override public MatrixStore<N> multiply( double scalar) {
        // TODO Auto-generated method stub
        return super.multiply(scalar);
    }

    @Override
    public MatrixStore<N> multiply( MatrixStore<N> right) {

         PhysicalStore.Factory<N, ?> tmpFactory = this.physical();

         PhysicalStore<N> retVal = tmpFactory.copy(right);

        retVal.modifyAll(tmpFactory.function().multiply().first(myNumber));

        return retVal;
    }

    @Override public MatrixStore<N> multiply( N scalar) {
        // TODO Auto-generated method stub
        return super.multiply(scalar);
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
        receiver.fillOne(0L, 0L, myNumber);
    }

    @Override public Scalar<N> toScalar( long row,  long column) {
        return this.physical().scalar().convert(myNumber);
    }

    @Override
    public MatrixStore<N> transpose() {
        return this;
    }

}
