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
package org.ojalgo.matrix;

import java.util.function.Supplier;

import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.TransformableRegion;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Transformation2D;

abstract class Mutator2D<N extends Comparable<N>, M extends BasicMatrix<N, M>, MR extends MatrixStore<N> & Mutate2D.ModifiableReceiver<N>>
        implements Mutate2D.ModifiableReceiver<N>, Supplier<M>, Access2D.Collectable<N, TransformableRegion<N>> {

    private final MR myDelegate;
    private boolean mySafe = true;

    Mutator2D( MR delegate) {

        super();

        myDelegate = delegate;
    }

    @Override public void accept( Access2D<?> supplied) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.accept(supplied);
    }

    @Override public void add( long index,  Comparable<?> addend) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.add(index, addend);
    }

    @Override public void add( long index,  double addend) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.add(index, addend);
    }

    @Override public void add( long row,  long col,  Comparable<?> value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.add(row, col, value);
    }

    @Override public void add( long row,  long col,  double value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.add(row, col, value);
    }

    @Override public long count() {
        return myDelegate.count();
    }

    @Override public long countColumns() {
        return myDelegate.countColumns();
    }

    @Override public long countRows() {
        return myDelegate.countRows();
    }

    @Override public double doubleValue( long row,  long col) {
        if (mySafe) {
            return myDelegate.doubleValue(row, col);
        }
        throw new IllegalStateException();
    }

    @Override public void exchangeColumns( long colA,  long colB) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.exchangeColumns(colA, colB);
    }

    @Override public void exchangeRows( long rowA,  long rowB) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.exchangeRows(rowA, rowB);
    }

    @Override public void fillAll( N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillAll(myDelegate.physical().scalar().cast(value));
    }

    @Override public void fillAll( NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillAll(supplier);
    }

    @Override public void fillColumn( long col,  Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(col, values);
    }

    @Override public void fillColumn( long row,  long col,  Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(row, col, values);
    }

    @Override public void fillColumn( long row,  long column,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(row, (int) column, myDelegate.physical().scalar().cast(value));
    }

    @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(row, col, supplier);
    }

    @Override public void fillColumn( long col,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(col, value);
    }

    @Override public void fillColumn( long col,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillColumn(col, supplier);
    }

    @Override public void fillDiagonal( Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(values);
    }

    @Override public void fillDiagonal( long row,  long col,  Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(row, col, values);
    }

    @Override public void fillDiagonal( long row,  long column,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(row, column, myDelegate.physical().scalar().cast(value));
    }

    @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(row, col, supplier);
    }

    @Override public void fillDiagonal( N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(value);
    }

    @Override public void fillDiagonal( NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillDiagonal(supplier);
    }

    @Override public void fillMatching( Access1D<?> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillMatching(values);
    }

    @Override public void fillMatching( Access1D<N> left,  BinaryFunction<N> function,  Access1D<N> right) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillMatching(left, function, right);
    }

    @Override public void fillMatching( UnaryFunction<N> function,  Access1D<N> arguments) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillMatching(function, arguments);
    }

    public void fillOne( long index,  Access1D<?> values,  long valueIndex) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(index, values, valueIndex);
    }

    public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(row, col, values, valueIndex);
    }

    public void fillOne( long row,  long col,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(row, col, value);
    }

    public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(row, col, supplier);
    }

    public void fillOne( long index,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(index, value);
    }

    public void fillOne( long index,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillOne(index, supplier);
    }

    @Override public void fillRange( long first,  long limit,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRange(first, limit, value);
    }

    @Override public void fillRange( long first,  long limit,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRange(first, limit, supplier);
    }

    @Override public void fillRow( long row,  Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, values);
    }

    @Override public void fillRow( long row,  long col,  Access1D<N> values) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, col, values);
    }

    @Override public void fillRow( long row,  long column,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, (int) column, myDelegate.physical().scalar().cast(value));
    }

    @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, col, supplier);
    }

    @Override public void fillRow( long row,  N value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, value);
    }

    @Override public void fillRow( long row,  NullaryFunction<?> supplier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.fillRow(row, supplier);
    }

    @Override
    public M get() {
        mySafe = false;
        return this.instantiate(myDelegate);
    }

    @Override public N get( long row,  long col) {
        if (mySafe) {
            return myDelegate.get(row, col);
        }
        throw new IllegalStateException();
    }

    @Override public void modifyAll( UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyAll(modifier);
    }

    @Override public void modifyAny( Transformation2D<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        modifier.transform(myDelegate);
    }

    @Override public void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyColumn(row, col, modifier);
    }

    @Override public void modifyColumn( long col,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyColumn(col, modifier);
    }

    @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyDiagonal(row, col, modifier);
    }

    @Override public void modifyDiagonal( UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyDiagonal(modifier);
    }

    @Override public void modifyMatching( Access1D<N> left,  BinaryFunction<N> function) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatching(left, function);
    }

    @Override public void modifyMatching( BinaryFunction<N> function,  Access1D<N> right) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatching(function, right);
    }

    @Override public void modifyMatchingInColumns( Access1D<N> left,  BinaryFunction<N> function) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatchingInColumns(left, function);
    }

    @Override public void modifyMatchingInColumns( BinaryFunction<N> function,  Access1D<N> right) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatchingInColumns(function, right);
    }

    @Override public void modifyMatchingInRows( Access1D<N> left,  BinaryFunction<N> function) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatchingInRows(left, function);
    }

    @Override public void modifyMatchingInRows( BinaryFunction<N> function,  Access1D<N> right) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyMatchingInRows(function, right);
    }

    @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyOne(row, col, modifier);
    }

    @Override public void modifyOne( long index,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyOne(index, modifier);
    }

    @Override public void modifyRange( long first,  long limit,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyRange(first, limit, modifier);
    }

    @Override public void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyRow(row, col, modifier);
    }

    @Override public void modifyRow( long row,  UnaryFunction<N> modifier) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.modifyRow(row, modifier);
    }

    @Override public void reset() {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.reset();
    }

    @Override public void set( long index,  Comparable<?> value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.set(index, myDelegate.physical().scalar().cast(value));
    }

    @Override public void set( long index,  double value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.set(index, value);
    }

    @Override public void set( long row,  long col,  Comparable<?> value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.set(row, col, value);
    }

    @Override public void set( long row,  long col,  double value) {
        if (!mySafe) {
            throw new IllegalStateException();
        }
        myDelegate.set(row, col, value);
    }

    @Override public void supplyTo( TransformableRegion<N> receiver) {
        myDelegate.supplyTo(receiver);
    }

    abstract M instantiate(MatrixStore<N> store);

}
