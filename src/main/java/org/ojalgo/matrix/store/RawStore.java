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

import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import com.google.errorprone.annotations.Var;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.operation.AMAX;
import org.ojalgo.array.operation.COPY;
import org.ojalgo.array.operation.FillMatchingDual;
import org.ojalgo.array.operation.ModifyAll;
import org.ojalgo.array.operation.SWAP;
import org.ojalgo.array.operation.SubstituteBackwards;
import org.ojalgo.array.operation.SubstituteForwards;
import org.ojalgo.array.operation.VisitAll;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.MissingMath;
import org.ojalgo.matrix.operation.MultiplyBoth;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.type.NumberDefinition;

/**
 * Uses double[][] internally.
 *
 * @author apete
 */
public final class RawStore implements PhysicalStore<Double> {

    public static final PhysicalStore.Factory<Double, RawStore> FACTORY = new PrimitiveFactory<RawStore>() {

        @Override public RawStore columns( Access1D<?>... source) {

            int nbRows = source[0].size();
            int nbCols = source.length;

            var retVal = new RawStore(nbRows, nbCols);
            double[][] retValData = retVal.data;

            @Var Access1D<?> tmpCol;
            for (int j = 0; j < nbCols; j++) {
                tmpCol = source[j];
                for (int i = 0; i < nbRows; i++) {
                    retValData[i][j] = tmpCol.doubleValue(i);
                }
            }

            return retVal;
        }

        @Override public RawStore columns( Comparable<?>[]... source) {

            int nbRows = source[0].length;
            int nbCols = source.length;

            var retVal = new RawStore(nbRows, nbCols);
            double[][] retValData = retVal.data;

            @Var Comparable<?>[] tmpCol;
            for (int j = 0; j < nbCols; j++) {
                tmpCol = source[j];
                for (int i = 0; i < nbRows; i++) {
                    retValData[i][j] = NumberDefinition.doubleValue(tmpCol[i]);
                }
            }

            return retVal;
        }

        @Override public RawStore columns( double[]... source) {

            int nbRows = source[0].length;
            int nbCols = source.length;

            var retVal = new RawStore(nbRows, nbCols);
            double[][] retValData = retVal.data;

            @Var double[] tmpCol;
            for (int j = 0; j < nbCols; j++) {
                tmpCol = source[j];
                for (int i = 0; i < nbRows; i++) {
                    retValData[i][j] = tmpCol[i];
                }
            }

            return retVal;
        }

        @Override public RawStore columns( List<? extends Comparable<?>>... source) {

            int nbRows = source[0].size();
            int nbCols = source.length;

            var retVal = new RawStore(nbRows, nbCols);
            double[][] retValData = retVal.data;

            @Var List<? extends Comparable<?>> tmpCol;
            for (int j = 0; j < nbCols; j++) {
                tmpCol = source[j];
                for (int i = 0; i < nbRows; i++) {
                    retValData[i][j] = NumberDefinition.doubleValue(tmpCol.get(i));
                }
            }

            return retVal;
        }

        @Override public RawStore copy( Access2D<?> source) {

            int nbRows = source.getRowDim();
            int nbCols = source.getColDim();

            var retVal = new RawStore(nbRows, nbCols);

            for (int i = 0; i < nbRows; i++) {
                COPY.row(source, i, retVal.data[i], 0, nbCols);
            }

            return retVal;
        }

        @Override public RawStore make( long rows,  long columns) {
            return new RawStore(Math.toIntExact(rows), Math.toIntExact(columns));
        }

        @Override public RawStore rows( Access1D<?>... source) {

            int nbRows = source.length;
            int nbCols = source[0].size();

            var retVal = new RawStore(nbRows, nbCols);

            @Var Access1D<?> tmpRow;
            @Var double[] retValRow;
            for (int i = 0; i < nbRows; i++) {
                tmpRow = source[i];
                retValRow = retVal.data[i];
                for (int j = 0; j < nbCols; j++) {
                    retValRow[j] = tmpRow.doubleValue(j);
                }
            }

            return retVal;
        }

        @Override public RawStore rows( Comparable<?>[]... source) {

            int nbRows = source.length;
            int nbCols = source[0].length;

            var retVal = new RawStore(nbRows, nbCols);

            @Var Comparable<?>[] tmpRow;
            @Var double[] retValRow;
            for (int i = 0; i < nbRows; i++) {
                tmpRow = source[i];
                retValRow = retVal.data[i];
                for (int j = 0; j < nbCols; j++) {
                    retValRow[j] = NumberDefinition.doubleValue(tmpRow[j]);
                }
            }

            return retVal;
        }

        @Override public RawStore rows( double[]... source) {

            int nbRows = source.length;
            int nbCols = source[0].length;

            var retVal = new RawStore(nbRows, nbCols);

            @Var double[] tmpRow;
            @Var double[] retValRow;
            for (int i = 0; i < nbRows; i++) {
                tmpRow = source[i];
                retValRow = retVal.data[i];
                for (int j = 0; j < nbCols; j++) {
                    retValRow[j] = tmpRow[j];
                }
            }

            return retVal;
        }

        @Override public RawStore rows( List<? extends Comparable<?>>... source) {

            int nbRows = source.length;
            int nbCols = source[0].size();

            var retVal = new RawStore(nbRows, nbCols);

            @Var List<? extends Comparable<?>> tmpRow;
            @Var double[] retValRow;
            for (int i = 0; i < nbRows; i++) {
                tmpRow = source[i];
                retValRow = retVal.data[i];
                for (int j = 0; j < nbCols; j++) {
                    retValRow[j] = NumberDefinition.doubleValue(tmpRow.get(j));
                }
            }

            return retVal;
        }

        @Override public RawStore transpose( Access2D<?> source) {

            int nbRows = source.getColDim();
            int nbCols = source.getRowDim();

            var retVal = new RawStore(nbRows, nbCols);

            for (int i = 0; i < nbRows; i++) {
                double[] retValRow = retVal.data[i];
                for (int j = 0; j < nbCols; j++) {
                    retValRow[j] = source.doubleValue(j, i);
                }
            }

            return retVal;
        }

    };

