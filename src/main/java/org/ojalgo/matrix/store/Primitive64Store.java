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

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import java.util.Arrays;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.ArrayC128;
import org.ojalgo.array.ArrayR064;
import org.ojalgo.array.BasicArray;
import org.ojalgo.array.operation.*;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.MissingMath;
import org.ojalgo.machine.JavaType;
import org.ojalgo.machine.MemoryEstimator;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.EvD1D;
import org.ojalgo.matrix.operation.HouseholderLeft;
import org.ojalgo.matrix.operation.HouseholderRight;
import org.ojalgo.matrix.operation.MultiplyBoth;
import org.ojalgo.matrix.operation.MultiplyLeft;
import org.ojalgo.matrix.operation.MultiplyNeither;
import org.ojalgo.matrix.operation.MultiplyRight;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.HouseholderReference;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;

/**
 * A {@linkplain double} implementation of {@linkplain PhysicalStore}.
 *
 * @author apete
 */
public final class Primitive64Store extends ArrayR064 implements PhysicalStore<Double>, DecompositionStore<Double> {

    public static final PhysicalStore.Factory<Double, Primitive64Store> FACTORY = new PrimitiveFactory<Primitive64Store>() {

        @Override public Primitive64Store columns( Access1D<?>... source) {

             var tmpRowDim = (int) source[0].count();
             int tmpColDim = source.length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var Access1D<?> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = tmpColumn.doubleValue(i);
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store columns( Comparable<?>[]... source) {

             int tmpRowDim = source[0].length;
             int tmpColDim = source.length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var Comparable<?>[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.doubleValue(tmpColumn[i]);
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store columns( double[]... source) {

             int tmpRowDim = source[0].length;
             int tmpColDim = source.length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var double[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = tmpColumn[i];
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store columns( List<? extends Comparable<?>>... source) {

             int tmpRowDim = source[0].size();
             int tmpColDim = source.length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var List<? extends Comparable<?>> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = source[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.doubleValue(tmpColumn.get(i));
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store copy( Access2D<?> source) {

             var tmpRowDim = (int) source.countRows();
             var tmpColDim = (int) source.countColumns();

             var retVal = new Primitive64Store(tmpRowDim, tmpColDim);

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

        @Override public Primitive64Store make( long rows,  long columns) {
            return new Primitive64Store((int) rows, (int) columns);
        }

        @Override public Primitive64Store rows( Access1D<?>... source) {

             int tmpRowDim = source.length;
             var tmpColDim = (int) source[0].count();

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var Access1D<?> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = tmpRow.doubleValue(j);
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store rows( Comparable<?>[]... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var Comparable<?>[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.doubleValue(tmpRow[j]);
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store rows( double[]... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].length;

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var double[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = tmpRow[j];
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store rows( List<? extends Comparable<?>>... source) {

             int tmpRowDim = source.length;
             int tmpColDim = source[0].size();

             double[] tmpData = new double[tmpRowDim * tmpColDim];

            @Var List<? extends Comparable<?>> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = source[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + tmpRowDim * j] = NumberDefinition.doubleValue(tmpRow.get(j));
                }
            }

            return new Primitive64Store(tmpRowDim, tmpColDim, tmpData);
        }

        @Override public Primitive64Store transpose( Access2D<?> source) {

             var retVal = new Primitive64Store((int) source.countColumns(), (int) source.countRows());

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

    static final long ELEMENT_SIZE = JavaType.DOUBLE.memory();

    static final long SHALLOW_SIZE = MemoryEstimator.estimateObject(Primitive64Store.class);

    /**
     * Extracts the argument of the ComplexNumber elements to a new primitive double valued matrix.
     */
    public static Primitive64Store getComplexArgument( Access2D<ComplexNumber> arg) {

         long numberOfRows = arg.countRows();
         long numberOfColumns = arg.countColumns();

         Primitive64Store retVal = FACTORY.make(numberOfRows, numberOfColumns);

        Mutate1D.copyComplexArgument(arg, retVal);

        return retVal;
    }

    /**
     * Extracts the imaginary part of the ComplexNumber elements to a new primitive double valued matrix.
     */
    public static Primitive64Store getComplexImaginary( Access2D<ComplexNumber> arg) {

         long numberOfRows = arg.countRows();
         long numberOfColumns = arg.countColumns();

         Primitive64Store retVal = FACTORY.make(numberOfRows, numberOfColumns);

        Mutate1D.copyComplexImaginary(arg, retVal);

        return retVal;
    }

    /**
     * Extracts the modulus of the ComplexNumber elements to a new primitive double valued matrix.
     */
    public static Primitive64Store getComplexModulus( Access2D<ComplexNumber> arg) {

         long numberOfRows = arg.countRows();
         long numberOfColumns = arg.countColumns();

         Primitive64Store retVal = FACTORY.make(numberOfRows, numberOfColumns);

        Mutate1D.copyComplexModulus(arg, retVal);

        return retVal;
    }

    /**
     * Extracts the real part of the ComplexNumber elements to a new primitive double valued matrix.
     */
    public static Primitive64Store getComplexReal( Access2D<ComplexNumber> arg) {

         long numberOfRows = arg.countRows();
         long numberOfColumns = arg.countColumns();

         Primitive64Store retVal = FACTORY.make(numberOfRows, numberOfColumns);

        Mutate1D.copyComplexReal(arg, retVal);

        return retVal;
    }

    public static Primitive64Store wrap( double... data) {
        return new Primitive64Store(data);
    }

    public static Primitive64Store wrap( double[] data,  int structure) {
        return new Primitive64Store(structure, data.length / structure, data);
    }

    static Primitive64Store cast( Access1D<Double> matrix) {
        if (matrix instanceof Primitive64Store) {
            return (Primitive64Store) matrix;
        }
        if (matrix instanceof Access2D<?>) {
            return FACTORY.copy((Access2D<?>) matrix);
        }
        return FACTORY.columns(matrix);
    }

    static Householder.Primitive64 cast( Householder<Double> transformation) {
        if (transformation instanceof Householder.Primitive64) {
            return (Householder.Primitive64) transformation;
        }
        if (transformation instanceof HouseholderReference<?>) {
            return ((Householder.Primitive64) ((HouseholderReference<Double>) transformation).getWorker(FACTORY)).copy(transformation);
        }
        return new Householder.Primitive64(transformation);
    }

    static Rotation.Primitive cast( Rotation<Double> transformation) {
        if (transformation instanceof Rotation.Primitive) {
            return (Rotation.Primitive) transformation;
        }
        return new Rotation.Primitive(transformation);
    }

    private final MultiplyBoth.Primitive multiplyBoth;
    private final MultiplyLeft.Primitive64 multiplyLeft;
    private final MultiplyNeither.Primitive64 multiplyNeither;
    private final MultiplyRight.Primitive64 multiplyRight;
    private final int myColDim;
    private final int myRowDim;
    private final Array2D<Double> myUtility;
    private transient double[] myWorkerColumn;

    @SuppressWarnings("unused")
    private Primitive64Store( double[] dataArray) {
        this(dataArray.length, 1, dataArray);
    }

    @SuppressWarnings("unused")
    private Primitive64Store( int numbRows) {
        this(numbRows, 1);
    }

    Primitive64Store( int numbRows,  int numbCols,  double[] dataArray) {

        super(dataArray);

        myRowDim = numbRows;
        myColDim = numbCols;

        myUtility = this.wrapInArray2D(myRowDim);

        multiplyBoth = MultiplyBoth.newPrimitive64(myRowDim, myColDim);
        multiplyLeft = MultiplyLeft.newPrimitive64(myRowDim, myColDim);
        multiplyRight = MultiplyRight.newPrimitive64(myRowDim, myColDim);
        multiplyNeither = MultiplyNeither.newPrimitive64(myRowDim, myColDim);
    }

    Primitive64Store( long numbRows,  long numbCols) {

        super(Math.toIntExact(numbRows * numbCols));

        myRowDim = Math.toIntExact(numbRows);
        myColDim = Math.toIntExact(numbCols);

        myUtility = this.wrapInArray2D(myRowDim);

        multiplyBoth = MultiplyBoth.newPrimitive64(myRowDim, myColDim);
        multiplyLeft = MultiplyLeft.newPrimitive64(myRowDim, myColDim);
        multiplyRight = MultiplyRight.newPrimitive64(myRowDim, myColDim);
        multiplyNeither = MultiplyNeither.newPrimitive64(myRowDim, myColDim);
    }

    @Override public void accept( Access2D<?> supplied) {
        for (long j = 0L; j < supplied.countColumns(); j++) {
            for (long i = 0L; i < supplied.countRows(); i++) {
                this.set(i, j, supplied.doubleValue(i, j));
            }
        }
    }

    @Override public void add( long row,  long col,  Comparable<?> addend) {
        myUtility.add(row, col, addend);
    }

    @Override public void add( long row,  long col,  double addend) {
        myUtility.add(row, col, addend);
    }

    @Override public void applyCholesky( int iterationPoint,  BasicArray<Double> multipliers) {

         double[] tmpData = data;
         double[] tmpColumn = ((ArrayR064) multipliers).data;

        if (myColDim - iterationPoint - 1 > ApplyCholesky.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                protected void conquer( int first,  int limit) {
                    ApplyCholesky.invoke(tmpData, myRowDim, first, limit, tmpColumn);
                }
            };

            tmpConquerer.invoke(iterationPoint + 1, myColDim, ApplyCholesky.THRESHOLD);

        } else {

            ApplyCholesky.invoke(tmpData, myRowDim, iterationPoint + 1, myColDim, tmpColumn);
        }
    }

    @Override public void applyLDL( int iterationPoint,  BasicArray<Double> multipliers) {

         double[] column = ((ArrayR064) multipliers).data;

        if (myColDim - iterationPoint - 1 > ApplyLDL.THRESHOLD) {

             var conquerer = new DivideAndConquer() {

                @Override
                protected void conquer( int first,  int limit) {
                    ApplyLDL.invoke(data, myRowDim, first, limit, column, iterationPoint);
                }
            };

            conquerer.invoke(iterationPoint + 1, myColDim, ApplyLDL.THRESHOLD);

        } else {

            ApplyLDL.invoke(data, myRowDim, iterationPoint + 1, myColDim, column, iterationPoint);
        }
    }

    @Override public void applyLU( int iterationPoint,  BasicArray<Double> multipliers) {

         double[] column = ((ArrayR064) multipliers).data;

        if (myColDim - iterationPoint - 1 > ApplyLU.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                protected void conquer( int first,  int limit) {
                    ApplyLU.invoke(data, myRowDim, first, limit, column, iterationPoint);
                }
            };

            tmpConquerer.invoke(iterationPoint + 1, myColDim, ApplyLU.THRESHOLD);

        } else {

            ApplyLU.invoke(data, myRowDim, iterationPoint + 1, myColDim, column, iterationPoint);
        }
    }

    @Override public Array1D<Double> asList() {
        return myUtility.flatten();
    }

    public void caxpy( double aSclrA,  int aColX,  int aColY,  int aFirstRow) {
        AXPY.invoke(data, aColY * myRowDim + aFirstRow, aSclrA, data, aColX * myRowDim + aFirstRow, 0, myRowDim - aFirstRow);
    }

    @Override public Array1D<ComplexNumber> computeInPlaceSchur( PhysicalStore<Double> transformationCollector,  boolean eigenvalue) {

        // final PrimitiveDenseStore tmpThisCopy = this.copy();
        // final PrimitiveDenseStore tmpCollCopy = (PrimitiveDenseStore)
        // aTransformationCollector.copy();
        //
        // tmpThisCopy.computeInPlaceHessenberg(true);

        // Actual

         double[] tmpData = data;

         double[] tmpCollectorData = ((Primitive64Store) transformationCollector).data;

         double[] tmpVctrWork = new double[this.getMinDim()];
        EvD1D.orthes(tmpData, tmpCollectorData, tmpVctrWork);

        // BasicLogger.logDebug("Schur Step", this);
        // BasicLogger.logDebug("Hessenberg", tmpThisCopy);

         double[][] tmpDiags = EvD1D.hqr2(tmpData, tmpCollectorData, eigenvalue);
         double[] aRawReal = tmpDiags[0];
         double[] aRawImag = tmpDiags[1];
         int tmpLength = Math.min(aRawReal.length, aRawImag.length);

         ArrayC128 retVal = ArrayC128.make(tmpLength);
         ComplexNumber[] tmpRaw = retVal.data;

        for (int i = 0; i < tmpLength; i++) {
            tmpRaw[i] = ComplexNumber.of(aRawReal[i], aRawImag[i]);
        }

        return Array1D.C128.wrap(retVal);
    }

    @Override public MatrixStore<Double> conjugate() {
        return this.transpose();
    }

    @Override public Primitive64Store copy() {
        return new Primitive64Store(myRowDim, myColDim, this.copyOfData());
    }

    @Override public long countColumns() {
        return myColDim;
    }

    @Override public long countRows() {
        return myRowDim;
    }

    @Override public void divideAndCopyColumn( int row,  int column,  BasicArray<Double> destination) {

        double[] destinationData = ((ArrayR064) destination).data;

        @Var int index = row + column * myRowDim;
        double denominator = data[index];

        for (int i = row + 1; i < myRowDim; i++) {
            destinationData[i] = data[++index] /= denominator;
        }
    }

    @Override public double doubleValue( long row,  long col) {
        return myUtility.doubleValue(row, col);
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Primitive64Store)) {
            return false;
        }
        var other = (Primitive64Store) obj;
        if (myColDim != other.myColDim || myRowDim != other.myRowDim) {
            return false;
        }
        return true;
    }

    @Override public void exchangeColumns( long colA,  long colB) {
        myUtility.exchangeColumns(colA, colB);
    }

    @Override public void exchangeHermitian( int indexA,  int indexB) {

         int indexMin = Math.min(indexA, indexB);
         int indexMax = Math.max(indexA, indexB);

        @Var double tmpVal;

        for (int j = 0; j < indexMin; j++) {
            tmpVal = this.doubleValue(indexMin, j);
            this.set(indexMin, j, this.doubleValue(indexMax, j));
            this.set(indexMax, j, tmpVal);
        }

        tmpVal = this.doubleValue(indexMin, indexMin);
        this.set(indexMin, indexMin, this.doubleValue(indexMax, indexMax));
        this.set(indexMax, indexMax, tmpVal);

        for (int ij = indexMin + 1; ij < indexMax; ij++) {
            tmpVal = this.doubleValue(ij, indexMin);
            this.set(ij, indexMin, this.doubleValue(indexMax, ij));
            this.set(indexMax, ij, tmpVal);
        }

        for (int i = indexMax + 1; i < myRowDim; i++) {
            tmpVal = this.doubleValue(i, indexMin);
            this.set(i, indexMin, this.doubleValue(i, indexMax));
            this.set(i, indexMax, tmpVal);
        }

    }

    @Override public void exchangeRows( long rowA,  long rowB) {
        myUtility.exchangeRows(rowA, rowB);
    }

    @Override public void fillByMultiplying( Access1D<Double> left,  Access1D<Double> right) {

         int complexity = Math.toIntExact(left.count() / this.countRows());
        if (complexity != Math.toIntExact(right.count() / this.countColumns())) {
            ProgrammingError.throwForMultiplicationNotPossible();
        }

        if (left instanceof Primitive64Store) {
            if (right instanceof Primitive64Store) {
                multiplyNeither.invoke(data, Primitive64Store.cast(left).data, complexity, Primitive64Store.cast(right).data);
            } else {
                multiplyRight.invoke(data, Primitive64Store.cast(left).data, complexity, right);
            }
        } else if (right instanceof Primitive64Store) {
            multiplyLeft.invoke(data, left, complexity, Primitive64Store.cast(right).data);
        } else {
            multiplyBoth.invoke(this, left, complexity, right);
        }
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

    @Override public void fillDiagonal( long row,  long col,  Double value) {
        myUtility.fillDiagonal(row, col, value);
    }

    @Override public void fillDiagonal( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillDiagonal(row, col, supplier);
    }

    @Override
    public void fillMatching( Access1D<?> values) {

        if (values instanceof TransjugatedStore) {
             var transposed = (TransjugatedStore<?>) values;

            if (myColDim > FillMatchingSingle.THRESHOLD) {

                 var tmpConquerer = new DivideAndConquer() {

                    @Override
                    public void conquer( int first,  int limit) {
                        FillMatchingSingle.transpose(data, myRowDim, first, limit, transposed.getOriginal());
                    }

                };

                tmpConquerer.invoke(0, myColDim, FillMatchingSingle.THRESHOLD);

            } else {

                FillMatchingSingle.transpose(data, myRowDim, 0, myColDim, transposed.getOriginal());
            }

        } else {

            super.fillMatching(values);
        }
    }

    @Override
    public void fillMatching( Access1D<Double> left,  BinaryFunction<Double> function,  Access1D<Double> right) {

        int matchingCount = MissingMath.toMinIntExact(this.count(), left.count(), right.count());

        if (myColDim > FillMatchingDual.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                protected void conquer( int first,  int limit) {
                    OperationBinary.invoke(data, first, limit, 1, left, function, right);
                }

            };

            tmpConquerer.invoke(0, matchingCount, FillMatchingDual.THRESHOLD * FillMatchingDual.THRESHOLD);

        } else {

            OperationBinary.invoke(data, 0, matchingCount, 1, left, function, right);
        }
    }

    @Override
    public void fillMatching( UnaryFunction<Double> function,  Access1D<Double> arguments) {

        int matchingCount = MissingMath.toMinIntExact(this.count(), arguments.count());

        if (myColDim > FillMatchingSingle.THRESHOLD) {

             var tmpConquerer = new DivideAndConquer() {

                @Override
                protected void conquer( int first,  int limit) {
                    OperationUnary.invoke(data, first, limit, 1, arguments, function);
                }

            };

            tmpConquerer.invoke(0, matchingCount, FillMatchingSingle.THRESHOLD * FillMatchingSingle.THRESHOLD);

        } else {

            OperationUnary.invoke(data, 0, matchingCount, 1, arguments, function);
        }
    }

    public void fillOne( long row,  long col,  Access1D<?> values,  long valueIndex) {
        this.set(row, col, values.doubleValue(valueIndex));
    }

    public void fillOne( long row,  long col,  Double value) {
        myUtility.fillOne(row, col, value);
    }

    public void fillOne( long row,  long col,  NullaryFunction<?> supplier) {
        myUtility.fillOne(row, col, supplier);
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

    @Override public boolean generateApplyAndCopyHouseholderColumn( int row,  int column,  Householder<Double> destination) {
        return GenerateApplyAndCopyHouseholderColumn.invoke(data, myRowDim, row, column, (Householder.Primitive64) destination);
    }

    @Override public boolean generateApplyAndCopyHouseholderRow( int row,  int column,  Householder<Double> destination) {
        return GenerateApplyAndCopyHouseholderRow.invoke(data, myRowDim, row, column, (Householder.Primitive64) destination);
    }

    public MatrixStore<Double> get() {
        return this;
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

    @Override
    public void modifyAll( UnaryFunction<Double> modifier) {

        if (myColDim > ModifyAll.THRESHOLD) {

             var conquerer = new DivideAndConquer() {

                @Override
                public void conquer( int first,  int limit) {
                    Primitive64Store.this.modify(myRowDim * first, myRowDim * limit, 1, modifier);
                }

            };

            conquerer.invoke(0, myColDim, ModifyAll.THRESHOLD);

        } else {

            this.modify(0, myRowDim * myColDim, 1, modifier);
        }
    }

    @Override public void modifyColumn( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyColumn(row, col, modifier);
    }

    @Override public void modifyDiagonal( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyDiagonal(row, col, modifier);
    }

    @Override public void modifyOne( long row,  long col,  UnaryFunction<Double> modifier) {

        @Var double tmpValue = this.doubleValue(row, col);

        tmpValue = modifier.invoke(tmpValue);

        this.set(row, col, tmpValue);
    }

    @Override public void modifyRow( long row,  long col,  UnaryFunction<Double> modifier) {
        myUtility.modifyRow(row, col, modifier);
    }

    @Override public MatrixStore<Double> multiply( MatrixStore<Double> right) {

        Primitive64Store retVal = FACTORY.make(myRowDim, right.countColumns());

        if (right instanceof Primitive64Store) {
            retVal.multiplyNeither.invoke(retVal.data, data, myColDim, Primitive64Store.cast(right).data);
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

    @Override public void negateColumn( int column) {
        myUtility.modifyColumn(0, column, PrimitiveMath.NEGATE);
    }

    @Override public PhysicalStore.Factory<Double, Primitive64Store> physical() {
        return FACTORY;
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

    @Override public void rotateRight( int low,  int high,  double cos,  double sin) {
        RotateRight.invoke(data, myRowDim, low, high, cos, sin);
    }

    @Override public void set( long row,  long col,  Comparable<?> value) {
        myUtility.set(row, col, value);
    }

    @Override public void set( long row,  long col,  double value) {
        myUtility.set(row, col, value);
    }

    @Override public void setToIdentity( int col) {
        myUtility.set(col, col, ONE);
        myUtility.fillColumn(col + 1, col, ZERO);
    }

    @Override public Array1D<Double> sliceColumn( long row,  long col) {
        return myUtility.sliceColumn(row, col);
    }

    @Override public Array1D<Double> sliceDiagonal( long row,  long col) {
        return myUtility.sliceDiagonal(row, col);
    }

    @Override public Array1D<Double> sliceRange( long first,  long limit) {
        return myUtility.sliceRange(first, limit);
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
                    SubstituteBackwards.invoke(Primitive64Store.this.data, tmpRowDim, first, limit, body, unitDiagonal, conjugated, hermitian);
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
                    SubstituteForwards.invoke(Primitive64Store.this.data, tmpRowDim, first, limit, body, unitDiagonal, conjugated, identity);
                }

            };

            tmpConquerer.invoke(0, tmpColDim, SubstituteForwards.THRESHOLD);

        } else {

            SubstituteForwards.invoke(data, tmpRowDim, 0, tmpColDim, body, unitDiagonal, conjugated, identity);
        }
    }

    @Override public PrimitiveScalar toScalar( long row,  long column) {
        return PrimitiveScalar.of(this.doubleValue(row, column));
    }

    @Override
    public String toString() {
        return Access2D.toString(this);
    }

    @Override public void transformLeft( Householder<Double> transformation,  int firstColumn) {
        HouseholderLeft.call(data, myRowDim, firstColumn, Primitive64Store.cast(transformation));
    }

    @Override public void transformLeft( Rotation<Double> transformation) {

         Rotation.Primitive tmpTransf = Primitive64Store.cast(transformation);

         int tmpLow = tmpTransf.low;
         int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateLeft.invoke(data, myRowDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
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
        HouseholderRight.call(data, myRowDim, firstRow, Primitive64Store.cast(transformation), this.getWorkerColumn());
    }

    @Override public void transformRight( Rotation<Double> transformation) {

         Rotation.Primitive tmpTransf = Primitive64Store.cast(transformation);

         int tmpLow = tmpTransf.low;
         int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateRight.invoke(data, myRowDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
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

    @Override public void transformSymmetric( Householder<Double> transformation) {
        HouseholderHermitian.invoke(data, Primitive64Store.cast(transformation), this.getWorkerColumn());
    }

    @Override public void tred2( BasicArray<Double> mainDiagonal,  BasicArray<Double> offDiagonal,  boolean yesvecs) {
        HouseholderHermitian.tred2j(data, ((ArrayR064) mainDiagonal).data, ((ArrayR064) offDiagonal).data, yesvecs);
    }

    @Override public void visitColumn( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitColumn(row, col, visitor);
    }

    @Override public void visitDiagonal( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitDiagonal(row, col, visitor);
    }

    @Override public void visitRow( long row,  long col,  VoidFunction<Double> visitor) {
        myUtility.visitRow(row, col, visitor);
    }

    private double[] getWorkerColumn() {
        if (myWorkerColumn != null) {
            Arrays.fill(myWorkerColumn, ZERO);
        } else {
            myWorkerColumn = new double[myRowDim];
        }
        return myWorkerColumn;
    }

}
