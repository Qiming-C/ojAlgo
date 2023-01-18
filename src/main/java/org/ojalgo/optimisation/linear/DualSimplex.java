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
package org.ojalgo.optimisation.linear;

import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.OptimisationData;
import org.ojalgo.optimisation.linear.SimplexTableau.MetaData;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D.RowView;
import org.ojalgo.structure.ElementView1D;
import org.ojalgo.type.context.NumberContext;

final class DualSimplex extends SimplexTableauSolver {

    

    static SimplexTableau build( OptimisationData convex,  Optimisation.Options options,  boolean checkFeasibility) {

        int nbVars = convex.countVariables();
        int nbEqus = convex.countEqualityConstraints();
        int nbInes = convex.countInequalityConstraints();

        SimplexTableau retVal = SimplexTableau.make(nbVars, nbEqus + nbEqus + nbInes, 0, 0, 0, true, options);
        MetaData meta = retVal.meta;
        Primitive2D constraintsBody = retVal.constraintsBody();
        Primitive1D constraintsRHS = retVal.constraintsRHS();
        Primitive1D objective = retVal.objective();

        MatrixStore<Double> convexC = convex.getObjective().getLinearFactors(true);

        for (int i = 0; i < nbVars; i++) {
            double rhs = checkFeasibility ? ZERO : convexC.doubleValue(i);
            boolean neg = meta.negatedDual[i] = NumberContext.compare(rhs, ZERO) < 0;
            constraintsRHS.set(i, neg ? -rhs : rhs);
        }

        if (nbEqus > 0) {
            for (RowView<Double> rowAE : convex.getRowsAE()) {
                int j = Math.toIntExact(rowAE.row());

                for (ElementView1D<Double, ?> element : rowAE.nonzeros()) {
                    int i = Math.toIntExact(element.index());

                    boolean neg = meta.negatedDual[i];

                    double valE = element.doubleValue();
                    constraintsBody.set(i, j, neg ? -valE : valE);
                    constraintsBody.set(i, nbEqus + j, neg ? valE : -valE);

                }
            }
        }

        if (nbInes > 0) {
            for (RowView<Double> rowAI : convex.getRowsAI()) {
                int j = Math.toIntExact(rowAI.row());

                for (ElementView1D<Double, ?> element : rowAI.nonzeros()) {
                    int i = Math.toIntExact(element.index());

                    double valI = element.doubleValue();
                    constraintsBody.set(i, nbEqus + nbEqus + j, meta.negatedDual[i] ? -valI : valI);
                }
            }
        }

        for (int j = 0; j < nbEqus; j++) {
            double valBE = convex.getBE(j);
            objective.set(j, valBE);
            objective.set(nbEqus + j, -valBE);
        }
        for (int j = 0; j < nbInes; j++) {
            double valBI = convex.getBI(j);
            objective.set(nbEqus + nbEqus + j, valBI);
        }

        return retVal;
    }

    static Optimisation.Result doSolve( OptimisationData convex,  Optimisation.Options options,  boolean zeroC) {

        SimplexTableau tableau = DualSimplex.build(convex, options, zeroC);

        var solver = new DualSimplex(tableau, options);

        Result result = solver.solve();

        return DualSimplex.toConvexState(result, convex);
    }

    static int size( OptimisationData convex) {

        int numbVars = convex.countVariables();
        int numbEqus = convex.countEqualityConstraints();
        int numbInes = convex.countInequalityConstraints();

        return SimplexTableau.size(numbVars, numbEqus + numbEqus + numbInes, 0, 0, true);
    }

    static Optimisation.Result toConvexState( Result result,  OptimisationData convex) {

        int nbEqus = convex.countEqualityConstraints();
        int nbInes = convex.countInequalityConstraints();

        Access1D<?> multipliers = result.getMultipliers().get();

        var retVal = new Optimisation.Result(result.getState(), result.getValue(), result);

        retVal.multipliers(new Primitive1D() {

            @Override
            public int size() {
                return nbEqus + nbInes;
            }

            @Override
            double doubleValue( int index) {
                if (index < nbEqus) {
                    return -(multipliers.doubleValue(index) - multipliers.doubleValue(nbEqus + index));
                }
                return -multipliers.doubleValue(nbEqus + index);
            }

            @Override
            void set( int index,  double value) {
                throw new IllegalArgumentException();
            }

        });

        return retVal;
    }

    DualSimplex( SimplexTableau tableau,  Options solverOptions) {
        super(tableau, solverOptions);
    }

    @Override
    protected double evaluateFunction( Access1D<?> solution) {
        return -super.evaluateFunction(solution);
    }

    @Override
    protected Access1D<?> extractMultipliers() {
        return super.extractSolution();
    }

    @Override
    protected Access1D<?> extractSolution() {
        return super.extractMultipliers();
    }

    @Override
    protected State getState() {

        State state = super.getState();

        if (state == State.UNBOUNDED) {
            return State.INFEASIBLE;
        }
        if (!state.isFeasible()) {
            return State.UNBOUNDED;
        }
        return state;
    }

}
