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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
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
import org.ojalgo.structure.AccessAnyD;
import org.ojalgo.structure.FactoryAnyD;
import org.ojalgo.structure.MutateAnyD;
import org.ojalgo.structure.StructureAnyD;
import org.ojalgo.structure.TransformationAnyD;
import org.ojalgo.tensor.TensorFactoryAnyD;

/**
 * ArrayAnyD
 *
 * @author apete
 */
public final class ArrayAnyD<N extends Comparable<N>> implements AccessAnyD.Visitable<N>, AccessAnyD.Aggregatable<N>, AccessAnyD.Sliceable<N>,
        StructureAnyD.ReducibleTo1D<Array1D<N>>, StructureAnyD.ReducibleTo2D<Array2D<N>>, AccessAnyD.Collectable<N, MutateAnyD>,
        MutateAnyD.ModifiableReceiver<N>, MutateAnyD.Mixable<N>, StructureAnyD.Reshapable {

    public static final class Factory<N extends Comparable<N>>
            implements FactoryAnyD.Dense<ArrayAnyD<N>>, FactoryAnyD.MayBeSparse<ArrayAnyD<N>, ArrayAnyD<N>, ArrayAnyD<N>> {

        private final BasicArray.Factory<N> myDelegate;

        Factory( DenseArray.Factory<N> denseArray) {
            super();
            myDelegate = new BasicArray.Factory<>(denseArray);
        }

        @Override public ArrayAnyD<N> copy( AccessAnyD<?> source) {
            return myDelegate.copy(source).wrapInArrayAnyD(source.shape());
        }

        @Override
        public FunctionSet<N> function() {
            return myDelegate.function();
        }

        @Override
        public ArrayAnyD<N> make( long... structure) {
            return this.makeDense(structure);
        }

        @Override public ArrayAnyD<N> makeDense( long... structure) {
            return myDelegate.makeToBeFilled(structure).wrapInArrayAnyD(structure);
        }

        @Override public ArrayAnyD<N> makeFilled( long[] structure,  NullaryFunction<?> supplier) {

            BasicArray<N> toBeFilled = myDelegate.makeToBeFilled(structure);

            toBeFilled.fillAll(supplier);

            return toBeFilled.wrapInArrayAnyD(structure);
        }

        @Override public ArrayAnyD<N> makeSparse( long... structure) {
            return myDelegate.makeStructuredZero(structure).wrapInArrayAnyD(structure);
        }

        @Override
        public Scalar.Factory<N> scalar() {
            return myDelegate.scalar();
        }

        public TensorFactoryAnyD<N, ArrayAnyD<N>> tensor() {
            return TensorFactoryAnyD.of(this);
        }

    }

    public static final Factory<ComplexNumber> C128 = ArrayAnyD.factory(ArrayC128.FACTORY);
    public static final Factory<Quaternion> H256 = ArrayAnyD.factory(ArrayH256.FACTORY);
    public static final Factory<RationalNumber> Q128 = ArrayAnyD.factory(ArrayQ128.FACTORY);
    public static final Factory<Double> R032 = ArrayAnyD.factory(ArrayR032.FACTORY);
    public static final Factory<Double> R064 = ArrayAnyD.factory(ArrayR064.FACTORY);
    public static final Factory<Quadruple> R128 = ArrayAnyD.factory(ArrayR128.FACTORY);
    public static final Factory<BigDecimal> R256 = ArrayAnyD.factory(ArrayR256.FACTORY);
    public static final Factory<Double> Z008 = ArrayAnyD.factory(ArrayZ008.FACTORY);
    public static final Factory<Double> Z016 = ArrayAnyD.factory(ArrayZ016.FACTORY);
    public static final Factory<Double> Z032 = ArrayAnyD.factory(ArrayZ032.FACTORY);
    public static final Factory<Double> Z064 = ArrayAnyD.factory(ArrayZ064.FACTORY);

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
    public static final Factory<Double> DIRECT32 = ArrayAnyD.factory(BufferArray.DIRECT32);
    /**
     * @deprecated v52 Use {@link #factory(DenseArray.Factory)} instead
     */
    @Deprecated
    public static final Factory<Double> DIRECT64 = ArrayAnyD.factory(BufferArray.DIRECT64);
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
     * @deprecated v52 Use {@link #Q128} instead
     */
    @Deprecated
    public static final Factory<RationalNumber> RATIONAL = Q128;
    /**
     * @deprecated v52 Use {@link #H256} instead
     */
    @Deprecated
    public static final Factory<Quaternion> QUATERNION = H256;

    public static <N extends Comparable<N>> ArrayAnyD.Factory<N> factory( DenseArray.Factory<N> denseArray) {
        return new ArrayAnyD.Factory<>(denseArray);
    }

    private final BasicArray<N> myDelegate;
    private final long[] myStructure;

    @SuppressWarnings("unused")
    private ArrayAnyD() {
        this(null, new long[0]);
    }

    ArrayAnyD( BasicArray<N> delegate,  long[] structure) {

        super();

        myDelegate = delegate;
        myStructure = structure;
    }

    @Override
    public void add( long index,  byte addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  Comparable<?> addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  double addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  float addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  int addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  long addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long index,  short addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public void add( long[] reference,  byte addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  Comparable<?> addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  double addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  float addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  int addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  long addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public void add( long[] reference,  short addend) {
        myDelegate.add(StructureAnyD.index(myStructure, reference), addend);
    }

    @Override
    public N aggregateRange( long first,  long limit,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRange(first, limit, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateSet( int dimension,  long dimensionalIndex,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitSet(dimension, dimensionalIndex, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateSet( long[] initial,  int dimension,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitSet(initial, dimension, visitor);
        return visitor.get();
    }

    @Override
    public byte byteValue( long index) {
        return myDelegate.byteValue(index);
    }

    @Override
    public byte byteValue( long... ref) {
        return myDelegate.byteValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public long count() {
        return myDelegate.count();
    }

    @Override
    public long count( int dimension) {
        return StructureAnyD.count(myStructure, dimension);
    }

    @Override
    public double doubleValue( long index) {
        return myDelegate.doubleValue(index);
    }

    @Override
    public double doubleValue( long... ref) {
        return myDelegate.doubleValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ArrayAnyD)) {
            return false;
        }
        var other = (ArrayAnyD<?>) obj;
        if (!Arrays.equals(myStructure, other.myStructure)) {
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

    @Override public ArrayAnyD<N> expand( int rank) {

        int r = Math.max(this.rank(), rank);
        long[] shape = new long[r];

        for (int d = 0; d < r; d++) {
            shape[d] = this.count(d);
        }

        return this.reshape(shape);
    }

    @Override
    public void fillAll( N value) {
        myDelegate.fill(0L, this.count(), 1L, value);
    }

    @Override
    public void fillAll( NullaryFunction<?> supplier) {
        myDelegate.fill(0L, this.count(), 1L, supplier);
    }

    @Override
    public void fillOne( long index,  Access1D<?> values,  long valueIndex) {
        myDelegate.fillOne(index, values, valueIndex);
    }

    @Override
    public void fillOne( long index,  N value) {
        myDelegate.fillOne(index, value);
    }

    @Override
    public void fillOne( long index,  NullaryFunction<?> supplier) {
        myDelegate.fillOne(index, supplier);
    }

    @Override
    public void fillOne( long[] reference,  N value) {
        myDelegate.fillOne(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void fillOne( long[] reference,  NullaryFunction<?> supplier) {
        myDelegate.fillOne(StructureAnyD.index(myStructure, reference), supplier);
    }

    @Override
    public void fillRange( long first,  long limit,  N value) {
        myDelegate.fill(first, limit, 1L, value);
    }

    @Override
    public void fillRange( long first,  long limit,  NullaryFunction<?> supplier) {
        myDelegate.fill(first, limit, 1L, supplier);
    }

    @Override
    public void fillSet( int dimension,  long dimensionalIndex,  N value) {
        this.loop(dimension, dimensionalIndex, (f, l, s) -> myDelegate.fill(f, l, s, value));
    }

    @Override
    public void fillSet( int dimension,  long dimensionalIndex,  NullaryFunction<?> supplier) {
        this.loop(dimension, dimensionalIndex, (f, l, s) -> myDelegate.fill(f, l, s, supplier));
    }

    @Override
    public void fillSet( long[] initial,  int dimension,  N value) {
        this.loop(initial, dimension, (f, l, s) -> myDelegate.fill(f, l, s, value));
    }

    @Override
    public void fillSet( long[] initial,  int dimension,  NullaryFunction<?> supplier) {
        this.loop(initial, dimension, (f, l, s) -> myDelegate.fill(f, l, s, supplier));
    }

    /**
     * Flattens this abitrary dimensional array to a one dimensional array. The (internal/actual) array is not
     * copied, it is just accessed through a different adaptor.
     *
     * @see org.ojalgo.structure.StructureAnyD.Reshapable#flatten()
     */
    @Override public Array1D<N> flatten() {
        return myDelegate.wrapInArray1D();
    }

    @Override
    public float floatValue( long index) {
        return myDelegate.floatValue(index);
    }

    @Override
    public float floatValue( long... ref) {
        return myDelegate.floatValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public N get( long index) {
        return myDelegate.get(index);
    }

    @Override
    public N get( long... ref) {
        return myDelegate.get(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public int hashCode() {
        int prime = 31;
        @Var int result = 1;
        result = prime * result + (myDelegate == null ? 0 : myDelegate.hashCode());
        return prime * result + Arrays.hashCode(myStructure);
    }

    @Override
    public long indexOfLargest() {
        return myDelegate.indexOfLargest();
    }

    @Override
    public int intValue( long index) {
        return myDelegate.intValue(index);
    }

    @Override
    public int intValue( long... ref) {
        return myDelegate.intValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public long longValue( long index) {
        return myDelegate.longValue(index);
    }

    @Override
    public long longValue( long... ref) {
        return myDelegate.longValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public double mix( long[] reference,  BinaryFunction<N> mixer,  double addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            double oldValue = this.doubleValue(reference);
            double newValue = mixer.invoke(oldValue, addend);
            this.set(reference, newValue);
            return newValue;
        }
    }

    @Override
    public N mix( long[] reference,  BinaryFunction<N> mixer,  N addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            N oldValue = this.get(reference);
            N newValue = mixer.invoke(oldValue, addend);
            this.set(reference, newValue);
            return newValue;
        }
    }

    @Override
    public void modifyAll( UnaryFunction<N> modifier) {
        myDelegate.modify(0L, this.count(), 1L, modifier);
    }

    @Override
    public void modifyAny( TransformationAnyD<N> modifier) {
        modifier.transform(this);
    }

    @Override
    public void modifyMatching( Access1D<N> left,  BinaryFunction<N> function) {
        myDelegate.modify(0L, this.count(), 1L, left, function);
    }

    @Override
    public void modifyMatching( BinaryFunction<N> function,  Access1D<N> right) {
        myDelegate.modify(0L, this.count(), 1L, function, right);
    }

    @Override
    public void modifyOne( long index,  UnaryFunction<N> modifier) {
        myDelegate.modifyOne(index, modifier);
    }

    @Override
    public void modifyOne( long[] reference,  UnaryFunction<N> modifier) {
        myDelegate.modifyOne(StructureAnyD.index(myStructure, reference), modifier);
    }

    @Override
    public void modifyRange( long first,  long limit,  UnaryFunction<N> modifier) {
        myDelegate.modify(first, limit, 1L, modifier);
    }

    @Override
    public void modifySet( int dimension,  long dimensionalIndex,  UnaryFunction<N> modifier) {
        this.loop(dimension, dimensionalIndex, (f, l, s) -> myDelegate.modify(f, l, s, modifier));
    }

    @Override
    public void modifySet( long[] initial,  int dimension,  UnaryFunction<N> modifier) {
        this.loop(initial, dimension, (f, l, s) -> myDelegate.modify(f, l, s, modifier));
    }

    @Override
    public int rank() {
        return myStructure.length;
    }

    @Override
    public Array1D<N> reduce( int dimension,  Aggregator aggregator) {
        long reduceToCount = StructureAnyD.count(myStructure, dimension);
        Array1D<N> retVal = myDelegate.factory().make(reduceToCount).wrapInArray1D();
        this.reduce(dimension, aggregator, retVal);
        return retVal;
    }

    @Override
    public Array2D<N> reduce( int rowDimension,  int columnDimension,  Aggregator aggregator) {

        long[] structure = this.shape();

        long numberOfRows = structure[rowDimension];
        long numberOfColumns = structure[columnDimension];

        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());

        boolean primitive = myDelegate.isPrimitive();

        Array2D<N> retVal = myDelegate.factory().make(numberOfRows * numberOfColumns).wrapInArray2D(numberOfRows);

        for (long j = 0L; j < numberOfColumns; j++) {
            long colInd = j;

            for (long i = 0L; i < numberOfRows; i++) {
                long rowInd = i;

                visitor.reset();
                this.loop(reference -> reference[rowDimension] == rowInd && reference[columnDimension] == colInd, index -> this.visitOne(index, visitor));
                if (primitive) {
                    retVal.set(rowInd, colInd, visitor.doubleValue());
                } else {
                    retVal.set(rowInd, colInd, visitor.get());
                }
            }
        }

        return retVal;
    }

    @Override public void reset() {
        myDelegate.reset();
    }

    @Override public ArrayAnyD<N> reshape( long... shape) {
        if (StructureAnyD.count(shape) != this.count()) {
            throw new IllegalArgumentException();
        }
        return myDelegate.wrapInArrayAnyD(shape);
    }

    @Override
    public void set( long index,  byte value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  Comparable<?> value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  double value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  float value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  int value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  long value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long index,  short value) {
        myDelegate.set(index, value);
    }

    @Override
    public void set( long[] reference,  byte value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  Comparable<?> value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  double value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  float value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  int value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  long value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public void set( long[] reference,  short value) {
        myDelegate.set(StructureAnyD.index(myStructure, reference), value);
    }

    @Override
    public long[] shape() {
        return myStructure;
    }

    @Override
    public short shortValue( long index) {
        return myDelegate.shortValue(index);
    }

    @Override
    public short shortValue( long... ref) {
        return myDelegate.shortValue(StructureAnyD.index(myStructure, ref));
    }

    @Override
    public Array1D<N> sliceRange( long first,  long limit) {
        return myDelegate.wrapInArray1D().sliceRange(first, limit);
    }

    @Override
    public Array1D<N> sliceSet( long[] initial,  int dimension) {

        var first = new AtomicLong();
        var limit = new AtomicLong();
        var step = new AtomicLong();

        this.loop(initial, dimension, (f, l, s) -> {
            first.set(f);
            limit.set(l);
            step.set(s);
        });

        return new Array1D<>(myDelegate, first.longValue(), limit.longValue(), step.longValue());
    }

    @Override public ArrayAnyD<N> squeeze() {

        long[] oldShape = this.shape();

        @Var int notOne = 0;
        for (int i = 0; i < oldShape.length; i++) {
            if (oldShape[i] > 1) {
                notOne++;
            }
        }

        if (notOne == oldShape.length) {
            return this;
        }
        long[] shape = new long[notOne];

        for (int i = 0, d = 0; i < oldShape.length; i++) {
            long length = oldShape[i];
            if (length > 1) {
                shape[d++] = length;
            }
        }

        return this.reshape(shape);
    }

    @Override public void supplyTo( MutateAnyD receiver) {
        myDelegate.supplyTo(receiver);
    }

    @Override
    public String toString() {

        var retVal = new StringBuilder();

        retVal.append('<');
        retVal.append(myStructure[0]);
        for (int i = 1; i < myStructure.length; i++) {
            retVal.append('x');
            retVal.append(myStructure[i]);
        }
        retVal.append('>');

        var tmpLength = (int) this.count();
        if (tmpLength >= 1 && tmpLength <= 100) {
            retVal.append(' ');
            retVal.append(myDelegate.toString());
        }

        return retVal.toString();
    }

    @Override
    public void visitAll( VoidFunction<N> visitor) {
        myDelegate.visit(0L, this.count(), 1L, visitor);
    }

    @Override
    public void visitOne( long index,  VoidFunction<N> visitor) {
        myDelegate.visitOne(index, visitor);
    }

    @Override
    public void visitOne( long[] reference,  VoidFunction<N> visitor) {
        myDelegate.visitOne(StructureAnyD.index(myStructure, reference), visitor);
    }

    @Override
    public void visitRange( long first,  long limit,  VoidFunction<N> visitor) {
        myDelegate.visit(first, limit, 1L, visitor);
    }

    @Override
    public void visitSet( int dimension,  long dimensionalIndex,  VoidFunction<N> visitor) {
        this.loop(dimension, dimensionalIndex, (f, l, s) -> myDelegate.visit(f, l, s, visitor));
    }

    @Override
    public void visitSet( long[] initial,  int dimension,  VoidFunction<N> visitor) {
        this.loop(initial, dimension, (f, l, s) -> myDelegate.visit(f, l, s, visitor));
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }
}
