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
import java.util.List;
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
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Factory2D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.structure.Transformation2D;
import org.ojalgo.tensor.TensorFactory2D;

/**
 * Array2D
 *
 * @author apete
 */
public final class Array2D<N extends Comparable<N>> implements Access2D.Visitable<N>, Access2D.Aggregatable<N>, Access2D.Sliceable<N>,
        Structure2D.ReducibleTo1D<Array1D<N>>, Access2D.Collectable<N, Mutate2D>, Mutate2D.ModifiableReceiver<N>, Mutate2D.Mixable<N>, Structure2D.Reshapable {

    public static final class Factory<N extends Comparable<N>>
            implements Factory2D.Dense<Array2D<N>>, Factory2D.MayBeSparse<Array2D<N>, Array2D<N>, Array2D<N>> {

        private final BasicArray.Factory<N> myDelegate;

        Factory( DenseArray.Factory<N> denseArray) {
            super();
            myDelegate = new BasicArray.Factory<>(denseArray);
        }

        @Override public Array2D<N> columns( Access1D<?>... source) {

            int tmpColumns = source.length;
            long tmpRows = source[0].count();

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            if (tmpDelegate.isPrimitive()) {
                @Var long tmpIndex = 0L;
                for (int j = 0; j < tmpColumns; j++) {
                    Access1D<?> tmpColumn = source[j];
                    for (long i = 0L; i < tmpRows; i++) {
                        tmpDelegate.set(tmpIndex++, tmpColumn.doubleValue(i));
                    }
                }
            } else {
                @Var long tmpIndex = 0L;
                for (int j = 0; j < tmpColumns; j++) {
                    Access1D<?> tmpColumn = source[j];
                    for (long i = 0L; i < tmpRows; i++) {
                        tmpDelegate.set(tmpIndex++, tmpColumn.get(i));
                    }
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> columns( Comparable<?>[]... source) {

            int tmpColumns = source.length;
            int tmpRows = source[0].length;

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            @Var long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                Comparable<?>[] tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn[i]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> columns( double[]... source) {

            int tmpColumns = source.length;
            int tmpRows = source[0].length;

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            @Var long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                double[] tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn[i]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> columns( List<? extends Comparable<?>>... source) {

            int tmpColumns = source.length;
            int tmpRows = source[0].size();

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            @Var long tmpIndex = 0L;
            for (int j = 0; j < tmpColumns; j++) {
                List<? extends Comparable<?>> tmpColumn = source[j];
                for (int i = 0; i < tmpRows; i++) {
                    tmpDelegate.set(tmpIndex++, tmpColumn.get(i));
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> copy( Access2D<?> source) {
            return myDelegate.copy(source).wrapInArray2D(source.countRows());
        }

        @Override
        public FunctionSet<N> function() {
            return myDelegate.function();
        }

        @Override
        public Array2D<N> make( long rows,  long columns) {
            return this.makeDense(rows, columns);
        }

        @Override public Array2D<N> makeDense( long rows,  long columns) {
            return myDelegate.makeToBeFilled(rows, columns).wrapInArray2D(rows);
        }

        @Override public Array2D<N> makeFilled( long rows,  long columns,  NullaryFunction<?> supplier) {

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(rows, columns);

            @Var long tmpIndex = 0L;
            for (long j = 0L; j < columns; j++) {
                for (long i = 0L; i < rows; i++) {
                    tmpDelegate.set(tmpIndex++, supplier.get());
                }
            }

            return tmpDelegate.wrapInArray2D(rows);
        }

        @Override public Array2D<N> makeSparse( long rows,  long columns) {
            return myDelegate.makeStructuredZero(rows, columns).wrapInArray2D(rows);
        }

        @Override public Array2D<N> rows( Access1D<?>... source) {

            int tmpRows = source.length;
            long tmpColumns = source[0].count();

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            if (tmpDelegate.isPrimitive()) {
                for (int i = 0; i < tmpRows; i++) {
                    Access1D<?> tmpRow = source[i];
                    for (long j = 0L; j < tmpColumns; j++) {
                        tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.doubleValue(j));
                    }
                }
            } else {
                for (int i = 0; i < tmpRows; i++) {
                    Access1D<?> tmpRow = source[i];
                    for (long j = 0L; j < tmpColumns; j++) {
                        tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.get(j));
                    }
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> rows( Comparable<?>[]... source) {

            int tmpRows = source.length;
            int tmpColumns = source[0].length;

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                Comparable<?>[] tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow[j]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> rows( double[]... source) {

            int tmpRows = source.length;
            int tmpColumns = source[0].length;

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                double[] tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow[j]);
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override public Array2D<N> rows( List<? extends Comparable<?>>... source) {

            int tmpRows = source.length;
            int tmpColumns = source[0].size();

            BasicArray<N> tmpDelegate = myDelegate.makeToBeFilled(tmpRows, tmpColumns);

            for (int i = 0; i < tmpRows; i++) {
                List<? extends Comparable<?>> tmpRow = source[i];
                for (int j = 0; j < tmpColumns; j++) {
                    tmpDelegate.set(Structure2D.index(tmpRows, i, j), tmpRow.get(j));
                }
            }

            return tmpDelegate.wrapInArray2D(tmpRows);
        }

        @Override
        public Scalar.Factory<N> scalar() {
            return myDelegate.scalar();
        }

        public TensorFactory2D<N, Array2D<N>> tensor() {
            return TensorFactory2D.of(this);
        }

    }

    public static final Factory<ComplexNumber> C128 = Array2D.factory(ArrayC128.FACTORY);
    public static final Factory<Quaternion> H256 = Array2D.factory(ArrayH256.FACTORY);
    public static final Factory<RationalNumber> Q128 = Array2D.factory(ArrayQ128.FACTORY);
    public static final Factory<Double> R032 = Array2D.factory(ArrayR032.FACTORY);
    public static final Factory<Double> R064 = Array2D.factory(ArrayR064.FACTORY);
    public static final Factory<Quadruple> R128 = Array2D.factory(ArrayR128.FACTORY);
    public static final Factory<BigDecimal> R256 = Array2D.factory(ArrayR256.FACTORY);
    public static final Factory<Double> Z008 = Array2D.factory(ArrayZ008.FACTORY);
    public static final Factory<Double> Z016 = Array2D.factory(ArrayZ016.FACTORY);
    public static final Factory<Double> Z032 = Array2D.factory(ArrayZ032.FACTORY);
    public static final Factory<Double> Z064 = Array2D.factory(ArrayZ064.FACTORY);

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
    public static final Factory<Double> DIRECT32 = Array2D.factory(BufferArray.DIRECT32);
    /**
     * @deprecated v52 Use {@link #factory(DenseArray.Factory)} instead
     */
    @Deprecated
    public static final Factory<Double> DIRECT64 = Array2D.factory(BufferArray.DIRECT64);
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
    /**
     * @deprecated v52 Use {@link #Q128} instead
     */
    @Deprecated
    public static final Factory<RationalNumber> RATIONAL = Q128;

    public static <N extends Comparable<N>> Array2D.Factory<N> factory( DenseArray.Factory<N> denseArray) {
        return new Array2D.Factory<>(denseArray);
    }

    private final long myColumnsCount;
    private final BasicArray<N> myDelegate;
    private final long myRowsCount;

    Array2D( BasicArray<N> delegate,  long structure) {

        super();

        myDelegate = delegate;

        myRowsCount = structure;
        myColumnsCount = structure == 0L ? 0L : delegate.count() / structure;
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
    public void add( long row,  long col,  byte addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  Comparable<?> addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  double addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  float addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  int addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  long addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long row,  long col,  short addend) {
        myDelegate.add(Structure2D.index(myRowsCount, row, col), addend);
    }

    @Override
    public void add( long index,  short addend) {
        myDelegate.add(index, addend);
    }

    @Override
    public N aggregateColumn( long row,  long col,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitColumn(row, col, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateDiagonal( long row,  long col,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitDiagonal(row, col, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateRange( long first,  long limit,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRange(first, limit, visitor);
        return visitor.get();
    }

    @Override
    public N aggregateRow( long row,  long col,  Aggregator aggregator) {
        AggregatorFunction<N> visitor = aggregator.getFunction(myDelegate.factory().aggregator());
        this.visitRow(row, col, visitor);
        return visitor.get();
    }

    @Override
    public byte byteValue( long index) {
        return myDelegate.byteValue(index);
    }

    @Override
    public byte byteValue( long row,  long col) {
        return myDelegate.byteValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public long count() {
        return myDelegate.count();
    }

    @Override
    public long countColumns() {
        return myColumnsCount;
    }

    @Override
    public long countRows() {
        return myRowsCount;
    }

    @Override
    public double doubleValue( long index) {
        return myDelegate.doubleValue(index);
    }

    @Override
    public double doubleValue( long row,  long col) {
        return myDelegate.doubleValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Array2D)) {
            return false;
        }
        var other = (Array2D<?>) obj;
        if (myRowsCount != other.myRowsCount || myColumnsCount != other.myColumnsCount) {
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
    public void exchangeColumns( long colA,  long colB) {
        myDelegate.exchange(colA * myRowsCount, colB * myRowsCount, 1L, myRowsCount);
    }

    @Override
    public void exchangeRows( long rowA,  long rowB) {
        myDelegate.exchange(rowA, rowB, myRowsCount, myColumnsCount);
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
    public void fillColumn( long row,  long col,  Access1D<N> values) {

        long offset = Structure2D.index(myRowsCount, row, col);
        long limit = Math.min(this.countRows() - row, values.count());

        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(offset + i, values.doubleValue(i));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.fillOne(offset + i, values.get(i));
            }
        }
    }

    @Override
    public void fillColumn( long row,  long col,  N value) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, value);
    }

    @Override
    public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, supplier);
    }

    @Override
    public void fillDiagonal( long row,  long col,  N value) {
        long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, value);
    }

    @Override
    public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
        long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, supplier);
    }

    @Override
    public void fillOne( long index,  Access1D<?> values,  long valueIndex) {
        myDelegate.fillOne(index, values, valueIndex);
    }

    @Override
    public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), values, valueIndex);
    }

    @Override
    public void fillOne( long row,  long col,  N value) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
        myDelegate.fillOne(Structure2D.index(myRowsCount, row, col), supplier);
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
    public void fillRange( long first,  long limit,  N value) {
        myDelegate.fill(first, limit, 1L, value);
    }

    @Override
    public void fillRange( long first,  long limit,  NullaryFunction<?> supplier) {
        myDelegate.fill(first, limit, 1L, supplier);
    }

    @Override
    public void fillRow( long row,  long col,  Access1D<N> values) {

        long offset = Structure2D.index(myRowsCount, row, col);
        long limit = Math.min(this.countColumns() - col, values.count());

        if (myDelegate.isPrimitive()) {
            for (long i = 0L; i < limit; i++) {
                this.set(offset + i * myRowsCount, values.doubleValue(i));
            }
        } else {
            for (long i = 0L; i < limit; i++) {
                this.fillOne(offset + i * myRowsCount, values.get(i));
            }
        }
    }

    @Override
    public void fillRow( long row,  long col,  N value) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, value);
    }

    @Override
    public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
        myDelegate.fill(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, supplier);
    }

    /**
     * Flattens this two dimensional array to a one dimensional array. The (internal/actual) array is not
     * copied, it is just accessed through a different adaptor.
     *
     * @see org.ojalgo.structure.Structure2D.Reshapable#flatten()
     */
    @Override public Array1D<N> flatten() {
        return myDelegate.wrapInArray1D();
    }

    @Override
    public float floatValue( long index) {
        return myDelegate.floatValue(index);
    }

    @Override
    public float floatValue( long row,  long col) {
        return myDelegate.floatValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public N get( long index) {
        return myDelegate.get(index);
    }

    @Override
    public N get( long row,  long col) {
        return myDelegate.get(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public int hashCode() {
        int prime = 31;
        @Var int result = 1;
        result = prime * result + (int) (myColumnsCount ^ myColumnsCount >>> 32);
        result = prime * result + (myDelegate == null ? 0 : myDelegate.hashCode());
        return prime * result + (int) (myRowsCount ^ myRowsCount >>> 32);
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
    public int intValue( long row,  long col) {
        return myDelegate.intValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public long longValue( long index) {
        return myDelegate.longValue(index);
    }

    @Override
    public long longValue( long row,  long col) {
        return myDelegate.longValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public double mix( long row,  long col,  BinaryFunction<N> mixer,  double addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            double oldValue = this.doubleValue(row, col);
            double newValue = mixer.invoke(oldValue, addend);
            this.set(row, col, newValue);
            return newValue;
        }
    }

    @Override
    public N mix( long row,  long col,  BinaryFunction<N> mixer,  N addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myDelegate) {
            N oldValue = this.get(row, col);
            N newValue = mixer.invoke(oldValue, addend);
            this.set(row, col, newValue);
            return newValue;
        }
    }

    @Override
    public void modifyAll( UnaryFunction<N> modifier) {
        myDelegate.modify(0L, this.count(), 1L, modifier);
    }

    @Override
    public void modifyAny( Transformation2D<N> modifier) {
        modifier.transform(this);
    }

    @Override
    public void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, modifier);
    }

    @Override
    public void modifyDiagonal( long row,  long col,  UnaryFunction<N> modifier) {
        long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, modifier);
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
    public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
        myDelegate.modifyOne(Structure2D.index(myRowsCount, row, col), modifier);
    }

    @Override
    public void modifyOne( long index,  UnaryFunction<N> modifier) {
        myDelegate.modifyOne(index, modifier);
    }

    @Override
    public void modifyRange( long first,  long limit,  UnaryFunction<N> modifier) {
        myDelegate.modify(first, limit, 1L, modifier);
    }

    @Override
    public void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
        myDelegate.modify(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, modifier);
    }

    @Override
    public Array1D<N> reduceColumns( Aggregator aggregator) {
        Array1D<N> retVal = myDelegate.factory().make(myColumnsCount).wrapInArray1D();
        this.reduceColumns(aggregator, retVal);
        return retVal;
    }

    @Override
    public Array1D<N> reduceRows( Aggregator aggregator) {
        Array1D<N> retVal = myDelegate.factory().make(myRowsCount).wrapInArray1D();
        this.reduceRows(aggregator, retVal);
        return retVal;
    }

    @Override public void reset() {
        myDelegate.reset();
    }

    @Override public Array2D<N> reshape( long rows,  long columns) {
        if (Structure2D.count(rows, columns) != this.count()) {
            throw new IllegalArgumentException();
        }
        return myDelegate.wrapInArray2D(rows);
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
    public void set( long row,  long col,  byte value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  Comparable<?> value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  double value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  float value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  int value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  long value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long row,  long col,  short value) {
        myDelegate.set(Structure2D.index(myRowsCount, row, col), value);
    }

    @Override
    public void set( long index,  short value) {
        myDelegate.set(index, value);
    }

    @Override
    public short shortValue( long index) {
        return myDelegate.shortValue(index);
    }

    @Override
    public short shortValue( long row,  long col) {
        return myDelegate.shortValue(Structure2D.index(myRowsCount, row, col));
    }

    @Override
    public Array1D<N> sliceColumn( long col) {
        return this.sliceColumn(0L, col);
    }

    @Override
    public Array1D<N> sliceColumn( long row,  long col) {
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L);
    }

    @Override
    public Array1D<N> sliceDiagonal( long row,  long col) {
        long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount),
                1L + myRowsCount);
    }

    @Override
    public Array1D<N> sliceRange( long first,  long limit) {
        return myDelegate.wrapInArray1D().sliceRange(first, limit);
    }

    @Override
    public Array1D<N> sliceRow( long row) {
        return this.sliceRow(row, 0L);
    }

    @Override
    public Array1D<N> sliceRow( long row,  long col) {
        return new Array1D<>(myDelegate, Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount);
    }

    @Override public void supplyTo( Mutate2D receiver) {
        myDelegate.supplyTo(receiver);
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    @Override
    public void visitAll( VoidFunction<N> visitor) {
        myDelegate.visit(0L, this.count(), 1L, visitor);
    }

    @Override
    public void visitColumn( long row,  long col,  VoidFunction<N> visitor) {
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, myRowsCount, col), 1L, visitor);
    }

    @Override
    public void visitDiagonal( long row,  long col,  VoidFunction<N> visitor) {
        long tmpCount = Math.min(myRowsCount - row, myColumnsCount - col);
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row + tmpCount, col + tmpCount), 1L + myRowsCount, visitor);
    }

    @Override
    public void visitOne( long row,  long col,  VoidFunction<N> visitor) {
        myDelegate.visitOne(Structure2D.index(myRowsCount, row, col), visitor);
    }

    @Override
    public void visitOne( long index,  VoidFunction<N> visitor) {
        myDelegate.visitOne(index, visitor);
    }

    @Override
    public void visitRange( long first,  long limit,  VoidFunction<N> visitor) {
        myDelegate.visit(first, limit, 1L, visitor);
    }

    @Override
    public void visitRow( long row,  long col,  VoidFunction<N> visitor) {
        myDelegate.visit(Structure2D.index(myRowsCount, row, col), Structure2D.index(myRowsCount, row, myColumnsCount), myRowsCount, visitor);
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }

}
