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
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.ArrayR032;
import org.ojalgo.array.DenseArray;
import org.ojalgo.array.operation.FillMatchingSingle;
import org.ojalgo.array.operation.RotateLeft;
import org.ojalgo.array.operation.RotateRight;
import org.ojalgo.array.operation.SubstituteBackwards;
import org.ojalgo.array.operation.SubstituteForwards;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.operation.HouseholderLeft;
import org.ojalgo.matrix.operation.HouseholderRight;
import org.ojalgo.matrix.operation.MultiplyBoth;
import org.ojalgo.matrix.operation.MultiplyLeft;
import org.ojalgo.matrix.operation.MultiplyNeither;
import org.ojalgo.matrix.operation.MultiplyRight;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.HouseholderReference;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.ElementView2D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.structure.Transformation2D;
import org.ojalgo.type.NumberDefinition;

/**
 * A {@linkplain float} implementation of {@linkplain PhysicalStore}.
 *
 * @author apete
 */
public final class Primitive32Store extends ArrayR032 implements PhysicalStore<Double> {

    public static final PhysicalStore.Factory<Double, Primitive32Store> FACTORY = new PrimitiveFactory<Primitive32Store>() {

        @Override
        public DenseArray.Factory<Double> array() {
            return ArrayR032.FACTORY;
        }

        @Override public Primitive32Store columns( Access1D<?>... source) {

             var tmpRowDim = (int) source[0].count();
             int tmpColDim = source.length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var Access1D<?> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = tmpColumn.floatValue(i);
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store columns( Comparable<?>[]... source) {

             int tmpRowDim = source[0].length;
             int tmpColDim = source.length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var Comparable<?>[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.floatValue(tmpColumn[i]);
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store columns( double[]... source) {

             int tmpRowDim = source[0].length;
             int tmpColDim = source.length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var double[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = (float) tmpColumn[i];
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store columns( List<? extends Comparable<?>>... source) {

             int tmpRowDim = source[0].size();
             int tmpColDim = source.length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var List<? extends Comparable<?>> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.floatValue(tmpColumn.get(i));
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store copy( Access2D<?> source) {

             var tmpRowDim = (int) source.countRows();
             var tmpColDim = (int) source.countColumns();

             var retVal = new Primitive32Store(tmpRowDim, tmpColDim);

            if (tmpColDim > FillMatchingSingle.THRESHOLD) {

                 var tmpConquerer = new DivideAndConquer() {

                    @Override
                    public void conquer( int aFirst,  int aLimit) {
                        FillMatchingSingle.copy(retVal.data, tmpRowDim, aFirst, aLimit, source);
                    }

                };

                tmpConquerer.invoke(0, tmpColDim, FillMatchingSingle.THRESHOLD);

            } else {

                FillMatchingSingle.copy(retVal.data, tmpRowDim, 0, tmpColDim, source);
            }

            return retVal;
        }

        @Override public Primitive32Store make( long rows,  long columns) {
            return new Primitive32Store((int) rows, (int) columns);
        }

        @Override
        public Householder<Double> makeHouseholder( int length) {
            return new Householder.Primitive32(length);
        }

        @Override public Primitive32Store rows( Access1D<?>... source) {

             int tmpRowDim = source.length;
             var tmpColDim = (int) source[0].count();

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var Access1D<?> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = tmpRow.floatValue(j);
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store rows( Comparable<?>[]... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var Comparable<?>[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.floatValue(tmpRow[j]);
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store rows( double[]... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].length;

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var double[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = (float) tmpRow[j];
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store rows( List<? extends Comparable<?>>... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].size();

             float[] tmpData = new float[tmpRowDim * tmpColDim];

            @Var List<? extends Comparable<?>> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.floatValue(tmpRow.get(j));
                }
            }

            return new Primitive32Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive32Store transpose( Access2D<?> source) {

             var retVal = new Primitive32Store((int) source.countColumns(), (int) source.countRows());

             int tmpRowDim = retVal.getRowDim();
             int tmpColDim = retVal.getColDim();

            if (tmpColDim > FillMatchingSingle.THRESHOLD) {

                 var tmpConquerer = new DivideAndConquer() {

                    @Override
                    public void conquer( int first,  int limit) {
                        FillMatchingSingle.transpose(retVal.data, tmpRowDim, first, limit, source);
                    }

                };

                tmpConquerer.invoke(0, tmpColDim, FillMatchingSingle.THRESHOLD);

            } else {

                FillMatchingSingle.transpose(retVal.data, tmpRowDim, 0, tmpColDim, source);
            }

            return retVal;
        }

    };

    static Primitive32Store cast( Access1D<Double> matrix) {
        if (matrix instanceof Primitive32Store) {
            return (Primitive32Store) matrix;
        }
        if (matrix instanceof Access2D<?>) {
            return FACTORY.copy((Access2D<?>) matrix);
        }
        return FACTORY.columns(matrix);
    }

    static Householder.Primitive32 cast( Householder<Double> transformation) {
        if (transformation instanceof Householder.Primitive32) {
            return (Householder.Primitive32) transformation;
        }
        if (transformation instanceof HouseholderReference<?>) {
            return ((Householder.Primitive32) ((HouseholderReference<Double>) transformation).getWorker(FACTORY)).copy(transformation);
        }
        return new Householder.Primitive32(transformation);
    }

    static Rotation.Primitive cast( Rotation<Double> transformation) {
        if (transformation instanceof Rotation.Primitive) {
            return (Rotation.Primitive) transformation;
        }
        return new Rotation.Primitive(transformation);
    }

    private final MultiplyBoth.Primitive multiplyBoth;
    private final MultiplyLeft.Primitive32 multiplyLeft;
    private final MultiplyNeither.Primitive32 multiplyNeither;
    private final MultiplyRight.Primitive32 multiplyRight;
    private final int myColDim;
    private final int myRowDim;
    private final Array2D<Double> myUtility;

    private transient float[] myWorkerColumn;

    Primitive32Store( int numbRows,  int numbCols,  float[] dataArray) {

        super(dataArray);

        myRowDim = numbRows;
        myColDim = numbCols;

        myUtility = this.wrapInArray2D(myRowDim);

        multiplyBoth = MultiplyBoth.newPrimitive32(myRowDim, myColDim);
        multiplyLeft = MultiplyLeft.newPrimitive32(myRowDim, myColDim);
        multiplyRight = MultiplyRight.newPrimitive32(myRowDim, myColDim);
        multiplyNeither = MultiplyNeither.newPrimitive32(myRowDim, myColDim);
    }

    Primitive32Store( long numbRows,  long numbCols) {

        super(Math.toIntExact(numbRows * numbCols));

        myRowDim = Math.toIntExact(numbRows);
        myColDim = Math.toIntExact(numbCols);

        myUtility = this.wrapInArray2D(myRowDim);

        multiplyBoth = MultiplyBoth.newPrimitive32(myRowDim, myColDim);
        multiplyLeft = MultiplyLeft.newPrimitive32(myRowDim, myColDim);
        multiplyRight = MultiplyRight.newPrimitive32(myRowDim, myColDim);
        multiplyNeither = MultiplyNeither.newPrimitive32(myRowDim, myColDim);
    }

    @Override public void accept( Access2D<?> supplied) {
        myUtility.accept(supplied);
    }

    @Override public void add( long row,  long col,  Comparable<?> addend) {
        myUtility.add(row, col, addend);
    }

    @Override public void add( long row,  long col,  double addend) {
        myUtility.add(row, col, addend);
    }

    @Override public Double aggregateColumn( long col,  Aggregator aggregator) {
        return myUtility.aggregateColumn(col, aggregator);
    }

    @Override public Double aggregateColumn( long row,  long col,  Aggregator aggregator) {
        return myUtility.aggregateColumn(row, col, aggregator);
    }

    @Override public Double aggregateDiagonal( Aggregator aggregator) {
        return myUtility.aggregateDiagonal(aggregator);
    }

    @Override public Double aggregateDiagonal( long row,  long col,  Aggregator aggregator) {
        return myUtility.aggregateDiagonal(row, col, aggregator);
    }

    @Override
    public Double aggregateRange( long first,  long limit,  Aggregator aggregator) {
        return myUtility.aggregateRange(first, limit, aggregator);
    }

    @Override public Double aggregateRow( long row,  Aggregator aggregator) {
        return myUtility.aggregateRow(row, aggregator);
    }

    @Override public Double aggregateRow( long row,  long col,  Aggregator aggregator) {
        return myUtility.aggregateRow(row, col, aggregator);
    }

    @Override public <NN extends Comparable<NN>, R extends Mutate2D.Receiver<NN>> Access2D.Collectable<NN, R> asCollectable2D() {
        return myUtility.asCollectable2D();
    }

    @Override public Array1D<Double> asList() {
        return myUtility.flatten();
    }

    @Override public byte byteValue( long row,  long col) {
        return myUtility.byteValue(row, col);
    }

    @Override public ColumnView<Double> columns() {
        return myUtility.columns();
    }

    @Override public MatrixStore<Double> conjugate() {
        return this.transpose();
    }

    @Override public long countColumns() {
        return myColDim;
    }

    @Override public long countRows() {
        return myRowDim;
    }

    @Override
    public double dot( Access1D<?> vector) {
        return myUtility.dot(vector);
    }

    @Override public double doubleValue( long row,  long col) {
        return myUtility.doubleValue(row, col);
    }

    @Override public ElementView2D<Double, ?> elements() {
        return myUtility.elements();
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Primitive32Store)) {
            return false;
        }
        var other = (Primitive32Store) obj;
        if (myColDim != other.myColDim || myRowDim != other.myRowDim) {
            return false;
        }
        return true;
    }

    @Override public void exchangeColumns( long colA,  long colB) {
        myUtility.exchangeColumns(colA, colB);
    }

    @Override public void exchangeRows( long rowA,  long rowB) {
        myUtility.exchangeRows(rowA, rowB);
    }

    @Override public void fillByMultiplying( Access1D<Double> left,  Access1D<Double> right) {

        int complexity = Math.toIntExact(left.count() / this.countRows());
        if (complexity != Math.toIntExact(right.count() / this.countColumns())) {
            ProgrammingError.throwForMultiplicationNotPossible();
        }

        if (left instanceof Primitive32Store) {
            if (right instanceof Primitive32Store) {
                multiplyNeither.invoke(data, Primitive32Store.cast(left).data, complexity, Primitive32Store.cast(right).data);
            } else {
                multiplyRight.invoke(data, Primitive32Store.cast(left).data, complexity, right);
            }
        } else if (right instanceof Primitive32Store) {
            multiplyLeft.invoke(data, left, complexity, Primitive32Store.cast(right).data);
        } else {
            multiplyBoth.invoke(this, left, complexity, right);
        }
    }

    @Override public void fillColumn( long col,  Access1D<Double> values) {
        myUtility.fillColumn(col, values);
    }

    @Override public void fillColumn( long col,  Double value) {
        myUtility.fillColumn(col, value);
    }

    @Override public void fillColumn( long row,  long col,  Access1D<Double> values) {
        myUtility.fillColumn(row, col, values);
    }

    @Override public void fillColumn( long row,  long col,  Double value) {
        myUtility.fillColumn(row, col, value);
    }

    @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillColumn(row, col, supplier);
    }

    @Override public void fillColumn( long col,  NullaryFunction<?> supplier) {
        myUtility.fillColumn(col, supplier);
    }

    @Override public void fillDiagonal( Access1D<Double> values) {
        myUtility.fillDiagonal(values);
    }

    @Override public void fillDiagonal( Double value) {
        myUtility.fillDiagonal(value);
    }

    @Override public void fillDiagonal( long row,  long col,  Access1D<Double> values) {
        myUtility.fillDiagonal(row, col, values);
    }

    @Override public void fillDiagonal( long row,  long col,  Double value) {
        myUtility.fillDiagonal(row, col, value);
    }

    @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillDiagonal(row, col, supplier);
    }

    @Override public void fillDiagonal( NullaryFunction<?> supplier) {
        myUtility.fillDiagonal(supplier);
    }

    @Override
    public void fillMatching( Access1D<Double> left,  BinaryFunction<Double> function,  Access1D<Double> right) {
        myUtility.fillMatching(left, function, right);
    }

    @Override
    public void fillMatching( UnaryFunction<Double> function,  Access1D<Double> arguments) {
        myUtility.fillMatching(function, arguments);
    }

    public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
        myUtility.fillOne(row, col, values, valueIndex);
    }

    public void fillOne( long row,  long col,  Double value) {
        myUtility.fillOne(row, col, value);
    }

    public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillOne(row, col, supplier);
    }

    @Override public void fillRow( long row,  Access1D<Double> values) {
        myUtility.fillRow(row, values);
    }

    @Override public void fillRow( long row,  Double value) {
        myUtility.fillRow(row, value);
    }

    @Override public void fillRow( long row,  long col,  Access1D<Double> values) {
        myUtility.fillRow(row, col, values);
    }

    @Override public void fillRow( long row,  long col,  Double value) {
        myUtility.fillRow(row, col, value);
    }

    @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillRow(row, col, supplier);
    }

    @Override public void fillRow( long row,  NullaryFunction<?> supplier) {
        myUtility.fillRow(row, supplier);
    }

    @Override public float floatValue( long row,  long col) {
        return myUtility.floatValue(row, col);
    }

    @Override public Double get( long row,  long col) {
        return myUtility.get(row, col);
    }

    @Override public int getColDim() {
        return myColDim;
    }

    @Override public int getMaxDim() {
        return Math.max(myRowDim, myColDim);
    }

    @Override public int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    @Override public int getRowDim() {
        return myRowDim;
    }

    @Override
    public int hashCode() {
         int prime = 31;
        @Var int result = super.hashCode();
        result = prime * result + myColDim;
        return prime * result + myRowDim;
    }

    @Override public int intValue( long row,  long col) {
        return myUtility.intValue(row, col);
    }

    @Override public boolean isAcceptable( Structure2D supplier) {
        return myUtility.isAcceptable(supplier);
    }

    @Override public boolean isEmpty() {
        return myUtility.isEmpty();
    }

    @Override public boolean isFat() {
        return myUtility.isFat();
    }

    @Override public boolean isScalar() {
        return myUtility.isScalar();
    }

    @Override public boolean isSquare() {
        return myUtility.isSquare();
    }

    @Override public boolean isTall() {
        return myUtility.isTall();
    }

    @Override public boolean isVector() {
        return myUtility.isVector();
    }

    @Override public long longValue( long row,  long col) {
        return myUtility.longValue(row, col);
    }

    @Override
    public void modifyAll( UnaryFunction<Double> modifier) {

        this.modify(0, myRowDim * myColDim, 1, modifier);

        //        if (myColDim > ModifyAll.THRESHOLD) {
        //
        //            final DivideAndConquer conquerer = new DivideAndConquer() {
        //
        //                @Override
        //                public void conquer(final int first, final int limit) {
        //                    Primitive32Store.this.modify(myRowDim * first, myRowDim * limit, 1, modifier);
        //                }
        //
        //            };
        //
        //            conquerer.invoke(0, myColDim, ModifyAll.THRESHOLD);
        //
        //        } else {
        //
        //            this.modify(0, myRowDim * myColDim, 1, modifier);
        //        }
    }

    @Override public void modifyAny( Transformation2D<Double> modifier) {
        myUtility.modifyAny(modifier);
    }

    @Override public void modifyColumn( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyColumn(row, col, modifier);
    }

    @Override public void modifyColumn( long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyColumn(col, modifier);
    }

    @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyDiagonal(row, col, modifier);
    }

