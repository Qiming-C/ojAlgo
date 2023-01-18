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
package org.ojalgo.optimisation.convex;

import org.ojalgo.function.multiary.LinearFunction;
import org.ojalgo.function.multiary.MultiaryFunction;
import org.ojalgo.function.multiary.PureQuadraticFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.structure.Access1D;

/**
 * [x]<sup>T</sup>[Q][x] - [l]<sup>T</sup>[x]
 *
 * @author apete
 */
public final class ConvexObjectiveFunction implements MultiaryFunction.TwiceDifferentiable<Double>, MultiaryFunction.Quadratic<Double> {

    private final LinearFunction<Double> myLinear;
    private final PureQuadraticFunction<Double> myPureQuadratic;

    ConvexObjectiveFunction( PhysicalStore<Double> quadratic,  PhysicalStore<Double> linear) {

        super();

        myPureQuadratic = PureQuadraticFunction.wrap(quadratic);
        myLinear = LinearFunction.wrap(linear);

        if (myPureQuadratic.arity() != myLinear.arity()) {
            throw new IllegalArgumentException("Must have the same arity!");
        }
    }

    @Override public int arity() {
        return myLinear.arity();
    }

    @Override public Double getConstant() {
        return myPureQuadratic.getConstant();
    }

    @Override public MatrixStore<Double> getGradient( Access1D<Double> point) {
        return myPureQuadratic.getGradient(point).subtract(myLinear.getGradient(point));
    }

    @Override public MatrixStore<Double> getHessian( Access1D<Double> point) {
        return myPureQuadratic.getHessian(point);
    }

    @Override public MatrixStore<Double> getLinearFactors( boolean negated) {
        return myLinear.getLinearFactors(!negated);
    }

    @Override
    public Double invoke( Access1D<Double> arg) {
        double quadratic = myPureQuadratic.invoke(arg).doubleValue();
        double linear = myLinear.invoke(arg).doubleValue();
        return Double.valueOf(quadratic - linear);
    }

    @Override public PhysicalStore<Double> linear() {
        return myLinear.linear();
    }

    @Override public PhysicalStore<Double> quadratic() {
        return myPureQuadratic.quadratic();
    }

    @Override public void setConstant( Comparable<?> constant) {
        myPureQuadratic.setConstant(constant);
    }

}
