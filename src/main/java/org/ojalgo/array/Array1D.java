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
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import org.ojalgo.ProgrammingError;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Quadruple;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Factory1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.structure.Transformation1D;
import org.ojalgo.tensor.TensorFactory1D;

/**
 * Array1D
 *
 * @author apete
 */
public final class Array1D<N extends Comparable<N>> extends AbstractList<N> implements Access1D.Visitable<N>, Access1D.Aggregatable<N>, Access1D.Sliceable<N>,
        Access1D.Collectable<N, Mutate1D>, Mutate1D.ModifiableReceiver<N>, Mutate1D.Mixable<N>, Mutate1D.Sortable, RandomAccess {

    public static final class Factory<N extends Comparable<N>>
            implements Factory1D.Dense<Array1D<N>>, Factory1D.MayBeSparse<Array1D<N>, Array1D<N>, Array1D<N>> {

        private final BasicArray.Factory<N> myDelegate;

        Factory( DenseArray.Factory<N> denseArray) {
            super();
            myDelegate = new BasicArray.Factory<>(denseArray);
        }

        @Override public Array1D<N> copy( Access1D<?> source) {
            return myDelegate.copy(source).wrapInArray1D();
        }

        @Override public Array1D<N> copy( Comparable<?>[] source) {
            return myDelegate.copy(source).wrapInArray1D();
        }

        @Override public Array1D<N> copy( double... source) {
            return myDelegate.copy(source).wrapInArray1D();
        }

        @Override public Array1D<N> copy( List<? extends Comparable<?>> source) {
            return myDelegate.copy(source).wrapInArray1D();
        }

        @Override
        public FunctionSet<N> function() {
            return myDelegate.function();
        }

        @Override
        public Array1D<N> make( long count) {
            return this.makeDense(count);
        }

        @Override public Array1D<N> makeDense( long count) {
            return myDelegate.makeToBeFilled(count).wrapInArray1D();
        }

        @Override public Array1D<N> makeFilled( long count,  NullaryFunction<?> supplier) {
            return myDelegate.makeFilled(count, supplier).wrapInArray1D();
        }

        @Override public Array1D<N> makeSparse( long count) {
            return myDelegate.makeStructuredZero(count).wrapInArray1D();
        }

        @Override
        public Scalar.Factory<N> scalar() {
            return myDelegate.scalar();
        }

        public TensorFactory1D<N, Array1D<N>> tensor() {
            return TensorFactory1D.of(this);
        }

        public Array1D<N> wrap( BasicArray<N> array) {
            return array.wrapInArray1D();
        }

    }

    static final class QuickAscendingSorter extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        private final long high;
        private final long low;
        private final Array1D<?> myArray;

        private QuickAscendingSorter( Array1D<?> array,  long low,  long high) {
            super();
            myArray = array;
            this.low = low;
            this.high = high;
        }

        QuickAscendingSorter( Array1D<?> array) {
            this(array, 0L, array.count() - 1L);
        }

        @Override
        protected void compute() {

            @Var long i = low, j = high;

            double pivot = myArray.doubleValue(low + (high - low) / 2);

            while (i <= j) {

                while (myArray.doubleValue(i) < pivot) {
                    i++;
                }
                while (myArray.doubleValue(j) > pivot) {
                    j--;
                }

                if (i <= j) {
                    myArray.exchange(i, j);
                    i++;
                    j--;
                }
            }

            @Var QuickAscendingSorter tmpPartL = null;
            @Var QuickAscendingSorter tmpPartH = null;

            if (low < j) {
                tmpPartL = new QuickAscendingSorter(myArray, low, j);
                tmpPartL.fork();
            }
            if (i < high) {
                tmpPartH = new QuickAscendingSorter(myArray, i, high);
                tmpPartH.fork();
            }
            if (tmpPartL != null) {
                tmpPartL.join();
            }
            if (tmpPartH != null) {
                tmpPartH.join();
            }
        }

    }

    static final class QuickDescendingSorter extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        private final long high;
        private final long low;
        private final Array1D<?> myArray;

        private QuickDescendingSorter( Array1D<?> array,  long low,  long high) {
            super();
            myArray = array;
            this.low = low;
            this.high = high;
        }

        QuickDescendingSorter( Array1D<?> array) {
            this(array, 0L, array.count() - 1L);
        }

        @Override
        protected void compute() {

            @Var long i = low, j = high;

            double pivot = myArray.doubleValue(low + (high - low) / 2);

            while (i <= j) {

                while (myArray.doubleValue(i) > pivot) {
                    i++;
                }
                while (myArray.doubleValue(j) < pivot) {
                    j--;
                }

                if (i <= j) {
                    myArray.exchange(i, j);
                    i++;
                    j--;
                }
            }

            @Var QuickDescendingSorter tmpPartL = null;
            @Var QuickDescendingSorter tmpPartH = null;

            if (low < j) {
                tmpPartL = new QuickDescendingSorter(myArray, low, j);
                tmpPartL.fork();
            }
            if (i < high) {
                tmpPartH = new QuickDescendingSorter(myArray, i, high);
                tmpPartH.fork();
            }
            if (tmpPartL != null) {
                tmpPartL.join();
            }
            if (tmpPartH != null) {
                tmpPartH.join();
            }
        }

    }

    public static final Factory<ComplexNumber> C128 = Array1D.factory(ArrayC128.FACTORY);
    public static final Factory<Quaternion> H256 = Array1D.factory(ArrayH256.FACTORY);
    public static final Factory<RationalNumber> Q128 = Array1D.factory(ArrayQ128.FACTORY);
    public static final Factory<Double> R032 = Array1D.factory(ArrayR032.FACTORY);
    public static final Factory<Double> R064 = Array1D.factory(ArrayR064.FACTORY);
    public static final Factory<Quadruple> R128 = Array1D.factory(ArrayR128.FACTORY);
    public static final Factory<BigDecimal> R256 = Array1D.factory(ArrayR256.FACTORY);
    public static final Factory<Double> Z008 = Array1D.factory(ArrayZ008.FACTORY);
    public static final Factory<Double> Z016 = Array1D.factory(ArrayZ016.FACTORY);
    public static final Factory<Double> Z032 = Array1D.factory(ArrayZ032.FACTORY);
    public static final Factory<Double> Z064 = Array1D.factory(ArrayZ064.FACTORY);

    /**
     * @deprecated v52 Use {@link #Q128} instead
     */
    @Deprecated
    public static final Factory<RationalNumber> RATIONAL = Q128;
    /**
     * @deprecated v52 Use {@link #R128} instead
     */
    @Deprecated
    public static final Factory<BigDecimal> BIG = R256;
    /**
     * @deprecated v52 Use {@link #C128} instead
     */
    @Deprecated
    public static final Factory<ComplexNumber> COMPLEX = C128;
    /**
     * @deprecated v52 Use {@link #factory(DenseArray.Factory)} instead
     */
    @Deprecated
    public static final Factory<Double> DIRECT32 = Array1D.factory(BufferArray.DIRECT32);
    /**
     * @deprecated v52 Use {@link #factory(DenseArray.Factory)} instead
     */
    @Deprecated
    public static final Factory<Double> DIRECT64 = Array1D.factory(BufferArray.DIRECT64);
    /**
     * @deprecated v52 Use {@link #R032} instead
     */
    @Deprecated
    public static final Factory<Double> PRIMITIVE32 = R032;
    /**
     * @deprecated v52 Use {@link #R064} instead
     */
    @Deprecated
    public static final Factory<Double> PRIMITIVE64 = R064;
    /**
     * @deprecated v52 Use {@link #H256} instead
     */
    @Deprecated
    public static final Factory<Quaternion> QUATERNION = H256;

    public static <N extends Comparable<N>> Array1D.Factory<N> factory( DenseArray.Factory<N> denseFactory) {
        return new Array1D.Factory<>(denseFactory);
    }

    public final long length;

    private final BasicArray<N> myDelegate;
    private final long myFirst;
    private final long myLimit;
    private final long myStep;

    Array1D( BasicArray<N> delegate) {
        this(delegate, 0L, delegate.count(), 1L);
    }

    Array1D( BasicArray<N> delegate,  long first,  long limit,  long step) {

        super();

        myDelegate = delegate;

        myFirst = first;
        myLimit = limit;
        myStep = step;

        length = (myLimit - myFirst) / myStep;
    }

    @Override
    public void add( long index,  byte addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  Comparable<?> addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  double addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  float addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  int addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  long addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public void add( long index,  short addend) {
        myDelegate.add(this.convert(index), addend);
    }

    @Override
    public N aggregateRange( long first,  long limit,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRange(first, limit, visitor);
        return visitor.get();
    }

    @Override
    public byte byteValue( long index) {
        return myDelegate.byteValue(this.convert(index));
    }

    @Override
    public void clear() {
        myDelegate.reset();
    }

    @Override
    public boolean contains( Object obj) {
        return this.indexOf(obj) != -1;
    }

    @Override
    public long count() {
        return length;
    }

    @Override
    public double doubleValue( long index) {
        return myDelegate.doubleValue(this.convert(index));
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Array1D)) {
            return false;
        }
        var other = (Array1D<?>) obj;
        if (length != other.length || myFirst != other.myFirst || myLimit != other.myLimit || myStep != other.myStep) {
            return false;
        }
        if (myDelegate == null) {
            if (other.myDelegate != null) {
                return false;
            }
        } else if (!myDelegate.equals(other.myDelegate)) {
            return false;
        }
        return true;
    }

    @Override
    public void fillAll( N value) {
        myDelegate.fill(myFirst, myLimit, myStep, value);
    }

    @Override
    public void fillAll( NullaryFunction<?> supplier) {
        myDelegate.fill(myFirst, myLimit, myStep, supplier);
    }

    @Override
    public void fillOne( long index,  Access1D<?> values,  long valueIndex) {
        myDelegate.fillOne(this.convert(index), values, valueIndex);
    }

    @Override
    public void fillOne( long index,  N value) {
        myDelegate.fillOne(this.convert(index), value);
    }

    @Override
    public void fillOne( long index,  NullaryFunction<?> supplier) {
        myDelegate.fillOne(this.convert(index), supplier);
    }

    @Override
    public void fillRange( long first,  long limit,  N value) {
        myDelegate.fill(this.convert(first), this.convert(limit), myStep, value);
    }

    @Override
    public void fillRange( long first,  long limit,  NullaryFunction<?> supplier) {
        myDelegate.fill(this.convert(first), this.convert(limit), myStep, supplier);
    }

    @Override
    public float floatValue( long index) {
        return myDelegate.floatValue(this.convert(index));
    }

    @Override
    public N get( int index) {
        return myDelegate.get(this.convert(index));
    }

    @Override
    public N get( long index) {
        return myDelegate.get(this.convert(index));
    }

    @Override
    public int hashCode() {
        int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + (int) (length ^ length >>> 32);
        result = prime * result + (myDelegate == null ? 0 : myDelegate.hashCode());
        result = prime * result + (int) (myFirst ^ myFirst >>> 32);
        result = prime * result + (int) (myLimit ^ myLimit >>> 32);
        return prime * result + (int) (myStep ^ myStep >>> 32);
    }

    @Override
    public int indexOf( Object obj) {
        int tmpLength = this.size();
        if (obj == null) {
            for (int i = 0; i < tmpLength; i++) {
                if (this.get(i) == null) {
                    return i;
                }
            }
        } else if (obj instanceof Comparable) {
            for (int i = 0; i < tmpLength; i++) {
                if (obj.equals(this.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public long indexOfLargest() {
        long first = this.convert(myFirst);
        long limit = this.convert(myLimit);
        long step = myStep;
        return (myDelegate.indexOfLargest(first, limit, step) - first) / step;
    }

    @Override
    public int intValue( long index) {
        return myDelegate.intValue(this.convert(index));
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    @Override
    public long longValue( long index) {
        return myDelegate.longValue(this.convert(index));
    }

    @Override
    public double mix( long index,  BinaryFunction<N> mixer,  double addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            double oldValue = this.doubleValue(index);
            double newValue = mixer.invoke(oldValue, addend);
            this.set(index, newValue);
            return newValue;
        }
    }

    @Override
    public N mix( long index,  BinaryFunction<N> mixer,  N addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            N oldValue = this.get(index);
            N newValue = mixer.invoke(oldValue, addend);
            this.set(index, newValue);
            return newValue;
        }
    }

    @Override
    public void modifyAll( UnaryFunction<N> modifier) {
        myDelegate.modify(myFirst, myLimit, myStep, modifier);
    }

    @Override
    public void modifyAny( Transformation1D<N> modifier) {
        modifier.transform(this);
    }

    @Override
    public void modifyMatching( Access1D<N> left,  BinaryFunction<N> function) {
        long limit = Math.min(length, left.count());
        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(i, function.invoke(left.doubleValue(i), this.doubleValue(i)));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.set(i, function.invoke(left.get(i), this.get(i)));
            }
        }
    }

    @Override
    public void modifyMatching( BinaryFunction<N> function,  Access1D<N> right) {
        long limit = Math.min(length, right.count());
        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(i, function.invoke(this.doubleValue(i), right.doubleValue(i)));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.set(i, function.invoke(this.get(i), right.get(i)));
            }
        }
    }

    @Override
    public void modifyOne( long index,  UnaryFunction<N> modifier) {
        myDelegate.modifyOne(this.convert(index), modifier);
    }

    @Override
    public void modifyRange( long first,  long limit,  UnaryFunction<N> modifier) {
        myDelegate.modify(this.convert(first), this.convert(limit), myStep, modifier);
    }

    @Override public void reset() {
        myDelegate.reset();
    }

    @Override
    public N set( int index,  N value) {
        long tmpIndex = this.convert(index);
        N retVal = myDelegate.get(tmpIndex);
        myDelegate.set(tmpIndex, value);
        return retVal;
    }

    @Override
    public void set( long index,  byte value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  Comparable<?> value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  double value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  float value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  int value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  long value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public void set( long index,  short value) {
        myDelegate.set(this.convert(index), value);
    }

    @Override
    public short shortValue( long index) {
        return myDelegate.shortValue(this.convert(index));
    }

    @Override
    public int size() {
        return Math.toIntExact(length);
    }

    @Override
    public Array1D<N> sliceRange( long first,  long limit) {
        return new Array1D<>(myDelegate, this.convert(first), this.convert(limit), myStep);
    }

    @Override
    public void sortAscending() {

        if (myDelegate instanceof Mutate1D.Sortable && this.count() == myDelegate.count()) {

            ((Mutate1D.Sortable) myDelegate).sortAscending();

        } else {

            //this.sortAscending(0L, this.count() - 1L);

            try {
                ForkJoinPool.commonPool().submit(new QuickAscendingSorter(this)).get();
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void sortDescending() {

        if (myDelegate instanceof Mutate1D.Sortable && this.count() == myDelegate.count()) {

            ((Mutate1D.Sortable) myDelegate).sortDescending();

        } else {

            //this.sortDescending(0L, this.count() - 1L);

            try {
                ForkJoinPool.commonPool().submit(new QuickDescendingSorter(this)).get();
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Array1D<N> subList( int first,  int limit) {
        return this.sliceRange(first, limit);
    }

    @Override public void supplyTo( Mutate1D receiver) {
        long limit = Math.min(length, receiver.count());
        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                receiver.set(i, this.doubleValue(i));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                receiver.set(i, this.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return Access1D.toString(this);
    }

    @Override
    public void visitAll( VoidFunction<N> visitor) {
        myDelegate.visit(myFirst, myLimit, myStep, visitor);
    }

    @Override
    public void visitOne( long index,  VoidFunction<N> visitor) {
        myDelegate.visitOne(this.convert(index), visitor);
    }

    @Override
    public void visitRange( long first,  long limit,  VoidFunction<N> visitor) {
        myDelegate.visit(this.convert(first), this.convert(limit), myStep, visitor);
    }

    /**
     * Convert an external (public API) index to the corresponding internal
     */
    private long convert( long index) {
        return myFirst + myStep * index;
    }

    void exchange( long indexA,  long indexB) {

        if (myDelegate.isPrimitive()) {

            double tmpVal = this.doubleValue(indexA);
            this.set(indexA, this.doubleValue(indexB));
            this.set(indexB, tmpVal);

        } else {

            N tmpVal = this.get(indexA);
            this.set(indexA, this.get(indexB));
            this.set(indexB, tmpVal);
        }
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }

    void sortAscending( long low,  long high) {

        @Var long i = low, j = high;

        double pivot = this.doubleValue(low + (high - low) / 2);

        while (i <= j) {

            while (this.doubleValue(i) < pivot) {
                i++;
            }
            while (this.doubleValue(j) > pivot) {
                j--;
            }

            if (i <= j) {
                this.exchange(i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            this.sortAscending(low, j);
        }
        if (i < high) {
            this.sortAscending(i, high);
        }
    }

    void sortDescending( long low,  long high) {

        @Var long i = low, j = high;

        double pivot = this.doubleValue(low + (high - low) / 2);

        while (i <= j) {

            while (this.doubleValue(i) > pivot) {
                i++;
            }
            while (this.doubleValue(j) < pivot) {
                j--;
            }

            if (i <= j) {
                this.exchange(i, j);
                i++;
                j--;
            }
        }

        if (low < j) {
            this.sortDescending(low, j);
        }
        if (i < high) {
            this.sortDescending(i, high);
        }
    }

}
