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
package org.ojalgo.function.polynomial;

import org.ojalgo.array.Array1D;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.Primitive32Store;
import org.ojalgo.structure.Access1D;

public final class PolynomialR032 extends AbstractPolynomial<Double> {

    public PolynomialR032( int degree) {
        super(Array1D.R032.make(degree + 1));
    }

    PolynomialR032( Array1D<Double> coefficients) {
        super(coefficients);
    }

    @Override public void estimate( Access1D<?> x,  Access1D<?> y) {
        this.estimate(x, y, Primitive32Store.FACTORY, QR.R064);
    }

    @Override public Double integrate( Double fromPoint,  Double toPoint) {

        PolynomialFunction<Double> primitive = this.buildPrimitive();

        double fromVal = primitive.invoke(fromPoint.doubleValue());
        double toVal = primitive.invoke(toPoint.doubleValue());

        return Double.valueOf(toVal - fromVal);
    }

    @Override public Double invoke( Double arg) {
        return Double.valueOf(this.invoke(arg.doubleValue()));
    }

    @Override public void set( Access1D<?> coefficients) {
        int limit = Math.min(this.size(), coefficients.size());
        for (int p = 0; p < limit; p++) {
            this.set(p, coefficients.doubleValue(p));
        }
    }

    @Override
    protected Double getDerivativeFactor( int power) {
        int nextIndex = power + 1;
        return Double.valueOf(nextIndex * this.doubleValue(nextIndex));
    }

    @Override
    protected Double getPrimitiveFactor( int power) {
        if (power <= 0) {
            return Double.valueOf(PrimitiveMath.ZERO);
        }
        return Double.valueOf(this.doubleValue(power - 1) / power);
    }

    @Override
    protected AbstractPolynomial<Double> makeInstance( int size) {
        return new PolynomialR032(Array1D.R032.make(size));
    }

}