    @Override public void modifyDiagonal( UnaryFunction<Double> modifier) {
        myUtility.modifyDiagonal(modifier);
    }

    @Override
    public void modifyMatching( Access1D<Double> left,  BinaryFunction<Double> function) {
        myUtility.modifyMatching(left, function);
    }

    @Override
    public void modifyMatching( BinaryFunction<Double> function,  Access1D<Double> right) {
        myUtility.modifyMatching(function, right);
    }

    @Override public void modifyMatchingInColumns( Access1D<Double> left,  BinaryFunction<Double> function) {
        myUtility.modifyMatchingInColumns(left, function);
    }

    @Override public void modifyMatchingInColumns( BinaryFunction<Double> function,  Access1D<Double> right) {
        myUtility.modifyMatchingInColumns(function, right);
    }

    @Override public void modifyMatchingInRows( Access1D<Double> left,  BinaryFunction<Double> function) {
        myUtility.modifyMatchingInRows(left, function);
    }

    @Override public void modifyMatchingInRows( BinaryFunction<Double> function,  Access1D<Double> right) {
        myUtility.modifyMatchingInRows(function, right);
    }

    @Override public void modifyOne( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyOne(row, col, modifier);
    }

    @Override public void modifyRow( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyRow(row, col, modifier);
    }

