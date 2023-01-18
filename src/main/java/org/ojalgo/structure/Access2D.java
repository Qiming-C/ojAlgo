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
package org.ojalgo.structure;

import com.google.errorprone.annotations.Var;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.ojalgo.ProgrammingError;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.type.NumberDefinition;
import org.ojalgo.type.context.NumberContext;

/**
 * 2-dimensional accessor methods
 *
 * @see Access1D
 * @author apete
 */
public interface Access2D<N extends Comparable<N>> extends Structure2D, Access1D<N> {

    public interface Aggregatable<N extends Comparable<N>> extends Structure2D, Access1D.Aggregatable<N> {

        default N aggregateColumn( long col,  Aggregator aggregator) {
            return this.aggregateColumn(0L, col, aggregator);
        }

        N aggregateColumn(long row, long col, Aggregator aggregator);

        default N aggregateDiagonal( Aggregator aggregator) {
            return this.aggregateDiagonal(0L, 0L, aggregator);
        }

        N aggregateDiagonal(long row, long col, Aggregator aggregator);

        default N aggregateRow( long row,  Aggregator aggregator) {
            return this.aggregateRow(row, 0L, aggregator);
        }

        N aggregateRow(long row, long col, Aggregator aggregator);

        default void reduceColumns( Aggregator aggregator,  Mutate1D receiver) {
            for (long j = 0L, limit = Math.min(this.countColumns(), receiver.count()); j < limit; j++) {
                receiver.set(j, this.aggregateColumn(j, aggregator));
            }
        }

        default void reduceRows( Aggregator aggregator,  Mutate1D receiver) {
            for (long i = 0L, limit = Math.min(this.countRows(), receiver.count()); i < limit; i++) {
                receiver.set(i, this.aggregateRow(i, aggregator));
            }
        }

    }

    public interface Collectable<N extends Comparable<N>, R extends Mutate2D> extends Structure2D {

        default <I extends R> I collect( Factory2D<I> factory) {

            I retVal = factory.make(this.countRows(), this.countColumns());

            this.supplyTo(retVal);

            return retVal;
        }

        void supplyTo(R receiver);

    }

    public static class ColumnView<N extends Comparable<N>> implements Access1D<N>, Iterable<ColumnView<N>>, Iterator<ColumnView<N>>,
            Spliterator<ColumnView<N>>, Comparable<ColumnView<N>>, Access1D.Collectable<N, Mutate1D> {

        static final int CHARACTERISTICS = Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED
                | Spliterator.SIZED | Spliterator.SORTED | Spliterator.SUBSIZED;

        private long myColumn = -1L;
        private final Access2D<N> myDelegate2D;
        private final long myLastColumn;

        private ColumnView( Access2D<N> access,  long column,  long lastColumn) {

            super();

            myDelegate2D = access;
            myLastColumn = lastColumn;

            myColumn = column;
        }

        protected ColumnView( Access2D<N> access) {
            this(access, -1L, access.countColumns() - 1L);
        }

        ColumnView( Access2D<N> access,  long column) {
            this(access, column, access.countColumns() - 1L);
        }

        @Override public int characteristics() {
            return CHARACTERISTICS;
        }

        public long column() {
            return myColumn;
        }

        @Override public int compareTo( ColumnView<N> other) {
            return Long.compare(myColumn, other.column());
        }

        @Override public long count() {
            return myDelegate2D.countRows();
        }

        @Override public double doubleValue( long index) {
            return myDelegate2D.doubleValue(index, myColumn);
        }

        @Override public long estimateSize() {
            return myLastColumn - myColumn;
        }

        @Override public void forEachRemaining( Consumer<? super ColumnView<N>> action) {
            Iterator.super.forEachRemaining(action);
        }

        @Override public N get( long index) {
            return myDelegate2D.get(index, myColumn);
        }

        public void goToColumn( long column) {
            myColumn = column;
        }

        @Override public boolean hasNext() {
            return myColumn < myLastColumn;
        }

        public boolean hasPrevious() {
            return myColumn > 0L;
        }

        @Override public ColumnView<N> iterator() {
            return new ColumnView<>(myDelegate2D);
        }

        @Override public ColumnView<N> next() {
            myColumn++;
            return this;
        }

        public ColumnView<N> previous() {
            myColumn--;
            return this;
        }

        @Override public void remove() {
            ProgrammingError.throwForUnsupportedOptionalOperation();
        }

        public Stream<ColumnView<N>> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override public void supplyTo( Mutate1D receiver) {
            for (long i = 0L, limit = Math.min(myDelegate2D.countRows(), receiver.count()); i < limit; i++) {
                receiver.set(i, myDelegate2D.get(i, myColumn));
            }
        }

        @Override
        public String toString() {
            return Access1D.toString(this);
        }

        @Override public boolean tryAdvance( Consumer<? super ColumnView<N>> action) {
            if (this.hasNext()) {
                action.accept(this.next());
                return true;
            } else {
                return false;
            }
        }

        @Override public Spliterator<ColumnView<N>> trySplit() {

             long remaining = myLastColumn - myColumn;

            if (remaining > 1L) {

                 long split = myColumn + (remaining / 2L);

                 ColumnView<N> retVal = new ColumnView<>(myDelegate2D, myColumn, split);

                myColumn = split;

                return retVal;

            } else {

                return null;
            }
        }

    }