    /**
     * Will create a single row matrix with the supplied array as the inner array. You access it using
     * <code>data[0]</code>.
     */
    public static RawStore wrap( double... data) {
        return new RawStore(new double[][] { data }, data.length);
    }

    public static RawStore wrap( double[][] data) {
        return new RawStore(data, data[0].length);
    }

    private static RawStore convert( Access1D<?> elements,  int structure) {

        if (elements instanceof RawStore) {
            return (RawStore) elements;
        }

        int nbCols = structure != 0 ? elements.size() / structure : 0;

        var retVal = new RawStore(structure, nbCols);

        if (structure * nbCols != elements.size()) {
            throw new IllegalArgumentException("Array length must be a multiple of structure.");
        }

        for (int i = 0; i < structure; i++) {
            double[] row = retVal.data[i];
            for (int j = 0; j < nbCols; j++) {
                row[j] = elements.doubleValue(Structure2D.index(structure, i, j));
            }
        }

        return retVal;
    }

    private static double[][] extract( Access1D<?> elements,  int nbRows) {

        @Var double[][] retVal = null;

        if (elements instanceof RawStore && ((RawStore) elements).getRowDim() == nbRows) {

            retVal = ((RawStore) elements).data;

        } else if (elements instanceof Access2D && ((Access2D<?>) elements).getRowDim() == nbRows) {

            retVal = ((Access2D<?>) elements).toRawCopy2D();

        } else {

            int nbColumns = nbRows != 0 ? Math.toIntExact(elements.count() / nbRows) : 0;

            retVal = new double[nbRows][];

            @Var double[] tmpRow;
            for (int i = 0; i < nbRows; i++) {
                tmpRow = retVal[i] = new double[nbColumns];
                for (int j = 0; j < nbColumns; j++) {
                    tmpRow[j] = elements.doubleValue(Structure2D.index(nbRows, i, j));
                }
            }
        }

        return retVal;
    }