    @Override public void modifyRow( long row,  UnaryFunction<Double> modifier) {
        myUtility.modifyRow(row, modifier);
    }

    @Override public MatrixStore<Double> multiply( MatrixStore<Double> right) {

        Primitive32Store retVal = FACTORY.make(myRowDim, right.countColumns());

        if (right instanceof Primitive32Store) {
            retVal.multiplyNeither.invoke(retVal.data, data, myColDim, Primitive32Store.cast(right).data);
        } else {
            retVal.multiplyRight.invoke(retVal.data, data, myColDim, right);
        }

        return retVal;
    }

    @Override public Double multiplyBoth( Access1D<Double> leftAndRight) {

        PhysicalStore<Double> tmpStep1 = FACTORY.make(1L, leftAndRight.count());
        PhysicalStore<Double> tmpStep2 = FACTORY.make(1L, 1L);

        tmpStep1.fillByMultiplying(leftAndRight, this);
        tmpStep2.fillByMultiplying(tmpStep1, leftAndRight);

        return tmpStep2.get(0L);
    }

    @Override public PhysicalStore.Factory<Double, ?> physical() {
        return FACTORY;
    }

    @Override public void reduceColumns( Aggregator aggregator,  Mutate1D receiver) {
        myUtility.reduceColumns(aggregator, receiver);
    }

