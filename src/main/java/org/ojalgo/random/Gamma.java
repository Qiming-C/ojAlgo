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
package org.ojalgo.random;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import com.google.errorprone.annotations.Var;
import org.ojalgo.scalar.PrimitiveScalar;

/**
 * Distribution of the sum of aCount random variables with an exponential distribution with parameter aLambda.
 *
 * @author apete
 */
public class Gamma extends RandomNumber {

    private final double myShape;
    private final double myRate;

    public Gamma() {
        this(ONE, ONE);
    }

    public Gamma( double aShape,  double aRate) {

        super();

        myShape = aShape;
        myRate = aRate;
    }

    @Override public double getExpected() {
        return myShape / myRate;
    }

    @Override
    public double getVariance() {
        return myShape / (myRate * myRate);
    }

    /**
     * A Convenient Way of Generating Gamma Random Variables Using Generalized Exponential Distribution
     *
     * @see org.ojalgo.random.RandomNumber#generate()
     */
    @Override
    protected double generate() {

         var tmpInteger = (int) myShape;
         double tmpFraction = myShape - tmpInteger;

        @Var double tmpIntegralPart = ZERO;
        for (int i = 0; i < tmpInteger; i++) {
            tmpIntegralPart -= LOG.invoke(this.random().nextDouble());
        }

        @Var double tmpFractionalPart = ZERO;
        if (!PrimitiveScalar.isSmall(ONE, tmpFraction)) {

             double tmpFractionMinusOne = tmpFraction - ONE;

            @Var double tmpNegHalfFraction;
            @Var double tmpNumer;
            @Var double tmpDenom;

            do {

                tmpFractionalPart = -TWO * LOG.invoke(ONE - POW.invoke(this.random().nextDouble(), ONE / tmpFraction));
                tmpNegHalfFraction = -tmpFractionalPart / TWO;

                tmpNumer = POW.invoke(tmpFractionalPart, tmpFractionMinusOne) * EXP.invoke(tmpNegHalfFraction);
                tmpDenom = POW.invoke(TWO, tmpFractionMinusOne) * POW.invoke(-EXPM1.invoke(tmpNegHalfFraction), tmpFractionMinusOne);

            } while (this.random().nextDouble() > (tmpNumer / tmpDenom));
        }

        return (tmpIntegralPart + tmpFractionalPart) / myRate;
    }

}
