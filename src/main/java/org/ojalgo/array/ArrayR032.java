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
package org.ojalgo.array;

import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import com.google.errorprone.annotations.Var;
import java.util.Arrays;
import org.ojalgo.array.operation.*;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.AggregatorSet;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.special.MissingMath;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;
import org.ojalgo.type.math.MathType;

/**
 * A one- and/or arbitrary-dimensional array of double.
 *
 * @author apete
 */
public class ArrayR032 extends PrimitiveArray {

    public static final DenseArray.Factory<Double> FACTORY = new DenseArray.Factory<>() {

        @Override
        public AggregatorSet<Double> aggregator() {
            return PrimitiveAggregator.getSet();
        }

        @Override
        public FunctionSet<Double> function() {
            return PrimitiveFunction.getSet();
        }

        @Override
        public Scalar.Factory<Double> scalar() {
            return PrimitiveScalar.FACTORY;
        }

        @Override
        MathType getMathType() {
            return MathType.R032;
        }

        @Override
        PlainArray<Double> makeDenseArray( long size) {
            return ArrayR032.make((int) size);
        }

    };

    public static ArrayR032 make( int size) {
        return new ArrayR032(size);
    }

    public static ArrayR032 wrap( float... data) {
        return new ArrayR032(data);
    }

    public final float[] data;

    /**
     * Array not copied! No checking!
     */
    protected ArrayR032( float[] data) {

        super(FACTORY, data.length);

        this.data = data;
    }

    protected ArrayR032( int size) {

        super(FACTORY, size);

        data = new float[size];
    }

    @Override
    public void axpy( double a,  Mutate1D.Modifiable<?> y) {
        AXPY.invoke(y, a, data);
    }

