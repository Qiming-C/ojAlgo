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

import com.google.errorprone.annotations.Var;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.structure.Access1D;

public class PolynomialC128 extends AbstractPolynomial<ComplexNumber> {

    public PolynomialC128( int degree) {
        super(Array1D.C128.make(degree + 1));
    }

    PolynomialC128( Array1D<ComplexNumber> coefficients) {
        super(coefficients);
    }

    @Override public void estimate( Access1D<?> x,  Access1D<?> y) {
        this.estimate(x, y, GenericStore.C128, QR.C128);
    }

    @Override public ComplexNumber integrate( ComplexNumber fromPoint,  ComplexNumber toPoint) {

        PolynomialFunction<ComplexNumber> primitive = this.buildPrimitive();

        ComplexNumber fromVal = primitive.invoke(fromPoint);
        ComplexNumber toVal = primitive.invoke(toPoint);

        return toVal.subtract(fromVal);
    }

    @Override public ComplexNumber invoke( ComplexNumber arg) {

        @Var int power = this.degree();

        @Var ComplexNumber retVal = this.get(power);

        while (--power >= 0) {
            retVal = this.get(power).add(arg.multiply(retVal));
        }

        return retVal;
    }

    @Override public void set( Access1D<?> coefficients) {
        int limit = Math.min(this.size(), coefficients.size());
        for (int p = 0; p < limit; p++) {
            this.set(p, ComplexNumber.valueOf(coefficients.get(p)));
        }
    }

    @Override
    protected ComplexNumber getDerivativeFactor( int power) {
        int nextIndex = power + 1;
        return this.get(nextIndex).multiply(nextIndex);
    }

    @Override
    protected ComplexNumber getPrimitiveFactor( int power) {
        if (power <= 0) {
            return ComplexNumber.ZERO;
        }
        return this.get(power - 1).divide(power);
    }

    @Override
    protected AbstractPolynomial<ComplexNumber> makeInstance( int size) {
        return new PolynomialC128(Array1D.C128.make(size));
    }

}