    @Override public void reduceRows( Aggregator aggregator,  Mutate1D receiver) {
        myUtility.reduceRows(aggregator, receiver);
    }

    @Override public TransformableRegion<Double> regionByColumns( int... columns) {
        return new Subregion2D.ColumnsRegion<>(this, multiplyBoth, columns);
    }

    @Override public TransformableRegion<Double> regionByLimits( int rowLimit,  int columnLimit) {
        return new Subregion2D.LimitRegion<>(this, multiplyBoth, rowLimit, columnLimit);
    }

    @Override public TransformableRegion<Double> regionByOffsets( int rowOffset,  int columnOffset) {
        return new Subregion2D.OffsetRegion<>(this, multiplyBoth, rowOffset, columnOffset);
    }

    @Override public TransformableRegion<Double> regionByRows( int... rows) {
        return new Subregion2D.RowsRegion<>(this, multiplyBoth, rows);
    }

    @Override public TransformableRegion<Double> regionByTransposing() {
        return new Subregion2D.TransposedRegion<>(this, multiplyBoth);
    }

    @Override public RowView<Double> rows() {
        return myUtility.rows();
    }

    @Override public void set( long row,  long col,  Comparable<?> value) {
        myUtility.set(row, col, value);
    }

    @Override public void set( long row,  long col,  double value) {
        myUtility.set(row, col, value);
    }

