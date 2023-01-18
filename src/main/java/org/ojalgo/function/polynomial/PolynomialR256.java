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
import java.math.BigDecimal;
import org.ojalgo.array.Array1D;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.structure.Access1D;
import org.ojalgo.type.TypeUtils;

/**
 * BigPolynomial
 *
 * @author apete
 */
public class PolynomialR256 extends AbstractPolynomial<BigDecimal> {

    public PolynomialR256( int degree) {
        super(Array1D.R256.make(degree + 1));
    }

    PolynomialR256( Array1D<BigDecimal> coefficients) {
        super(coefficients);
    }

    @Override public void estimate( Access1D<?> x,  Access1D<?> y) {

        var delegate = new PolynomialQ128(this.degree());

        delegate.estimate(x, y);

        this.set(delegate);
    }

    @Override public BigDecimal integrate( BigDecimal fromPoint,  BigDecimal toPoint) {

        PolynomialFunction<BigDecimal> primitive = this.buildPrimitive();

        BigDecimal fromVal = primitive.invoke(fromPoint);
        BigDecimal toVal = primitive.invoke(toPoint);

        return toVal.subtract(fromVal);
    }

    @Override public BigDecimal invoke( BigDecimal arg) {

        @Var int power = this.degree();

        @Var BigDecimal retVal = this.get(power);

        while (--power >= 0) {
            retVal = this.get(power).add(arg.multiply(retVal));
        }

        return retVal;
    }

    @Override public void set( Access1D<?> coefficients) {
        int limit = Math.min(this.size(), coefficients.size());
        for (int p = 0; p < limit; p++) {
            this.set(p, TypeUtils.toBigDecimal(coefficients.get(p)));
        }
    }

    @Override
    protected BigDecimal getDerivativeFactor( int power) {
        int nextIndex = power + 1;
        return this.get(nextIndex).multiply(new BigDecimal(nextIndex));
    }

    @Override
    protected BigDecimal getPrimitiveFactor( int power) {
        if (power <= 0) {
            return BigMath.ZERO;
        }
        return this.get(power - 1).divide(new BigDecimal(power));
    }

    @Override
    protected AbstractPolynomial<BigDecimal> makeInstance( int size) {
        return new PolynomialR256(Array1D.R256.make(size));
    }

}