    public static class ElementView<N extends Comparable<N>> implements ElementView2D<N, ElementView<N>> {

        private final ElementView1D<N, ?> myDelegate1D;
        private final long myStructure;

        public ElementView( ElementView1D<N, ?> delegate,  long structure) {

            super();

            myDelegate1D = delegate;
            myStructure = structure;
        }

        @Override public long column() {
            return Structure2D.column(myDelegate1D.index(), myStructure);
        }

        @Override public double doubleValue() {
            return myDelegate1D.doubleValue();
        }

        @Override public long estimateSize() {
            return myDelegate1D.estimateSize();
        }

        @Override public N get() {
            return myDelegate1D.get();
        }

        @Override public boolean hasNext() {
            return myDelegate1D.hasNext();
        }

        @Override public boolean hasPrevious() {
            return myDelegate1D.hasPrevious();
        }

        @Override public long index() {
            return myDelegate1D.index();
        }

        @Override public ElementView<N> iterator() {
            return new ElementView<>(myDelegate1D.iterator(), myStructure);
        }

        @Override public ElementView<N> next() {
            myDelegate1D.next();
            return this;
        }

        @Override public long nextIndex() {
            return myDelegate1D.nextIndex();
        }

        @Override public ElementView<N> previous() {
            myDelegate1D.previous();
            return this;
        }

        @Override public long previousIndex() {
            return myDelegate1D.previousIndex();
        }

        @Override public long row() {
            return Structure2D.row(myDelegate1D.index(), myStructure);
        }

        @Override
        public String toString() {
            return myDelegate1D.toString();
        }

        @Override public ElementView<N> trySplit() {

            ElementView1D<N, ?> delegateSpliterator = myDelegate1D.trySplit();

            if (delegateSpliterator != null) {
                return new ElementView<>(delegateSpliterator, myStructure);
            }
            return null;
        }

    }

    public static class RowView<N extends Comparable<N>> implements Access1D<N>, Iterable<RowView<N>>, Iterator<RowView<N>>, Spliterator<RowView<N>>,
            Comparable<RowView<N>>, Access1D.Collectable<N, Mutate1D> {

        static final int CHARACTERISTICS = Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED
                | Spliterator.SIZED | Spliterator.SORTED | Spliterator.SUBSIZED;

        private final Access2D<N> myDelegate2D;
        private final long myLastRow;
        private long myRow = -1L;

        private RowView( Access2D<N> access,  long row,  long lastRow) {

            super();

            myDelegate2D = access;
            myLastRow = lastRow;

            myRow = row;
        }

        protected RowView( Access2D<N> access) {
            this(access, -1L, access.countRows() - 1L);
        }

        RowView( Access2D<N> access,  long row) {
            this(access, row, access.countRows() - 1L);
        }

        @Override public int characteristics() {
            return CHARACTERISTICS;
        }

        @Override public int compareTo( RowView<N> other) {
            return Long.compare(myRow, other.row());
        }

        @Override public long count() {
            return myDelegate2D.countColumns();
        }

        @Override public double doubleValue( long index) {
            return myDelegate2D.doubleValue(myRow, index);
        }

        @Override public long estimateSize() {
            return myLastRow - myRow;
        }

        @Override public void forEachRemaining( Consumer<? super RowView<N>> action) {
            Iterator.super.forEachRemaining(action);
        }

        @Override public N get( long index) {
            return myDelegate2D.get(myRow, index);
        }

        public void goToRow( long row) {
            myRow = row;
        }

        @Override public boolean hasNext() {
            return myRow < myLastRow;
        }

        public boolean hasPrevious() {
            return myRow > 0L;
        }

        @Override public RowView<N> iterator() {
            return new RowView<>(myDelegate2D);
        }

        @Override public RowView<N> next() {
            myRow++;
            return this;
        }

        public RowView<N> previous() {
            myRow--;
            return this;
        }

        @Override public void remove() {
            ProgrammingError.throwForUnsupportedOptionalOperation();
        }

        public long row() {
            return myRow;
        }

        public Stream<RowView<N>> stream() {
            return StreamSupport.stream(this, false);
        }

        @Override public void supplyTo( Mutate1D receiver) {
            for (long j = 0L, limit = Math.min(myDelegate2D.countColumns(), receiver.count()); j < limit; j++) {
                receiver.set(j, myDelegate2D.get(myRow, j));
            }
        }

        @Override
        public String toString() {
            return Access1D.toString(this);
        }

        @Override public boolean tryAdvance( Consumer<? super RowView<N>> action) {
            if (this.hasNext()) {
                action.accept(this.next());
                return true;
            } else {
                return false;
            }
        }

        @Override public Spliterator<RowView<N>> trySplit() {

             long remaining = myLastRow - myRow;

            if (remaining > 1L) {

                 long split = myRow + (remaining / 2L);

                 RowView<N> retVal = new RowView<>(myDelegate2D, myRow, split);

                myRow = split;

                return retVal;

            } else {

                return null;
            }
        }

    }