    private static void multiply( double[][] product,  double[][] left,  double[][] right) {

        int tmpRowsCount = product.length;
        int tmpComplexity = right.length;
        int tmpColsCount = right[0].length;

        @Var double[] tmpRow;
        double[] tmpColumn = new double[tmpComplexity];
        for (int j = 0; j < tmpColsCount; j++) {
            for (int k = 0; k < tmpComplexity; k++) {
                tmpColumn[k] = right[k][j];
            }
            for (int i = 0; i < tmpRowsCount; i++) {
                tmpRow = left[i];
                @Var double tmpVal = 0.0;
                for (int k = 0; k < tmpComplexity; k++) {
                    tmpVal += tmpRow[k] * tmpColumn[k];
                }
                product[i][j] = tmpVal;
            }
        }
    }

    static Rotation.Primitive cast( Rotation<Double> aTransf) {
        if (aTransf instanceof Rotation.Primitive) {
            return (Rotation.Primitive) aTransf;
        }
        return new Rotation.Primitive(aTransf);
    }

    public final double[][] data;

    private final int myNumberOfColumns;

    RawStore( double[][] elements,  int numberOfColumns) {

        super();

        data = elements;

        myNumberOfColumns = numberOfColumns;
    }

    /**
     * Construct an m-by-n matrix of zeros.
     *
     * @param m Number of rows.
     * @param n Number of colums.
     */
    RawStore( int m,  int n) {

        super();

        myNumberOfColumns = n;
        data = new double[m][n];
    }

    @Override public void accept( Access2D<?> supplied) {

        int numbRows = MissingMath.toMinIntExact(data.length, supplied.countRows());
        int numbCols = MissingMath.toMinIntExact(myNumberOfColumns, supplied.countColumns());

        for (int i = 0; i < numbRows; i++) {
            COPY.row(supplied, i, data[i], 0, numbCols);
        }
    }

