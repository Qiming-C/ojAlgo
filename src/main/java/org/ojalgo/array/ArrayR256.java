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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import org.ojalgo.array.operation.AMAX;
import org.ojalgo.array.operation.AXPY;
import org.ojalgo.function.BigFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.aggregator.AggregatorSet;
import org.ojalgo.function.aggregator.BigAggregator;
import org.ojalgo.scalar.BigScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.math.MathType;

/**
 * A one- and/or arbitrary-dimensional array of {@linkplain java.math.BigDecimal}.
 *
 * @author apete
 */
public class ArrayR256 extends ReferenceTypeArray<BigDecimal> {

    public static final DenseArray.Factory<BigDecimal> FACTORY = new DenseArray.Factory<>() {

        @Override
        public AggregatorSet<BigDecimal> aggregator() {
            return BigAggregator.getSet();
        }

        @Override
        public FunctionSet<BigDecimal> function() {
            return BigFunction.getSet();
        }

        @Override
        public Scalar.Factory<BigDecimal> scalar() {
            return BigScalar.FACTORY;
        }

        @Override
        MathType getMathType() {
            return MathType.R128;
        }

        @Override
        PlainArray<BigDecimal> makeDenseArray( long size) {
            return ArrayR256.make((int) size);
        }

    };

    public static ArrayR256 make( int size) {
        return new ArrayR256(size);
    }

    public static ArrayR256 wrap( BigDecimal... data) {
        return new ArrayR256(data);
    }

    protected ArrayR256( BigDecimal[] data) {
        super(FACTORY, data);
    }

    protected ArrayR256( int size) {
        super(FACTORY, size);
    }

    @Override
    public void axpy( double a,  Mutate1D.Modifiable<?> y) {
        AXPY.invoke(y, a, data);
    }

    @Override
    public void sortAscending() {
        Arrays.parallelSort(data);
    }

    @Override
    public void sortDescending() {
        Arrays.parallelSort(data, Comparator.reverseOrder());
    }

    @Override
    protected void add( int index,  Comparable<?> addend) {
        this.fillOne(index, this.get(index).add(this.valueOf(addend)));
    }

    @Override
    protected void add( int index,  double addend) {
        this.fillOne(index, this.get(index).add(this.valueOf(addend)));
    }

    @Override
    protected byte byteValue( int index) {
        return this.get(index).byteValue();
    }

    @Override
    protected double doubleValue( int index) {
        return data[index].doubleValue();
    }

    @Override
    protected void fillOne( int index,  Access1D<?> values,  long valueIndex) {
        data[index] = this.valueOf(values.get(valueIndex));
    }

    @Override
    protected float floatValue( int index) {
        return data[index].floatValue();
    }

    @Override
    protected int indexOfLargest( int first,  int limit,  int step) {
        return AMAX.invoke(data, first, limit, step);
    }

    @Override
    protected int intValue( int index) {
        return this.get(index).intValue();
    }

    @Override
    protected boolean isAbsolute( int index) {
        return BigScalar.isAbsolute(data[index]);
    }

    @Override
    protected boolean isSmall( int index,  double comparedTo) {
        return BigScalar.isSmall(comparedTo, data[index]);
    }

    @Override
    protected long longValue( int index) {
        return this.get(index).longValue();
    }

    @Override
    protected short shortValue( int index) {
        return this.get(index).shortValue();
    }

    @Override
    protected void set( int index,  long value) {
        data[index] = new BigDecimal(value);
    }

}