    public static final class SelectionView<N extends Comparable<N>> implements Access2D<N>, Collectable<N, Mutate2D> {

        private final long[] myColumns;
        private final Access2D<N> myFullData;
        private final long[] myRows;

        SelectionView( Access2D<N> fullData,  long[] rows,  long[] columns) {

            super();

            myFullData = fullData;
            myRows = Structure1D.replaceNullOrEmptyWithFull(rows, fullData.getRowDim());
            myColumns = Structure1D.replaceNullOrEmptyWithFull(columns, fullData.getColDim());
        }

        @Override public long countColumns() {
            return myColumns.length;
        }

        @Override public long countRows() {
            return myRows.length;
        }

        @Override public double doubleValue( long row,  long col) {
            return myFullData.doubleValue(myRows[Math.toIntExact(row)], myColumns[Math.toIntExact(col)]);
        }

        @Override public N get( long row,  long col) {
            return myFullData.get(myRows[Math.toIntExact(row)], myColumns[Math.toIntExact(col)]);
        }

        @Override public void supplyTo( Mutate2D receiver) {
            for (int j = 0; j < myColumns.length; j++) {
                for (int i = 0; i < myRows.length; i++) {
                    receiver.set(i, j, myFullData.get(myRows[i], myColumns[j]));
                }
            }
        }

        @Override
        public String toString() {
            return Access2D.toString(this);
        }

    }

    public interface Sliceable<N extends Comparable<N>> extends Structure2D, Access1D.Sliceable<N> {

        default Access1D<N> sliceColumn( long col) {
            return this.sliceColumn(0L, col);
            // return new ColumnView<N>(this, col);
        }

        Access1D<N> sliceColumn(long row, long col);

        default Access1D<N> sliceDiagonal() {
            return this.sliceDiagonal(0L, 0L);
        }

        Access1D<N> sliceDiagonal(long row, long col);

        default Access1D<N> sliceRow( long row) {
            return this.sliceRow(row, 0L);
        }

        Access1D<N> sliceRow(long row, long col);

    }

    public interface Visitable<N extends Comparable<N>> extends Structure2D, Access1D.Visitable<N> {

        default void visitColumn( long row,  long col,  VoidFunction<N> visitor) {
            this.loopColumn(row, col, (r, c) -> this.visitOne(r, c, visitor));
        }

        default void visitColumn( long col,  VoidFunction<N> visitor) {
            this.visitColumn(0L, col, visitor);
        }

        default void visitDiagonal( long row,  long col,  VoidFunction<N> visitor) {
            this.loopDiagonal(row, col, (r, c) -> this.visitOne(r, c, visitor));
        }

        default void visitDiagonal( VoidFunction<N> visitor) {
            this.visitDiagonal(0L, 0L, visitor);
        }

        void visitOne(long row, long col, VoidFunction<N> visitor);

        @Override default void visitOne( long index,  VoidFunction<N> visitor) {
            long tmpStructure = this.countRows();
            this.visitOne(Structure2D.row(index, tmpStructure), Structure2D.column(index, tmpStructure), visitor);
        }

