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
package org.ojalgo.matrix.decomposition;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.array.operation.AXPY;
import org.ojalgo.array.operation.SWAP;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Access2D.Collectable;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.type.context.NumberContext;

final class RawLU extends RawDecomposition implements LU<Double> {

    private final Pivot myPivot = new Pivot();

    /**
     * Not recommended to use this constructor directly. Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.LU#make(Access2D)} instead.
     */
    RawLU() {
        super();
    }

    @Override public Double calculateDeterminant( Access2D<?> matrix) {

         double[][] data = this.reset(matrix, false);

        this.getInternalStore().fillMatching(matrix);

        this.doDecompose(data, true);

        return this.getDeterminant();
    }

    @Override public int countSignificant( double threshold) {

        RawStore internal = this.getInternalStore();

        @Var int significant = 0;
        for (int ij = 0, limit = this.getMinDim(); ij < limit; ij++) {
            if (Math.abs(internal.doubleValue(ij, ij)) > threshold) {
                significant++;
            }
        }

        return significant;
    }

    @Override public boolean decompose( Access2D.Collectable<Double, ? super PhysicalStore<Double>> matrix) {

         double[][] data = this.reset(matrix, false);

        matrix.supplyTo(this.getInternalStore());

        return this.doDecompose(data, true);
    }

    @Override public boolean decomposeWithoutPivoting( Collectable<Double, ? super PhysicalStore<Double>> matrix) {

         double[][] data = this.reset(matrix, false);

        matrix.supplyTo(this.getInternalStore());

        return this.doDecompose(data, false);
    }

    @Override public Double getDeterminant() {
         int m = this.getRowDim();
         int n = this.getColDim();
        if (m != n) {
            throw new IllegalArgumentException("RawStore must be square.");
        }
         double[][] LU = this.getInternalData();
        @Var double d = myPivot.signum();
        for (int j = 0; j < n; j++) {
            d *= LU[j][j];
        }
        return d;
    }

    @Override public MatrixStore<Double> getInverse() {
         int tmpRowDim = this.getRowDim();
        return this.doGetInverse(this.allocate(tmpRowDim, tmpRowDim));
    }

    @Override public MatrixStore<Double> getInverse( PhysicalStore<Double> preallocated) {
        return this.doGetInverse(preallocated);
    }

    @Override public MatrixStore<Double> getL() {
        MatrixStore<Double> logical = this.getInternalStore().triangular(false, true);
        int nbRows = this.getRowDim();
        if (nbRows < this.getColDim()) {
            return logical.limits(nbRows, nbRows);
        }
        return logical;
    }

    @Override public int[] getPivotOrder() {
        return myPivot.getOrder();
    }

    @Override public int[] getReversePivotOrder() {
        return myPivot.reverseOrder();
    }

    @Override public double getRankThreshold() {

        double largest = this.getInternalStore().aggregateDiagonal(Aggregator.LARGEST);
        double epsilon = this.getDimensionalEpsilon();

        return epsilon * Math.max(MACHINE_SMALLEST, largest);
    }

    @Override public MatrixStore<Double> getSolution( Collectable<Double, ? super PhysicalStore<Double>> rhs) {
         DecompositionStore<Double> tmpPreallocated = this.allocate(rhs.countRows(), rhs.countColumns());
        return this.getSolution(rhs, tmpPreallocated);
    }

    @Override
    public MatrixStore<Double> getSolution( Collectable<Double, ? super PhysicalStore<Double>> rhs,  PhysicalStore<Double> preallocated) {

        this.collect(rhs).rows(myPivot.getOrder()).supplyTo(preallocated);

        return this.doSolve(preallocated);
    }

    @Override public MatrixStore<Double> getU() {
        @Var MatrixStore<Double> retVal = this.getInternalStore().triangular(true, false);
        int nbCols = this.getColDim();
        if (this.getRowDim() > nbCols) {
            retVal = retVal.limits(nbCols, nbCols);
        }
        return retVal;
    }