    @Override public void add( long row,  long col,  Comparable<?> addend) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] += NumberDefinition.doubleValue(addend);
    }

    @Override public void add( long row,  long col,  double addend) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] += addend;
    }

    @Override public Double aggregateAll( Aggregator aggregator) {

        AggregatorFunction<Double> tmpVisitor = aggregator.getFunction(PrimitiveAggregator.getSet());

        this.visitAll(tmpVisitor);

        return tmpVisitor.get();
    }

    @Override public List<Double> asList() {

        int tmpStructure = data.length;

        return new AbstractList<Double>() {

            @Override
            public Double get( int index) {
                return RawStore.this.get(Structure2D.row(index, tmpStructure), Structure2D.column(index, tmpStructure));
            }

            @Override
            public Double set( int index,  Double value) {
                int tmpRow = Structure2D.row(index, tmpStructure);
                int tmpColumn = Structure2D.column(index, tmpStructure);
                Double retVal = RawStore.this.get(tmpRow, tmpColumn);
                RawStore.this.set(tmpRow, tmpColumn, value);
                return retVal;
            }

            @Override
            public int size() {
                return (int) RawStore.this.count();
            }
        };
    }

    @Override public MatrixStore<Double> conjugate() {
        return this.transpose();
    }

    /**
     * Make a deep copy of a matrix
     */
    @Override public RawStore copy() {
        return new RawStore(this.toRawCopy2D(), myNumberOfColumns);
    }

    @Override public long count() {
        return Structure2D.count(data.length, myNumberOfColumns);
    }

    @Override public long countColumns() {
        return myNumberOfColumns;
    }

    @Override public long countRows() {
        return data.length;
    }

    @Override public double doubleValue( long row,  long col) {
        return data[Math.toIntExact(row)][Math.toIntExact(col)];
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RawStore)) {
            return false;
        }
        var other = (RawStore) obj;
        if (myNumberOfColumns != other.myNumberOfColumns || !Arrays.deepEquals(data, other.data)) {
            return false;
        }
        return true;
    }

    @Override public void exchangeColumns( long colA,  long colB) {
        SWAP.exchangeColumns(data, Math.toIntExact(colA), Math.toIntExact(colB));
    }

    @Override public void exchangeRows( long rowA,  long rowB) {
        SWAP.exchangeRows(data, Math.toIntExact(rowA), Math.toIntExact(rowB));
    }

    @Override public void fillAll( Double value) {
        FillMatchingDual.fillAll(data, value.doubleValue());
    }

    @Override public void fillAll( NullaryFunction<?> supplier) {
        FillMatchingDual.fillAll(data, supplier);
    }

    @Override public void fillByMultiplying( Access1D<Double> left,  Access1D<Double> right) {

        int complexity = Math.toIntExact(left.count() / this.countRows());
        if (complexity != Math.toIntExact(right.count() / this.countColumns())) {
            ProgrammingError.throwForMultiplicationNotPossible();
        }

        double[][] rawLeft = RawStore.extract(left, this.getRowDim());
        double[][] rawRight = RawStore.extract(right, complexity);

        RawStore.multiply(data, rawLeft, rawRight);
    }

    @Override public void fillColumn( long row,  long col,  Double value) {
        FillMatchingDual.fillColumn(data, Math.toIntExact(row), Math.toIntExact(col), value.doubleValue());
    }

    @Override public void fillColumn( long row,  long col,  NullaryFunction<?> supplier) {
        FillMatchingDual.fillColumn(data, Math.toIntExact(row), Math.toIntExact(col), supplier);
    }

    @Override public void fillDiagonal( long row,  long col,  Double value) {
        FillMatchingDual.fillDiagonal(data, Math.toIntExact(row), Math.toIntExact(col), value.doubleValue());
    }

    @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
        FillMatchingDual.fillDiagonal(data, Math.toIntExact(row), Math.toIntExact(col), supplier);
    }

    @Override public void fillMatching( Access1D<?> source) {

        @Var double[] rowI;

        int structure = data.length;
        for (int i = 0; i < structure; i++) {
            rowI = data[i];

            for (int j = 0; j < myNumberOfColumns; j++) {
                rowI[j] = source.doubleValue(Structure2D.index(structure, i, j));
            }
        }
    }

    @Override public void fillMatching( Access1D<Double> left,  BinaryFunction<Double> function,  Access1D<Double> right) {
        if (left == this) {
            double[][] tmpRight = RawStore.convert(right, data.length).data;
            if (function == PrimitiveMath.ADD) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = data[i][j] + tmpRight[i][j];
                    }
                }
            } else if (function == PrimitiveMath.DIVIDE) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = data[i][j] / tmpRight[i][j];
                    }
                }
            } else if (function == PrimitiveMath.MULTIPLY) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = data[i][j] * tmpRight[i][j];
                    }
                }
            } else if (function == PrimitiveMath.SUBTRACT) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = data[i][j] - tmpRight[i][j];
                    }
                }
            } else {
                FillMatchingDual.fillMatching(data, data, function, tmpRight);
            }
        } else if (right == this) {
            double[][] tmpLeft = RawStore.convert(left, data.length).data;
            if (function == PrimitiveMath.ADD) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = tmpLeft[i][j] + data[i][j];
                    }
                }
            } else if (function == PrimitiveMath.DIVIDE) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = tmpLeft[i][j] / data[i][j];
                    }
                }
            } else if (function == PrimitiveMath.MULTIPLY) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = tmpLeft[i][j] * data[i][j];
                    }
                }
            } else if (function == PrimitiveMath.SUBTRACT) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < myNumberOfColumns; j++) {
                        data[i][j] = tmpLeft[i][j] - data[i][j];
                    }
                }
            } else {
                FillMatchingDual.fillMatching(data, tmpLeft, function, data);
            }
        } else {
            FillMatchingDual.fillMatching(data, RawStore.convert(left, data.length).data, function, RawStore.convert(right, data.length).data);
        }
    }

    public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
        this.set(row, col, values.doubleValue(valueIndex));
    }

    public void fillOne( long row,  long col,  Double value) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] = value.doubleValue();
    }

    public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] = supplier.doubleValue();
    }

    @Override public void fillRange( long first,  long limit,  Double value) {
        FillMatchingDual.fillRange(data, (int) first, (int) limit, value.doubleValue());
    }

    @Override public void fillRange( long first,  long limit,  NullaryFunction<?> supplier) {
        FillMatchingDual.fillRange(data, (int) first, (int) limit, supplier);
    }

    @Override public void fillRow( long row,  long col,  Double value) {
        FillMatchingDual.fillRow(data, Math.toIntExact(row), Math.toIntExact(col), value.doubleValue());
    }

    @Override public void fillRow( long row,  long col,  NullaryFunction<?> supplier) {
        FillMatchingDual.fillRow(data, Math.toIntExact(row), Math.toIntExact(col), supplier);
    }

    public MatrixStore<Double> get() {
        return this;
    }

    @Override public Double get( long row,  long col) {
        return Double.valueOf(this.doubleValue(row, col));
    }

    @Override public int getColDim() {
        return myNumberOfColumns;
    }

    @Override public int getRowDim() {
        return data.length;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        @Var int result = 1;
        result = prime * result + Arrays.deepHashCode(data);
        result = prime * result + myNumberOfColumns;
        return result;
    }

    @Override public long indexOfLargest() {
        return AMAX.invoke(data);
    }

    @Override public boolean isSmall( long row,  long col,  double comparedTo) {
        return PrimitiveScalar.isSmall(comparedTo, this.doubleValue(row, col));
    }

    @Override public void modifyAll( UnaryFunction<Double> modifier) {
        ModifyAll.modifyAll(data, modifier);
    }

    @Override public void modifyColumn( long row,  long col,  UnaryFunction<Double> modifier) {
        ModifyAll.modifyColumn(data, Math.toIntExact(row), Math.toIntExact(col), modifier);
    }

    @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<Double> modifier) {

        long tmpCount = Math.min(data.length - row, myNumberOfColumns - col);

        var tmpFirst = (int) (row + col * data.length);
        var tmpLimit = (int) (row + tmpCount + (col + tmpCount) * data.length);
        int tmpStep = 1 + data.length;

        for (int ij = tmpFirst; ij < tmpLimit; ij += tmpStep) {
            this.set(ij, modifier.invoke(this.doubleValue(ij)));
        }

    }

    @Override public void modifyMatching( Access1D<Double> left,  BinaryFunction<Double> function) {

        @Var double[] tmpRowI;

        int tmpRowDim = data.length;
        for (int i = 0; i < tmpRowDim; i++) {

            tmpRowI = data[i];

            for (int j = 0; j < myNumberOfColumns; j++) {
                tmpRowI[j] = function.invoke(left.doubleValue(Structure2D.index(tmpRowDim, i, j)), tmpRowI[j]);
            }
        }
    }

    @Override public void modifyMatching( BinaryFunction<Double> function,  Access1D<Double> right) {

        @Var double[] tmpRowI;

        int tmpRowDim = data.length;
        for (int i = 0; i < tmpRowDim; i++) {

            tmpRowI = data[i];

            for (int j = 0; j < myNumberOfColumns; j++) {
                tmpRowI[j] = function.invoke(tmpRowI[j], right.doubleValue(Structure2D.index(tmpRowDim, i, j)));
            }
        }
    }

    @Override public void modifyOne( long row,  long col,  UnaryFunction<Double> modifier) {

        @Var double tmpValue = this.doubleValue(row, col);

        tmpValue = modifier.invoke(tmpValue);

        this.set(row, col, tmpValue);
    }

    @Override public void modifyRange( long first,  long limit,  UnaryFunction<Double> modifier) {
        for (long index = first; index < limit; index++) {
            this.set(index, modifier.invoke(this.doubleValue(index)));
        }
    }

    @Override public void modifyRow( long row,  long col,  UnaryFunction<Double> modifier) {
        ModifyAll.modifyRow(data, Math.toIntExact(row), Math.toIntExact(col), modifier);
    }

    @Override public RawStore multiply( MatrixStore<Double> right) {

        int tmpRowDim = data.length;
        int tmpComplexity = myNumberOfColumns;
        var tmpColDim = (int) (right.count() / tmpComplexity);

        var retVal = new RawStore(tmpRowDim, tmpColDim);

        double[][] tmpRight = RawStore.extract(right, tmpComplexity);

        RawStore.multiply(retVal.data, data, tmpRight);

        return retVal;
    }

    @Override public Double multiplyBoth( Access1D<Double> leftAndRight) {

        PhysicalStore<Double> tmpStep1 = FACTORY.make(1L, leftAndRight.count());
        PhysicalStore<Double> tmpStep2 = FACTORY.make(1L, 1L);

        tmpStep1.fillByMultiplying(leftAndRight, this);
        tmpStep2.fillByMultiplying(tmpStep1, leftAndRight);

        return tmpStep2.get(0L);
    }

    @Override public PhysicalStore.Factory<Double, RawStore> physical() {
        return FACTORY;
    }

    @Override public TransformableRegion<Double> regionByColumns( int... columns) {
        return new Subregion2D.ColumnsRegion<>(this, MultiplyBoth.newPrimitive64(data.length, myNumberOfColumns), columns);
    }

    @Override public TransformableRegion<Double> regionByLimits( int rowLimit,  int columnLimit) {
        return new Subregion2D.LimitRegion<>(this, MultiplyBoth.newPrimitive64(data.length, myNumberOfColumns), rowLimit, columnLimit);
    }

    @Override public TransformableRegion<Double> regionByOffsets( int rowOffset,  int columnOffset) {
        return new Subregion2D.OffsetRegion<>(this, MultiplyBoth.newPrimitive64(data.length, myNumberOfColumns), rowOffset, columnOffset);
    }

    @Override public TransformableRegion<Double> regionByRows( int... rows) {
        return new Subregion2D.RowsRegion<>(this, MultiplyBoth.newPrimitive64(data.length, myNumberOfColumns), rows);
    }

    @Override public TransformableRegion<Double> regionByTransposing() {
        return new Subregion2D.TransposedRegion<>(this, MultiplyBoth.newPrimitive64(data.length, myNumberOfColumns));
    }

    @Override public void set( long row,  long col,  Comparable<?> value) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] = NumberDefinition.doubleValue(value);
    }

    @Override public void set( long row,  long col,  double value) {
        data[Math.toIntExact(row)][Math.toIntExact(col)] = value;
    }

    @Override public Access1D<Double> sliceRow( long row) {
        return Access1D.wrap(data[Math.toIntExact(row)]);
    }

    @Override public void substituteBackwards( Access2D<Double> body,  boolean unitDiagonal,  boolean conjugated,  boolean hermitian) {
        SubstituteBackwards.invoke(data, body, unitDiagonal, conjugated, hermitian);
    }

    @Override public void substituteForwards( Access2D<Double> body,  boolean unitDiagonal,  boolean conjugated,  boolean identity) {
        SubstituteForwards.invoke(data, body, unitDiagonal, conjugated, identity);
    }

    @Override public PrimitiveScalar toScalar( long row,  long column) {
        return PrimitiveScalar.of(this.doubleValue(row, column));
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    @Override public void transformLeft( Householder<Double> transformation,  int firstColumn) {

        double[][] tmpArray = data;
        int tmpRowDim = data.length;
        int tmpColDim = myNumberOfColumns;

        int tmpFirst = transformation.first();

        double[] tmpWorkCopy = new double[(int) transformation.count()];

        @Var double tmpScale;
        for (int j = firstColumn; j < tmpColDim; j++) {
            tmpScale = ZERO;
            for (int i = tmpFirst; i < tmpRowDim; i++) {
                tmpScale += tmpWorkCopy[i] * tmpArray[i][j];
            }
            @Var double tmpVal, tmpVal2 = PrimitiveMath.ZERO;
            var tmpSize = (int) transformation.count();
            for (int i1 = transformation.first(); i1 < tmpSize; i1++) {
                tmpVal = transformation.doubleValue(i1);
                tmpVal2 += tmpVal * tmpVal;
                tmpWorkCopy[i1] = tmpVal;
            }
            tmpScale *= PrimitiveMath.TWO / tmpVal2;
            for (int i = tmpFirst; i < tmpRowDim; i++) {
                tmpArray[i][j] -= tmpScale * tmpWorkCopy[i];
            }
        }
    }

    @Override public void transformLeft( Rotation<Double> transformation) {

        Rotation.Primitive tmpTransf = RawStore.cast(transformation);

        int tmpLow = tmpTransf.low;
        int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {

                double[][] tmpArray = data;
                @Var double tmpOldLow;
                @Var double tmpOldHigh;

                for (int j = 0; j < tmpArray[0].length; j++) {

                    tmpOldLow = tmpArray[tmpLow][j];
                    tmpOldHigh = tmpArray[tmpHigh][j];

                    tmpArray[tmpLow][j] = tmpTransf.cos * tmpOldLow + tmpTransf.sin * tmpOldHigh;
                    tmpArray[tmpHigh][j] = tmpTransf.cos * tmpOldHigh - tmpTransf.sin * tmpOldLow;
                }
            } else {
                this.exchangeRows(tmpLow, tmpHigh);
            }
        } else if (!Double.isNaN(tmpTransf.cos)) {
            this.modifyRow(tmpLow, 0, PrimitiveMath.MULTIPLY.second(tmpTransf.cos));
        } else if (!Double.isNaN(tmpTransf.sin)) {
            this.modifyRow(tmpLow, 0, PrimitiveMath.DIVIDE.second(tmpTransf.sin));
        } else {
            this.modifyRow(tmpLow, 0, PrimitiveMath.NEGATE);
        }
    }

    @Override public void transformRight( Householder<Double> transformation,  int firstRow) {

        double[][] tmpArray = data;
        int tmpRowDim = data.length;
        int tmpColDim = myNumberOfColumns;

        int tmpFirst = transformation.first();

        double[] tmpWorkCopy = new double[(int) transformation.count()];

        @Var double tmpScale;
        for (int i = firstRow; i < tmpRowDim; i++) {
            tmpScale = ZERO;
            for (int j = tmpFirst; j < tmpColDim; j++) {
                tmpScale += tmpWorkCopy[j] * tmpArray[i][j];
            }
            @Var double tmpVal, tmpVal2 = PrimitiveMath.ZERO;
            var tmpSize = (int) transformation.count();
            for (int i1 = transformation.first(); i1 < tmpSize; i1++) {
                tmpVal = transformation.doubleValue(i1);
                tmpVal2 += tmpVal * tmpVal;
                tmpWorkCopy[i1] = tmpVal;
            }
            tmpScale *= PrimitiveMath.TWO / tmpVal2;
            for (int j = tmpFirst; j < tmpColDim; j++) {
                tmpArray[i][j] -= tmpScale * tmpWorkCopy[j];
            }
        }
    }

    @Override public void transformRight( Rotation<Double> transformation) {

        Rotation.Primitive tmpTransf = RawStore.cast(transformation);

        int tmpLow = tmpTransf.low;
        int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {

                double[][] tmpArray = data;
                @Var double tmpOldLow;
                @Var double tmpOldHigh;

                for (int i = 0; i < tmpArray.length; i++) {

                    tmpOldLow = tmpArray[i][tmpLow];
                    tmpOldHigh = tmpArray[i][tmpHigh];

                    tmpArray[i][tmpLow] = tmpTransf.cos * tmpOldLow - tmpTransf.sin * tmpOldHigh;
                    tmpArray[i][tmpHigh] = tmpTransf.cos * tmpOldHigh + tmpTransf.sin * tmpOldLow;
                }
            } else {
                this.exchangeColumns(tmpLow, tmpHigh);
            }
        } else if (!Double.isNaN(tmpTransf.cos)) {
            this.modifyColumn(0, tmpHigh, PrimitiveMath.MULTIPLY.second(tmpTransf.cos));
        } else if (!Double.isNaN(tmpTransf.sin)) {
            this.modifyColumn(0, tmpHigh, PrimitiveMath.DIVIDE.second(tmpTransf.sin));
        } else {
            this.modifyColumn(0, tmpHigh, PrimitiveMath.NEGATE);
        }
    }

    @Override public MatrixStore<Double> transpose() {
        return new TransposedStore<>(this);
    }

    @Override public void visitAll( VoidFunction<Double> visitor) {
        VisitAll.visitAll(data, visitor);
    }

    @Override public void visitColumn( long row,  long col,  VoidFunction<Double> visitor) {
        VisitAll.visitColumn(data, Math.toIntExact(row), Math.toIntExact(col), visitor);
    }

    @Override public void visitDiagonal( long row,  long col,  VoidFunction<Double> visitor) {
        VisitAll.visitDiagonal(data, Math.toIntExact(row), Math.toIntExact(col), visitor);
    }

    @Override public void visitRange( long first,  long limit,  VoidFunction<Double> visitor) {
        VisitAll.visitRange(data, (int) first, (int) limit, visitor);
    }

    @Override public void visitRow( long row,  long col,  VoidFunction<Double> visitor) {
        VisitAll.visitRow(data, Math.toIntExact(row), Math.toIntExact(col), visitor);
    }

}
