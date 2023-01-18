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

import com.google.errorprone.annotations.Var;
import java.util.Arrays;
import org.ojalgo.ProgrammingError;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.matrix.operation.MultiplyBoth;
import org.ojalgo.structure.Access1D;

abstract class Subregion2D<N extends Comparable<N>> implements TransformableRegion<N> {

    static final class ColumnsRegion<N extends Comparable<N>> extends Subregion2D<N> {

        private final TransformableRegion<N> myBase;
        private final int[] myColumns;

        /**
         * 
         * 
         * 
         */
        ColumnsRegion( TransformableRegion<N> base,  TransformableRegion.FillByMultiplying<N> multiplier,  int... columns) {
            super(multiplier, base.countRows(), columns.length);
            myBase = base;
            myColumns = columns;
        }

        @Override public void add( long row,  long col,  Comparable<?> addend) {
            myBase.add(row, myColumns[(int) col], addend);
        }

        @Override public void add( long row,  long col,  double addend) {
            myBase.add(row, myColumns[(int) col], addend);
        }

        @Override public long countColumns() {
            return myColumns.length;
        }

        @Override public long countRows() {
            return myBase.countRows();
        }

        @Override public double doubleValue( long row,  long col) {
            return myBase.doubleValue(row, myColumns[(int) col]);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ColumnsRegion)) {
                return false;
            }
            var other = (ColumnsRegion) obj;
            if (myBase == null) {
                if (other.myBase != null) {
                    return false;
                }
            } else if (!myBase.equals(other.myBase)) {
                return false;
            }
            if (!Arrays.equals(myColumns, other.myColumns)) {
                return false;
            }
            return true;
        }

        @Override public void fillColumn( long row,  long col,  Access1D<N> values) {
            myBase.fillColumn(row, myColumns[(int) col], values);
        }

        @Override public void fillColumn( long row,  long col,  N value) {
            myBase.fillColumn(row, myColumns[(int) col], value);
        }

        @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillColumn(row, myColumns[(int) col], supplier);
        }

        public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            myBase.fillOne(row, myColumns[(int) col], values, valueIndex);
        }

        public void fillOne( long row,  long col,  N value) {
            myBase.fillOne(row, myColumns[(int) col], value);
        }

        public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillOne(row, myColumns[(int) col], supplier);
        }

        @Override public N get( long row,  long col) {
            return myBase.get(row, myColumns[(int) col]);
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myBase == null ? 0 : myBase.hashCode());
            result = prime * result + Arrays.hashCode(myColumns);
            return result;
        }

        @Override public void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyColumn(row, myColumns[(int) col], modifier);
        }

        @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyOne(row, myColumns[(int) col], modifier);
        }

        @Override public void set( long row,  long col,  Comparable<?> value) {
            myBase.set(row, myColumns[(int) col], value);
        }

        @Override public void set( long row,  long col,  double value) {
            myBase.set(row, myColumns[(int) col], value);
        }

    }

    static final class LimitRegion<N extends Comparable<N>> extends Subregion2D<N> {

        private final TransformableRegion<N> myBase;
        private final int myRowLimit, myColumnLimit; // limits

        LimitRegion( TransformableRegion<N> base,  TransformableRegion.FillByMultiplying<N> multiplier,  int rowLimit,  int columnLimit) {
            super(multiplier, rowLimit, columnLimit);
            myBase = base;
            myRowLimit = rowLimit;
            myColumnLimit = columnLimit;
        }

        @Override public void add( long row,  long col,  Comparable<?> addend) {
            myBase.add(row, col, addend);
        }

        @Override public void add( long row,  long col,  double addend) {
            myBase.add(row, col, addend);
        }

        @Override public long countColumns() {
            return myColumnLimit;
        }

        @Override public long countRows() {
            return myRowLimit;
        }

        @Override public double doubleValue( long row,  long col) {
            return myBase.doubleValue(row, col);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LimitRegion)) {
                return false;
            }
            var other = (LimitRegion) obj;
            if (myBase == null) {
                if (other.myBase != null) {
                    return false;
                }
            } else if (!myBase.equals(other.myBase)) {
                return false;
            }
            if (myColumnLimit != other.myColumnLimit || myRowLimit != other.myRowLimit) {
                return false;
            }
            return true;
        }

        public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            myBase.fillOne(row, col, values, valueIndex);
        }

        public void fillOne( long row,  long col,  N value) {
            myBase.fillOne(row, col, value);
        }

        public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillOne(row, col, supplier);
        }

        @Override public N get( long row,  long col) {
            return myBase.get(row, col);
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myBase == null ? 0 : myBase.hashCode());
            result = prime * result + myColumnLimit;
            result = prime * result + myRowLimit;
            return result;
        }

        @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyOne(row, col, modifier);
        }

        @Override public void set( long row,  long col,  Comparable<?> value) {
            myBase.set(row, col, value);
        }

        @Override public void set( long row,  long col,  double value) {
            myBase.set(row, col, value);
        }

    }

    static final class OffsetRegion<N extends Comparable<N>> extends Subregion2D<N> {

        private final TransformableRegion<N> myBase;
        private final int myRowOffset, myColumnOffset; // origin/offset

        OffsetRegion( TransformableRegion<N> base,  TransformableRegion.FillByMultiplying<N> multiplier,  int rowOffset,
                 int columnOffset) {
            super(multiplier, base.countRows() - rowOffset, base.countColumns() - columnOffset);
            myBase = base;
            myRowOffset = rowOffset;
            myColumnOffset = columnOffset;
        }

        @Override public void add( long row,  long col,  Comparable<?> addend) {
            myBase.add(myRowOffset + row, myColumnOffset + col, addend);
        }

        @Override public void add( long row,  long col,  double addend) {
            myBase.add(myRowOffset + row, myColumnOffset + col, addend);
        }

        @Override public long countColumns() {
            return myBase.countColumns() - myColumnOffset;
        }

        @Override public long countRows() {
            return myBase.countRows() - myRowOffset;
        }

        @Override public double doubleValue( long row,  long col) {
            return myBase.doubleValue(myRowOffset + row, myColumnOffset + col);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OffsetRegion)) {
                return false;
            }
            var other = (OffsetRegion) obj;
            if (myBase == null) {
                if (other.myBase != null) {
                    return false;
                }
            } else if (!myBase.equals(other.myBase)) {
                return false;
            }
            if (myColumnOffset != other.myColumnOffset || myRowOffset != other.myRowOffset) {
                return false;
            }
            return true;
        }

        @Override
        public void fillAll( N value) {
             long tmpCountColumns = myBase.countColumns();
            for (long j = myColumnOffset; j < tmpCountColumns; j++) {
                myBase.fillColumn(myRowOffset, j, value);
            }
        }

        @Override
        public void fillAll( NullaryFunction<?> supplier) {
             long tmpCountColumns = myBase.countColumns();
            for (long j = myColumnOffset; j < tmpCountColumns; j++) {
                myBase.fillColumn(myRowOffset, j, supplier);
            }
        }

        @Override public void fillColumn( long row,  long col,  N value) {
            myBase.fillColumn(myRowOffset + row, myColumnOffset + col, value);
        }

        @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillColumn(myRowOffset + row, myColumnOffset + col, supplier);
        }

        @Override public void fillDiagonal( long row,  long col,  N value) {
            myBase.fillDiagonal(myRowOffset + row, myColumnOffset + col, value);
        }

        @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillDiagonal(myRowOffset + row, myColumnOffset + col, supplier);
        }

        public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            myBase.fillOne(myRowOffset + row, myColumnOffset + col, values, valueIndex);
        }

        public void fillOne( long row,  long col,  N value) {
            myBase.fillOne(myRowOffset + row, myColumnOffset + col, value);
        }

        public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillOne(myRowOffset + row, myColumnOffset + col, supplier);
        }

        @Override public void fillRow( long row,  long col,  N value) {
            myBase.fillRow(myRowOffset + row, myColumnOffset + col, value);
        }

        @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillRow(myRowOffset + row, myColumnOffset + col, supplier);
        }

        @Override public N get( long row,  long col) {
            return myBase.get(myRowOffset + row, myColumnOffset + col);
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myBase == null ? 0 : myBase.hashCode());
            result = prime * result + myColumnOffset;
            result = prime * result + myRowOffset;
            return result;
        }

        @Override public void modifyAll( UnaryFunction<N> modifier) {
            for (long j = myColumnOffset; j < myBase.countColumns(); j++) {
                myBase.modifyColumn(myRowOffset, j, modifier);
            }
        }

        @Override public void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyColumn(myRowOffset + row, myColumnOffset + col, modifier);
        }

        @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyDiagonal(myRowOffset + row, myColumnOffset + col, modifier);
        }

        @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyOne(myRowOffset + row, myColumnOffset + col, modifier);
        }

        @Override public void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyRow(myRowOffset + row, myColumnOffset + col, modifier);
        }

        @Override public void set( long row,  long col,  Comparable<?> value) {
            myBase.set(myRowOffset + row, myColumnOffset + col, value);
        }

        @Override public void set( long row,  long col,  double value) {
            myBase.set(myRowOffset + row, myColumnOffset + col, value);
        }

    }

    static final class RowsRegion<N extends Comparable<N>> extends Subregion2D<N> {

        private final TransformableRegion<N> myBase;
        private final int[] myRows;

        RowsRegion( TransformableRegion<N> base,  TransformableRegion.FillByMultiplying<N> multiplier,  int... rows) {
            super(multiplier, rows.length, base.countColumns());
            myBase = base;
            myRows = rows;
        }

        @Override public void add( long row,  long col,  Comparable<?> addend) {
            myBase.add(myRows[(int) row], col, addend);
        }

        @Override public void add( long row,  long col,  double addend) {
            myBase.add(myRows[(int) row], col, addend);
        }

        @Override public long countColumns() {
            return myBase.countColumns();
        }

        @Override public long countRows() {
            return myRows.length;
        }

        @Override public double doubleValue( long row,  long col) {
            return myBase.doubleValue(myRows[(int) row], col);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RowsRegion)) {
                return false;
            }
            var other = (RowsRegion) obj;
            if (myBase == null) {
                if (other.myBase != null) {
                    return false;
                }
            } else if (!myBase.equals(other.myBase)) {
                return false;
            }
            if (!Arrays.equals(myRows, other.myRows)) {
                return false;
            }
            return true;
        }

        public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            myBase.fillOne(myRows[(int) row], col, values, valueIndex);
        }

        public void fillOne( long row,  long col,  N value) {
            myBase.fillOne(myRows[(int) row], col, value);
        }

        public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillOne(myRows[(int) row], col, supplier);
        }

        @Override public void fillRow( long row,  long col,  Access1D<N> values) {
            myBase.fillRow(myRows[(int) row], col, values);
        }

        @Override public void fillRow( long row,  long col,  N value) {
            myBase.fillRow(myRows[(int) row], col, value);
        }

        @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillRow(myRows[(int) row], col, supplier);
        }

        @Override public N get( long row,  long col) {
            return myBase.get(myRows[(int) row], col);
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myBase == null ? 0 : myBase.hashCode());
            result = prime * result + Arrays.hashCode(myRows);
            return result;
        }

        @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyOne(myRows[(int) row], col, modifier);
        }

        @Override public void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyRow(myRows[(int) row], col, modifier);
        }

        @Override public void set( long row,  long col,  Comparable<?> value) {
            myBase.set(myRows[(int) row], col, value);
        }

        @Override public void set( long row,  long col,  double value) {
            myBase.set(myRows[(int) row], col, value);
        }

    }

    static final class TransposedRegion<N extends Comparable<N>> extends Subregion2D<N> {

        private final TransformableRegion<N> myBase;

        TransposedRegion( TransformableRegion<N> base,  TransformableRegion.FillByMultiplying<N> multiplier) {
            super(multiplier, base.countColumns(), base.countRows());
            myBase = base;
        }

        @Override public void add( long row,  long col,  Comparable<?> addend) {
            myBase.add(col, row, addend);
        }

        @Override public void add( long row,  long col,  double addend) {
            myBase.add(col, row, addend);
        }

        @Override public long countColumns() {
            return myBase.countRows();
        }

        @Override public long countRows() {
            return myBase.countColumns();
        }

        @Override public double doubleValue( long row,  long col) {
            return myBase.doubleValue(col, row);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TransposedRegion)) {
                return false;
            }
            var other = (TransposedRegion) obj;
            if (myBase == null) {
                if (other.myBase != null) {
                    return false;
                }
            } else if (!myBase.equals(other.myBase)) {
                return false;
            }
            return true;
        }

        @Override public void fillColumn( long row,  long col,  N value) {
            myBase.fillRow(col, row, value);
        }

        @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillRow(col, row, supplier);
        }

        @Override public void fillDiagonal( long row,  long col,  N value) {
            myBase.fillDiagonal(col, row, value);
        }

        @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillRow(col, row, supplier);
        }

        public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
            myBase.fillOne(col, row, values, valueIndex);
        }

        public void fillOne( long row,  long col,  N value) {
            myBase.fillOne(col, row, value);
        }

        public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillOne(col, row, supplier);
        }

        @Override public void fillRow( long row,  long col,  N value) {
            myBase.fillDiagonal(col, row, value);
        }

        @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
            myBase.fillDiagonal(col, row, supplier);
        }

        @Override public N get( long row,  long col) {
            return myBase.get(col, row);
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + (myBase == null ? 0 : myBase.hashCode());
            return result;
        }

        @Override public void modifyColumn( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyRow(col, row, modifier);
        }

        @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyDiagonal(col, row, modifier);
        }

        @Override public void modifyOne( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyOne(col, row, modifier);
        }

        @Override public void modifyRow( long row,  long col,  UnaryFunction<N> modifier) {
            myBase.modifyColumn(col, row, modifier);
        }

        @Override
        public TransformableRegion<N> regionByTransposing() {
            return myBase;
        }

        @Override public void set( long row,  long col,  Comparable<?> value) {
            myBase.set(col, row, value);
        }

        @Override public void set( long row,  long col,  double value) {
            myBase.set(col, row, value);
        }

    }

    private final TransformableRegion.FillByMultiplying<N> myMultiplier;

    @SuppressWarnings("unused")
    private Subregion2D() {
        this(null, 0L, 0L);
    }

    @SuppressWarnings("unchecked")
    Subregion2D( TransformableRegion.FillByMultiplying<N> multiplier,  long rows,  long columns) {

        super();

        if (multiplier instanceof MultiplyBoth.Primitive) {
            myMultiplier = (TransformableRegion.FillByMultiplying<N>) MultiplyBoth.newPrimitive64(Math.toIntExact(rows), Math.toIntExact(columns));
        } else if (multiplier instanceof MultiplyBoth.Generic) {
            myMultiplier = (TransformableRegion.FillByMultiplying<N>) MultiplyBoth.newGeneric(Math.toIntExact(rows), Math.toIntExact(columns));
        } else {
            myMultiplier = multiplier;
        }
    }

    @Override public final void fillByMultiplying( Access1D<N> left,  Access1D<N> right) {

         int complexity = Math.toIntExact(left.count() / this.countRows());
        if (complexity != Math.toIntExact(right.count() / this.countColumns())) {
            ProgrammingError.throwForMultiplicationNotPossible();
        }

        myMultiplier.invoke(this, left, (int) (left.count() / this.countRows()), right);
    }

    @Override public final TransformableRegion<N> regionByColumns( int... columns) {
        return new Subregion2D.ColumnsRegion<>(this, myMultiplier, columns);
    }

    @Override public final TransformableRegion<N> regionByLimits( int rowLimit,  int columnLimit) {
        return new Subregion2D.LimitRegion<>(this, myMultiplier, rowLimit, columnLimit);
    }

    @Override public final TransformableRegion<N> regionByOffsets( int rowOffset,  int columnOffset) {
        return new Subregion2D.OffsetRegion<>(this, myMultiplier, rowOffset, columnOffset);
    }

    @Override public final TransformableRegion<N> regionByRows( int... rows) {
        return new Subregion2D.RowsRegion<>(this, myMultiplier, rows);
    }

    @Override public TransformableRegion<N> regionByTransposing() {
        return new Subregion2D.TransposedRegion<>(this, myMultiplier);
    }

    @Override
    public String toString() {
        return Access1D.toString(this);
    }

}