    @Override
    public double dot( Access1D<?> vector) {

        @Var double retVal = ZERO;

        for (int i = 0, limit = Math.min(data.length, (int) vector.count()); i < limit; i++) {
            retVal += data[i] * vector.doubleValue(i);
        }

        return retVal;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof ArrayR032)) {
            return false;
        }
        var other = (ArrayR032) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public void fillMatching( Access1D<?> values) {
        if (values instanceof ArrayR032) {
            FillMatchingSingle.fill(data, ((ArrayR032) values).data);
        } else {
            FillMatchingSingle.fill(data, values);
        }
    }

    @Override
    public void fillMatching( Access1D<Double> left,  BinaryFunction<Double> function,  Access1D<Double> right) {
        int limit = MissingMath.toMinIntExact(this.count(), left.count(), right.count());
        OperationBinary.invoke(data, 0, limit, 1, left, function, right);
    }

    @Override
    public void fillMatching( UnaryFunction<Double> function,  Access1D<Double> arguments) {
        int limit = MissingMath.toMinIntExact(this.count(), arguments.count());
        OperationUnary.invoke(data, 0, limit, 1, arguments, function);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        int result = super.hashCode();
        return prime * result + Arrays.hashCode(data);
    }

    @Override
    public void reset() {
        Arrays.fill(data, 0.0F);
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data);
    }

    @Override
    public void sortDescending() {
        CorePrimitiveOperation.negate(data, 0, data.length, 1, data);
        Arrays.parallelSort(data);
        CorePrimitiveOperation.negate(data, 0, data.length, 1, data);
    }

    @Override
    public void supplyTo( Mutate1D receiver) {
        int limit = Math.min(data.length, receiver.size());
        for (int i = 0; i < limit; i++) {
            receiver.set(i, data[i]);
        }
    }

    @Override
    protected void add( int index,  Comparable<?> addend) {
        data[index] += NumberDefinition.floatValue(addend);
    }

    @Override
    protected void add( int index,  double addend) {
        data[index] += (float) addend;
    }

    @Override
    protected void add( int index,  float addend) {
        data[index] += addend;
    }

    @Override
    protected byte byteValue( int index) {
        return (byte) Math.round(data[index]);
    }

    protected final float[] copyOfData() {
        return COPY.copyOf(data);
    }

    @Override
    protected final double doubleValue( int index) {
        return data[index];
    }

    @Override
    protected final void exchange( int firstA,  int firstB,  int step,  int count) {
        Exchange.exchange(data, firstA, firstB, step, count);
    }

    @Override
    protected final void fill( int first,  int limit,  int step,  Double value) {
        FillAll.fill(data, first, limit, step, value.floatValue());
    }

    @Override
    protected final void fill( int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        FillAll.fill(data, first, limit, step, supplier);
    }

    @Override
    protected void fillOne( int index,  Access1D<?> values,  long valueIndex) {
        data[index] = (float) values.doubleValue(valueIndex);
    }

    @Override
    protected void fillOne( int index,  Double value) {
        data[index] = value.floatValue();
    }

    @Override
    protected void fillOne( int index,  NullaryFunction<?> supplier) {
        data[index] = supplier.floatValue();
    }

    @Override
    protected float floatValue( int index) {
        return data[index];
    }

    @Override
    protected final Double get( int index) {
        return Double.valueOf(data[index]);
    }

    @Override
    protected final int indexOfLargest( int first,  int limit,  int step) {
        return AMAX.invoke(data, first, limit, step);
    }

    @Override
    protected int intValue( int index) {
        return Math.round(data[index]);
    }

    @Override
    protected boolean isAbsolute( int index) {
        return PrimitiveScalar.isAbsolute(data[index]);
    }

    @Override
    protected boolean isSmall( int index,  double comparedTo) {
        return PrimitiveScalar.isSmall(comparedTo, data[index]);
    }

    @Override
    protected final void modify( int first,  int limit,  int step,  Access1D<Double> left,  BinaryFunction<Double> function) {
        OperationBinary.invoke(data, first, limit, step, left, function, this);
    }

    @Override
    protected final void modify( int first,  int limit,  int step,  BinaryFunction<Double> function,  Access1D<Double> right) {
        OperationBinary.invoke(data, first, limit, step, this, function, right);
    }

    @Override
    protected final void modify( int first,  int limit,  int step,  UnaryFunction<Double> function) {
        OperationUnary.invoke(data, first, limit, step, this, function);
    }

    @Override
    protected final void modifyOne( int index,  UnaryFunction<Double> modifier) {
        data[index] = modifier.invoke(data[index]);
    }

    @Override
    protected final int searchAscending( Double number) {
        return Arrays.binarySearch(data, number.floatValue());
    }

    @Override
    protected final void set( int index,  Comparable<?> value) {
        data[index] = Scalar.floatValue(value);
    }

    @Override
    protected final void set( int index,  double value) {
        data[index] = (float) value;
    }

    @Override
    protected final void set( int index,  float value) {
        data[index] = value;
    }

    @Override
    protected short shortValue( int index) {
        return (short) Math.round(data[index]);
    }

    @Override
    protected final void visit( int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        OperationVoid.invoke(data, first, limit, step, visitor);
    }

    @Override
    protected void visitOne( int index,  VoidFunction<Double> visitor) {
        visitor.invoke(data[index]);
    }

    @Override
    void modify( long extIndex,  int intIndex,  Access1D<Double> left,  BinaryFunction<Double> function) {
        data[intIndex] = (float) function.invoke(left.doubleValue(extIndex), data[intIndex]);
    }

    @Override
    void modify( long extIndex,  int intIndex,  BinaryFunction<Double> function,  Access1D<Double> right) {
        data[intIndex] = (float) function.invoke(data[intIndex], right.doubleValue(extIndex));
    }

    @Override
    void modify( long extIndex,  int intIndex,  UnaryFunction<Double> function) {
        data[intIndex] = function.invoke(data[intIndex]);
    }

    @Override
    protected void set( int index,  long value) {
        data[index] = value;
    }

}