    @Override
    public MatrixStore<Double> invert( Access2D<?> original,  PhysicalStore<Double> preallocated) throws RecoverableCondition {

         double[][] tmpData = this.reset(original, false);

        this.getInternalStore().fillMatching(original);

        this.doDecompose(tmpData, true);

        if (this.isSolvable()) {
            return this.getInverse(preallocated);
        }
        throw RecoverableCondition.newMatrixNotInvertible();
    }

    @Override public boolean isPivoted() {
        return myPivot.isModified();
    }

    @Override
    public boolean isSolvable() {
        return super.isSolvable();
    }

    @Override public PhysicalStore<Double> preallocate( Structure2D template) {
        return this.allocate(template.countRows(), template.countRows());
    }

    @Override public PhysicalStore<Double> preallocate( Structure2D templateBody,  Structure2D templateRHS) {
        return this.allocate(templateBody.countRows(), templateRHS.countColumns());
    }

    @Override
    public MatrixStore<Double> solve( Access2D<?> body,  Access2D<?> rhs,  PhysicalStore<Double> preallocated) throws RecoverableCondition {

         double[][] tmpData = this.reset(body, false);

        this.getInternalStore().fillMatching(body);

        this.doDecompose(tmpData, true);

        if (this.isSolvable()) {

            Primitive64Store.FACTORY.makeWrapper(rhs).rows(myPivot.getOrder()).supplyTo(preallocated);

            return this.doSolve(preallocated);

        }
        throw RecoverableCondition.newEquationSystemNotSolvable();
    }

    private boolean doDecompose( double[][] data,  boolean pivoting) {

         int m = this.getRowDim();
         int n = this.getColDim();

        myPivot.reset(m);

        @Var double[] rowP;
        @Var double[] rowI;

        @Var double valP;
        @Var double valI;

        // Main loop along the diagonal
        for (int ij = 0, limit = Math.min(m, n); ij < limit; ij++) {

            if (pivoting) {
                @Var int p = ij;
                valP = ABS.invoke(data[p][ij]);
                for (int i = ij + 1; i < m; i++) {
                    valI = ABS.invoke(data[i][ij]);
                    if (valI > valP) {
                        p = i;
                        valP = valI;
                    }
                }
                if (p != ij) {
                    SWAP.exchangeRows(data, ij, p);
                    myPivot.change(ij, p);
                }
            }

            rowP = data[ij];
            valP = rowP[ij];

            if (NumberContext.compare(valP, ZERO) != 0) {
                for (int i = ij + 1; i < m; i++) {

                    rowI = data[i];
                    valI = rowI[ij] / valP;

                    if (NumberContext.compare(valI, ZERO) != 0) {
                        rowI[ij] = valI;
                        AXPY.invoke(rowI, 0, -valI, rowP, 0, ij + 1, n);
                    }
                }
            }
        }

        return this.computed(true);
    }

    private MatrixStore<Double> doGetInverse( PhysicalStore<Double> preallocated) {

        int[] pivotOrder = myPivot.getOrder();
        int numbRows = this.getRowDim();
        for (int i = 0; i < numbRows; i++) {
            preallocated.set(i, pivotOrder[i], ONE);
        }

        RawStore body = this.getInternalStore();

        preallocated.substituteForwards(body, true, false, !myPivot.isModified());

        preallocated.substituteBackwards(body, false, false, false);

        return preallocated;
    }

    private MatrixStore<Double> doSolve( PhysicalStore<Double> preallocated) {

        MatrixStore<Double> body = this.getInternalStore();

        preallocated.substituteForwards(body, true, false, false);

        preallocated.substituteBackwards(body, false, false, false);

        return preallocated;
    }

    @Override
    protected boolean checkSolvability() {
        return this.isSquare() && this.isFullRank();
    }

}
