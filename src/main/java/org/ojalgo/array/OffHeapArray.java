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

import com.google.errorprone.annotations.Var;
import java.util.function.LongFunction;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.AggregatorSet;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.type.math.MathType;

/**
 * <p>
 * Off heap memory array.
 * <p>
 * When just instantiated these array classes contain uninitialized memory – memory is allocated but not
 * initialized. To initialize call {@link #reset()}. Explicit initialization is only necessary if your code
 * depends on having zeros as the default/initial value.
 *
 * @author apete
 */
public abstract class OffHeapArray extends DenseArray<Double> {

    static final class Factory extends DenseArray.Factory<Double> {

        private final LongFunction<OffHeapArray> myConstructor;
        private final MathType myMathType;

        Factory( MathType mathType,  LongFunction<OffHeapArray> constructor) {
            super();
            myMathType = mathType;
            myConstructor = constructor;
        }

        @Override
        public AggregatorSet<Double> aggregator() {
            return PrimitiveAggregator.getSet();
        }

        @Override
        public FunctionSet<Double> function() {
            return PrimitiveFunction.getSet();
        }

        @Override
        public DenseArray<Double> makeDenseArray( long size) {
            return myConstructor.apply(size);
        }

        @Override
        public Scalar.Factory<Double> scalar() {
            return PrimitiveScalar.FACTORY;
        }

        @Override
        long getCapacityLimit() {
            return Long.MAX_VALUE;
        }

        @Override
        MathType getMathType() {
            return myMathType;
        }

    }

    public static final DenseArray.Factory<Double> R032 = new Factory(MathType.R032, OffHeapR032::new);
    public static final DenseArray.Factory<Double> R064 = new Factory(MathType.R064, OffHeapR064::new);
    public static final DenseArray.Factory<Double> Z008 = new Factory(MathType.Z008, OffHeapZ008::new);
    public static final DenseArray.Factory<Double> Z016 = new Factory(MathType.Z016, OffHeapZ016::new);
    public static final DenseArray.Factory<Double> Z032 = new Factory(MathType.Z032, OffHeapZ032::new);
    public static final DenseArray.Factory<Double> Z064 = new Factory(MathType.Z064, OffHeapZ064::new);

    /**
     * @deprecated Use {@link #R032} instead
     */
    @Deprecated
    public static final DenseArray.Factory<Double> NATIVE32 = R032;
    /**
     * @deprecated Use {@link #R064} instead
     */
    @Deprecated
    public static final DenseArray.Factory<Double> NATIVE64 = R064;

    /**
     * @deprecated Use {@link #R032} instead
     */
    @Deprecated
    public static OffHeapArray makeNative32( long count) {
        return new OffHeapR032(count);
    }

    /**
     * @deprecated Use {@link #R064} instead
     */
    @Deprecated
    public static OffHeapArray makeNative64( long count) {
        return new OffHeapR064(count);
    }

    private final long myCount;

    OffHeapArray( DenseArray.Factory<Double> factory,  long count) {

        super(factory);

        myCount = count;
    }

    @Override public final void add( long index,  double addend) {
        this.set(index, this.doubleValue(index) + addend);
    }

    @Override public final void add( long index,  float addend) {
        this.set(index, this.floatValue(index) + addend);
    }

    @Override public final void add( long index,  long addend) {
        this.set(index, this.longValue(index) + addend);
    }

    @Override public final void add( long index,  int addend) {
        this.set(index, this.intValue(index) + addend);
    }

    @Override public final void add( long index,  short addend) {
        this.set(index, this.shortValue(index) + addend);
    }

    @Override public final void add( long index,  byte addend) {
        this.set(index, this.byteValue(index) + addend);
    }

    @Override public final long count() {
        return myCount;
    }

    @Override public void fillAll( Double value) {
        this.fill(0L, this.count(), 1L, value);
    }

    public void fillOne( long index,  Access1D<?> values,  long valueIndex) {
        this.set(index, values.doubleValue(valueIndex));
    }

    public void fillOne( long index,  Double value) {
        this.set(index, value.doubleValue());
    }

    public void fillOne( long index,  NullaryFunction<?> supplier) {
        this.set(index, supplier.doubleValue());
    }

    @Override public Double get( long index) {
        return Double.valueOf(this.doubleValue(index));
    }

    @Override public void modifyOne( long index,  UnaryFunction<Double> modifier) {
        this.set(index, modifier.invoke(this.doubleValue(index)));
    }

    @Override public void visitOne( long index,  VoidFunction<Double> visitor) {
        visitor.accept(this.doubleValue(index));
    }

    @Override
    protected void exchange( long firstA,  long firstB,  long step,  long count) {

        @Var long tmpIndexA = firstA;
        @Var long tmpIndexB = firstB;

        @Var double tmpVal;

        for (long i = 0; i < count; i++) {

            tmpVal = this.doubleValue(tmpIndexA);
            this.set(tmpIndexA, this.doubleValue(tmpIndexB));
            this.set(tmpIndexB, tmpVal);

            tmpIndexA += step;
            tmpIndexB += step;
        }
    }

    @Override
    void modify( long extIndex,  int intIndex,  Access1D<Double> left,  BinaryFunction<Double> function) {
        this.set(intIndex, function.invoke(left.doubleValue(extIndex), this.doubleValue(intIndex)));
    }

    @Override
    void modify( long extIndex,  int intIndex,  BinaryFunction<Double> function,  Access1D<Double> right) {
        this.set(intIndex, function.invoke(this.doubleValue(intIndex), right.doubleValue(extIndex)));
    }

    @Override
    void modify( long extIndex,  int intIndex,  UnaryFunction<Double> function) {
        this.set(intIndex, function.invoke(this.doubleValue(intIndex)));
    }

}
