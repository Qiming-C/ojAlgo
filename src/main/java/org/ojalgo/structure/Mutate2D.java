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

import java.util.function.Consumer;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;

/**
 * 2-dimensional mutator methods
 *
 * @author apete
 */
public interface Mutate2D extends Structure2D, Mutate1D {

    /**
     * A few operations with no 1D or AnyD counterpart.
     *
     * @author apete
     */
    interface Exchangeable extends Structure2D {

        void exchangeColumns(long colA, long colB);

        void exchangeRows(long rowA, long rowB);

    }

    interface Fillable<N extends Comparable<N>> extends Mutate2D, Mutate1D.Fillable<N> {

        default void fillColumn( long col,  Access1D<N> values) {
            this.fillColumn(0L, col, values);
        }

        default void fillColumn( long row,  long col,  Access1D<N> values) {
            this.loopColumn(row, col, (r, c) -> this.fillOne(r, c, values.get(r - row)));
        }

        default void fillColumn( long row,  long col,  N value) {
            this.loopColumn(row, col, (r, c) -> this.fillOne(r, c, value));
        }

        default void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
            this.loopColumn(row, col, (r, c) -> this.fillOne(r, c, supplier));
        }

        default void fillColumn( long col,  N value) {
            this.fillColumn(0L, col, value);
        }

        default void fillColumn( long col,  NullaryFunction<?> supplier) {
            this.fillColumn(0L, col, supplier);
        }

        default void fillDiagonal( Access1D<N> values) {
            this.fillDiagonal(0L, 0L, values);
        }

        default void fillDiagonal( long row,  long col,  Access1D<N> values) {
            this.loopDiagonal(row, col, (r, c) -> this.fillOne(r, c, values.get(r - row)));
        }

        default void fillDiagonal( long row,  long col,  N value) {
            this.loopDiagonal(row, col, (r, c) -> this.fillOne(r, c, value));
        }