        default void visitRow( long row,  long col,  VoidFunction<N> visitor) {
            this.loopRow(row, col, (r, c) -> this.visitOne(r, c, visitor));
        }

        default void visitRow( long row,  VoidFunction<N> visitor) {
            this.visitRow(row, 0L, visitor);
        }

    }

    static Access2D<Double> asPrimitive2D( Access2D<?> access) {
        return new Access2D<>() {

            @Override public long count() {
                return access.count();
            }

            @Override public long countColumns() {
                return access.countColumns();
            }

            @Override public long countRows() {
                return access.countRows();
            }

            @Override public double doubleValue( long index) {
                return access.doubleValue(index);
            }

            @Override public double doubleValue( long row,  long col) {
                return access.doubleValue(row, col);
            }

            @Override public Double get( long index) {
                return access.doubleValue(index);
            }

            @Override public Double get( long row,  long col) {
                return access.doubleValue(row, col);
            }

            @Override
            public String toString() {
                return Access2D.toString(this);
            }

        };
    }

    static boolean equals( Access2D<?> accessA,  Access2D<?> accessB,  NumberContext accuracy) {
        return accessA.countRows() == accessB.countRows() && accessA.countColumns() == accessB.countColumns() && Access1D.equals(accessA, accessB, accuracy);
    }

    static <R extends Mutate2D.Receiver<Double>> Access2D.Collectable<Double, R> newPrimitiveColumnCollectable( Access1D<?> anything1D) {
        return new Access2D.Collectable<>() {

            @Override public long countColumns() {
                return 1L;
            }

            @Override public long countRows() {
                return anything1D.count();
            }

            @Override public void supplyTo( R receiver) {
                receiver.reset();
                anything1D.nonzeros().forEach(nz -> receiver.set(nz.index(), 0L, nz.doubleValue()));
            }

        };
    }

    static <R extends Mutate2D.Receiver<Double>> Access2D.Collectable<Double, R> newPrimitiveRowCollectable( Access1D<?> anything1D) {
        return new Access2D.Collectable<>() {

            @Override public long countColumns() {
                return anything1D.count();
            }

            @Override public long countRows() {
                return 1L;
            }

            @Override public void supplyTo( R receiver) {
                receiver.reset();
                anything1D.nonzeros().forEach(nz -> receiver.set(0L, nz.index(), nz.doubleValue()));
            }

        };
    }

    static String toString( Access2D<?> matrix) {

        var builder = new StringBuilder();

        int numbRows = Math.toIntExact(matrix.countRows());
        int numbCols = Math.toIntExact(matrix.countColumns());

        builder.append(matrix.getClass().getName());
        builder.append(' ').append('<').append(' ').append(numbRows).append(' ').append('x').append(' ').append(numbCols).append(' ').append('>');

        if (numbRows > 0 && numbCols > 0 && numbRows <= 50 && numbCols <= 50 && numbRows * numbCols <= 200) {

            // First element
            builder.append("\n{ { ").append(matrix.get(0, 0));

            // Rest of the first row
            for (int j = 1; j < numbCols; j++) {
                builder.append(",\t").append(matrix.get(0, j));
            }

            // For each of the remaining rows
            for (int i = 1; i < numbRows; i++) {

                // First column
                builder.append(" },\n{ ").append(matrix.get(i, 0));

                // Remaining columns
                for (int j = 1; j < numbCols; j++) {
                    builder.append(",\t").append(matrix.get(i, j));
                }
            }

            // Finish
            builder.append(" } }");
        }

        return builder.toString();
    }

    static Access2D<Double> wrap( double[][] target) {
        return new Access2D<>() {

            @Override public long count() {
                return Structure2D.count(target.length, target[0].length);
            }

            @Override public long countColumns() {
                return target[0].length;
            }

            @Override public long countRows() {
                return target.length;
            }

            @Override public double doubleValue( long row,  long col) {
                return target[(int) row][(int) col];
            }

            @Override public Double get( long row,  long col) {
                return target[(int) row][(int) col];
            }

            @Override
            public String toString() {
                return Access2D.toString(this);
            }

        };
    }

    static <N extends Comparable<N>> Access2D<N> wrap( N[][] target) {
        return new Access2D<>() {

            @Override public long count() {
                return Structure2D.count(target.length, target[0].length);
            }

            @Override public long countColumns() {
                return target[0].length;
            }

            @Override public long countRows() {
                return target.length;
            }

            @Override public double doubleValue( long index) {
                return NumberDefinition.doubleValue(this.get(index));
            }

            @Override public double doubleValue( long row,  long col) {
                return NumberDefinition.doubleValue(this.get(row, col));
            }

            @Override public N get( long row,  long col) {
                return target[(int) row][(int) col];
            }

            @Override
            public String toString() {
                return Access2D.toString(this);
            }

        };
    }