    @Override public short shortValue( long row,  long col) {
        return myUtility.shortValue(row, col);
    }

    @Override public Array1D<Double> sliceColumn( long col) {
        return myUtility.sliceColumn(col);
    }

    @Override public Array1D<Double> sliceColumn( long row,  long col) {
        return myUtility.sliceColumn(row, col);
    }

    @Override public Access1D<Double> sliceDiagonal() {
        return myUtility.sliceDiagonal();
    }

    @Override public Array1D<Double> sliceDiagonal( long row,  long col) {
        return myUtility.sliceDiagonal(row, col);
    }

    @Override public Array1D<Double> sliceRow( long row) {
        return myUtility.sliceRow(row);
    }

    @Override public Array1D<Double> sliceRow( long row,  long col) {
        return myUtility.sliceRow(row, col);
    }

    @Override public void substituteBackwards( Access2D<Double> body,  boolean unitDiagonal,  boolean conjugated,  boolean hermitian) {

         int tmpRowDim = myRowDim;
         int tmpColDim = myColDim;

        if (tmpColDim > SubstituteBackwards.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                public void conquer( int first,  int limit) {
                    SubstituteBackwards.invoke(Primitive32Store.this.data, tmpRowDim, first, limit, body, unitDiagonal, conjugated, hermitian);
                }

            };

            tmpConquerer.invoke(0, tmpColDim, SubstituteBackwards.THRESHOLD);

        } else {

            SubstituteBackwards.invoke(data, tmpRowDim, 0, tmpColDim, body, unitDiagonal, conjugated, hermitian);
        }
    }

    @Override public void substituteForwards( Access2D<Double> body,  boolean unitDiagonal,  boolean conjugated,  boolean identity) {

         int tmpRowDim = myRowDim;
         int tmpColDim = myColDim;

        if (tmpColDim > SubstituteForwards.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                public void conquer( int first,  int limit) {
                    SubstituteForwards.invoke(Primitive32Store.this.data, tmpRowDim, first, limit, body, unitDiagonal, conjugated, identity);
                }

            };

            tmpConquerer.invoke(0, tmpColDim, SubstituteForwards.THRESHOLD);

        } else {

            SubstituteForwards.invoke(data, tmpRowDim, 0, tmpColDim, body, unitDiagonal, conjugated, identity);
        }
    }

    @Override public double[] toRawCopy1D() {
        return myUtility.toRawCopy1D();
    }

    @Override public double[][] toRawCopy2D() {
        return myUtility.toRawCopy2D();
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    @Override public void transformLeft( Householder<Double> transformation,  int firstColumn) {
        HouseholderLeft.call(data, myRowDim, firstColumn, Primitive32Store.cast(transformation));
    }

    @Override public void transformLeft( Rotation<Double> transformation) {

         Rotation.Primitive tmpTransf = Primitive64Store.cast(transformation);

         int tmpLow = tmpTransf.low;
         int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateLeft.invoke(data, myRowDim, tmpLow, tmpHigh, (float) tmpTransf.cos, (float) tmpTransf.sin);
            } else {
                myUtility.exchangeRows(tmpLow, tmpHigh);
            }
        } else if (!Double.isNaN(tmpTransf.cos)) {
            myUtility.modifyRow(tmpLow, 0L, PrimitiveMath.MULTIPLY.second(tmpTransf.cos));
        } else if (!Double.isNaN(tmpTransf.sin)) {
            myUtility.modifyRow(tmpLow, 0L, PrimitiveMath.DIVIDE.second(tmpTransf.sin));
        } else {
            myUtility.modifyRow(tmpLow, 0, PrimitiveMath.NEGATE);
        }
    }

    @Override public void transformRight( Householder<Double> transformation,  int firstRow) {
        HouseholderRight.call(data, myRowDim, firstRow, Primitive32Store.cast(transformation), this.getWorkerColumn());
    }

    @Override public void transformRight( Rotation<Double> transformation) {

         Rotation.Primitive tmpTransf = Primitive64Store.cast(transformation);

         int tmpLow = tmpTransf.low;
         int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateRight.invoke(data, myRowDim, tmpLow, tmpHigh, (float) tmpTransf.cos, (float) tmpTransf.sin);
            } else {
                myUtility.exchangeColumns(tmpLow, tmpHigh);
            }
        } else if (!Double.isNaN(tmpTransf.cos)) {
            myUtility.modifyColumn(0L, tmpHigh, PrimitiveMath.MULTIPLY.second(tmpTransf.cos));
        } else if (!Double.isNaN(tmpTransf.sin)) {
            myUtility.modifyColumn(0L, tmpHigh, PrimitiveMath.DIVIDE.second(tmpTransf.sin));
        } else {
            myUtility.modifyColumn(0, tmpHigh, PrimitiveMath.NEGATE);
        }
    }

    @Override public void visitColumn( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitColumn(row, col, visitor);
    }

    @Override public void visitColumn( long col,  VoidFunction<Double> visitor) {
        myUtility.visitColumn(col, visitor);
    }

    @Override public void visitDiagonal( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitDiagonal(row, col, visitor);
    }

    @Override public void visitDiagonal( VoidFunction<Double> visitor) {
        myUtility.visitDiagonal(visitor);
    }

    @Override public void visitOne( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitOne(row, col, visitor);
    }

    @Override public void visitRow( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitRow(row, col, visitor);
    }

    @Override public void visitRow( long row,  VoidFunction<Double> visitor) {
        myUtility.visitRow(row, visitor);
    }

    private float[] getWorkerColumn() {
        if (myWorkerColumn != null) {
            Arrays.fill(myWorkerColumn, 0F);
        } else {
            myWorkerColumn = new float[myRowDim];
        }
        return myWorkerColumn;
    }

}