        default void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
            this.loopDiagonal(row, col, (r, c) -> this.fillOne(r, c, supplier));
        }

        default void fillDiagonal( N value) {
            this.fillDiagonal(0L, 0L, value);
        }

        default void fillDiagonal( NullaryFunction<?> supplier) {
            this.fillDiagonal(0L, 0L, supplier);
        }

        /**
         * @deprecated v52 Use {@link #set(long, Comparable)} instead.
         */
        @Deprecated
        @Override
        default void fillOne( long index,  Access1D<?> values,  long valueIndex) {
            long structure = this.countRows();
            this.fillOne(Structure2D.row(index, structure), Structure2D.column(index, structure), values, valueIndex);
        }

        /**
         * @deprecated v52 Use {@link #set(long, long, Comparable)} instead.
         */
        @Deprecated
        default void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            this.fillOne(row, col, (N) values.get(valueIndex));
        }

        /**
         * @deprecated v52 Use {@link #set(long, long, Comparable)} instead.
         */
        @Deprecated
        default void fillOne( long row,  long col,  N value) {
            this.set(row, col, value);
        }

        /**
         * @deprecated v52 Use {@link #set(long, long, Comparable)} instead.
         */
        @Deprecated
        default void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            this.set(row, col, supplier.get());
        }

        /**
         * @deprecated v52 Use {@link #set(long, Comparable)} instead.
         */
        @Deprecated
        @Override
        default void fillOne( long index,  N value) {
            long structure = this.countRows();
            this.fillOne(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
        }

        /**
         * @deprecated v52 Use {@link #set(long, Comparable)} instead.
         */
        @Deprecated
        @Override
        default void fillOne( long index,  NullaryFunction<?> supplier) {
            long structure = this.countRows();
            this.fillOne(Structure2D.row(index, structure), Structure2D.column(index, structure), supplier);
        }

        default void fillRow( long row,  Access1D<N> values) {
            this.fillRow(row, 0L, values);
        }

        default void fillRow( long row,  long col,  Access1D<N> values) {
            this.loopRow(row, col, (r, c) -> this.fillOne(r, c, values.get(c - col)));
        }

        default void fillRow( long row,  long col,  N value) {
            this.loopRow(row, col, (r, c) -> this.fillOne(r, c, value));
        }

        default void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
            this.loopRow(row, col, (r, c) -> this.fillOne(r, c, supplier));
        }

        default void fillRow( long row,  N value) {
            this.fillRow(row, 0L, value);
        }

        default void fillRow( long row,  NullaryFunction<?> supplier) {
            this.fillRow(row, 0L, supplier);
        }

    }

    interface Mixable<N extends Comparable<N>> extends Structure2D, Mutate1D.Mixable<N> {

        @Override
        default double mix( long index,  BinaryFunction<N> mixer,  double addend) {
            long structure = this.countRows();
            return this.mix(Structure2D.row(index, structure), Structure2D.column(index, structure), mixer, addend);
        }

        @Override
        default N mix( long index,  BinaryFunction<N> mixer,  N addend) {
            long structure = this.countRows();
            return this.mix(Structure2D.row(index, structure), Structure2D.column(index, structure), mixer, addend);
        }

        double mix(long row, long col, BinaryFunction<N> mixer, double addend);

        N mix(long row, long col, BinaryFunction<N> mixer, N addend);
    }

    interface Modifiable<N extends Comparable<N>> extends Structure2D, Mutate1D.Modifiable<N> {

        @Override
        default void add( long index,  byte addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        @Override
        default void add( long index,  Comparable<?> addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        @Override
        default void add( long index,  double addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        @Override
        default void add( long index,  float addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        @Override
        default void add( long index,  int addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        @Override
        default void add( long index,  long addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        default void add( long row,  long col,  byte addend) {
            this.add(row, col, (short) addend);
        }

        void add(long row, long col, Comparable<?> addend);

        void add(long row, long col, double addend);

        default void add( long row,  long col,  float addend) {
            this.add(row, col, (double) addend);
        }

        default void add( long row,  long col,  int addend) {
            this.add(row, col, (long) addend);
        }

        default void add( long row,  long col,  long addend) {
            this.add(row, col, (double) addend);
        }

        default void add( long row,  long col,  short addend) {
            this.add(row, col, (int) addend);
        }

        @Override
        default void add( long index,  short addend) {
            long structure = this.countRows();
            this.add(Structure2D.row(index, structure), Structure2D.column(index, structure), addend);
        }

        default void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
            this.loopColumn(row, col, (r, c) -> this.modifyOne(r, c, modifier));
        }

        default void modifyColumn( long col,  UnaryFunction<N> modifier) {
            this.modifyColumn(0L, col, modifier);
        }

        default void modifyDiagonal( long row,  long col,  UnaryFunction<N> modifier) {
            this.loopDiagonal(row, col, (r, c) -> this.modifyOne(r, c, modifier));
        }

        default void modifyDiagonal( UnaryFunction<N> modifier) {
            this.modifyDiagonal(0L, 0L, modifier);
        }

        default void modifyMatchingInColumns( Access1D<N> left,  BinaryFunction<N> function) {
            left.loopAll(r -> this.modifyRow(r, function.first(left.get(r))));
        }

        default void modifyMatchingInColumns( BinaryFunction<N> function,  Access1D<N> right) {
            right.loopAll(r -> this.modifyRow(r, function.second(right.get(r))));
        }

        default void modifyMatchingInRows( Access1D<N> left,  BinaryFunction<N> function) {
            left.loopAll(c -> this.modifyColumn(c, function.first(left.get(c))));
        }

        default void modifyMatchingInRows( BinaryFunction<N> function,  Access1D<N> right) {
            right.loopAll(c -> this.modifyColumn(c, function.second(right.get(c))));
        }

        void modifyOne(long row, long col, UnaryFunction<N> modifier);

        @Override
        default void modifyOne( long index,  UnaryFunction<N> modifier) {
            long structure = this.countRows();
            this.modifyOne(Structure2D.row(index, structure), Structure2D.column(index, structure), modifier);
        }

        default void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
            this.loopRow(row, col, (r, c) -> this.modifyOne(r, c, modifier));
        }

        default void modifyRow( long row,  UnaryFunction<N> modifier) {
            this.modifyRow(row, 0L, modifier);
        }

    }

    /**
     * Apart from extending {@link Receiver} this interface extends {@link Modifiable} and
     * {@link Exchangeable} which both imply access to existing elements as well as {@link Access2D} that
     * dictates explicit access.
     *
     * @author apete
     */
    interface ModifiableReceiver<N extends Comparable<N>> extends Modifiable<N>, Receiver<N>, Exchangeable, Access2D<N> {

        void modifyAny(Transformation2D<N> modifier);

    }

    interface Receiver<N extends Comparable<N>> extends Mutate2D, Fillable<N>, Consumer<Access2D<?>> {

        @Override
        default void accept( Access2D<?> supplied) {
            if (this.isAcceptable(supplied)) {
                supplied.loopAll((r, c) -> this.set(r, c, supplied.get(r, c)));
            } else {
                throw new ProgrammingError("Not acceptable!");
            }
        }

        default boolean isAcceptable( Structure2D supplier) {
            return (this.countRows() >= supplier.countRows()) && (this.countColumns() >= supplier.countColumns());
        }

    }

    @Override
    default void set( long index,  byte value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    @Override
    default void set( long index,  Comparable<?> value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    @Override
    default void set( long index,  double value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    @Override
    default void set( long index,  float value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    @Override
    default void set( long index,  int value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    @Override
    default void set( long index,  long value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

    default void set( long row,  long col,  byte value) {
        this.set(row, col, (short) value);
    }

    void set(long row, long col, Comparable<?> value);

    void set(long row, long col, double value);

    default void set( long row,  long col,  float value) {
        this.set(row, col, (double) value);
    }

    default void set( long row,  long col,  int value) {
        this.set(row, col, (long) value);
    }

    default void set( long row,  long col,  long value) {
        this.set(row, col, (double) value);
    }

    default void set( long row,  long col,  short value) {
        this.set(row, col, (int) value);
    }

    @Override
    default void set( long index,  short value) {
        long structure = this.countRows();
        this.set(Structure2D.row(index, structure), Structure2D.column(index, structure), value);
    }

}