    default <NN extends Comparable<NN>, R extends Mutate2D.Receiver<NN>> Collectable<NN, R> asCollectable2D() {
        return new Collectable<>() {

            @Override public long countColumns() {
                return Access2D.this.countColumns();
            }

            @Override public long countRows() {
                return Access2D.this.countRows();
            }

            @Override public void supplyTo( R receiver) {
                receiver.accept(Access2D.this);
            }

        };
    }

    @Override default byte byteValue( long index) {
        long structure = this.countRows();
        return this.byteValue(Structure2D.row(index, structure), Structure2D.column(index, structure));
    }

    default byte byteValue( long row,  long col) {
        return (byte) this.shortValue(row, col);
    }

    default ColumnView<N> columns() {
        return new ColumnView<>(this);
    }

    default Access2D<N> columns( int... columns) {
        return this.select(null, columns);
    }

    default Access2D<N> columns( long... columns) {
        return this.select(null, columns);
    }

    @Override default double doubleValue( long index) {
        long structure = this.countRows();
        long row = Structure2D.row(index, structure);
        long col = Structure2D.column(index, structure);
        return this.doubleValue(row, col);
    }

    /**
     * Extracts one element of this matrix as a double.
     *
     * @param row A row index.
     * @param col A column index.
     * @return One matrix element
     */
    double doubleValue(long row, long col);

    @Override default ElementView2D<N, ?> elements() {
        return new Access2D.ElementView<>(Access1D.super.elements(), this.countRows());
    }

    @Override default float floatValue( long index) {
        long structure = this.countRows();
        return this.floatValue(Structure2D.row(index, structure), Structure2D.column(index, structure));
    }

    default float floatValue( long row,  long col) {
        return (float) this.doubleValue(row, col);
    }

    @Override default N get( long index) {
        long tmpStructure = this.countRows();
        return this.get(Structure2D.row(index, tmpStructure), Structure2D.column(index, tmpStructure));
    }

    N get(long row, long col);

    @Override default int intValue( long index) {
        long structure = this.countRows();
        return this.intValue(Structure2D.row(index, structure), Structure2D.column(index, structure));
    }

    default int intValue( long row,  long col) {
        return (int) this.longValue(row, col);
    }

    @Override default long longValue( long index) {
        long structure = this.countRows();
        return this.longValue(Structure2D.row(index, structure), Structure2D.column(index, structure));
    }

    default long longValue( long row,  long col) {
        return Math.round(this.doubleValue(row, col));
    }

    @Override default ElementView2D<N, ?> nonzeros() {
        return new Access2D.ElementView<>(Access1D.super.nonzeros(), this.countRows());
    }

    default RowView<N> rows() {
        return new RowView<>(this);
    }

    default Access2D<N> rows( int... rows) {
        return this.select(rows, null);
    }

    default Access2D<N> rows( long... rows) {
        return this.select(rows, null);
    }

    default Access2D<N> select( int[] rows,  int[] columns) {
        return new Access2D.SelectionView<>(this, Structure1D.toLongIndexes(rows), Structure1D.toLongIndexes(columns));
    }

    /**
     * Creates a view of the underlying data structure of only the selected elements. If either the rows or
     * columns input arguments are null or empty arrays, then that transaltes to all rows and/or columns.
     */
    default Access2D<N> select( long[] rows,  long[] columns) {
        return new Access2D.SelectionView<>(this, rows, columns);
    }

    @Override default short shortValue( long index) {
        long structure = this.countRows();
        return this.shortValue(Structure2D.row(index, structure), Structure2D.column(index, structure));
    }

    default short shortValue( long row,  long col) {
        return (short) this.intValue(row, col);
    }

    default double[][] toRawCopy2D() {

        var tmpRowDim = (int) this.countRows();
        var tmpColDim = (int) this.countColumns();

        double[][] retVal = new double[tmpRowDim][tmpColDim];

        @Var double[] tmpRow;
        for (int i = 0; i < tmpRowDim; i++) {
            tmpRow = retVal[i];
            for (int j = 0; j < tmpColDim; j++) {
                tmpRow[j] = this.doubleValue(i, j);
            }
        }

        return retVal;
    }
}
