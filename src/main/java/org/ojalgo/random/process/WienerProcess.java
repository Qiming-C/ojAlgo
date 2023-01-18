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
package org.ojalgo.random.process;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import org.ojalgo.function.special.ErrorFunction;
import org.ojalgo.random.Normal;

public final class WienerProcess extends SingleValueBasedProcess<Normal> implements Process1D.ComponentProcess<Normal> {

    private static final Normal GENERATOR = new Normal();

    public WienerProcess() {

        super();

        this.setCurrentValue(ZERO);
    }

    @SuppressWarnings("unused")
    private WienerProcess( double initialValue) {

        super();

        this.setCurrentValue(initialValue);
    }

    @Override public Normal getDistribution( double evaluationPoint) {
        return new Normal(this.getCurrentValue(), SQRT.invoke(evaluationPoint));
    }

    @Override public double getValue() {
        return this.getCurrentValue();
    }

    @Override public void setValue( double newValue) {
        this.setCurrentValue(newValue);
    }

    @Override
    public double step( double stepSize,  double standardGaussianInnovation) {
        return this.doStep(stepSize, standardGaussianInnovation);
    }

    @Override
    double doStep( double stepSize,  double normalisedRandomIncrement) {
        double retVal = this.getCurrentValue() + (SQRT.invoke(stepSize) * normalisedRandomIncrement);
        this.setCurrentValue(retVal);
        return retVal;
    }

    @Override
    double getExpected( double stepSize) {
        return this.getCurrentValue();
    }

    @Override
    double getLowerConfidenceQuantile( double stepSize,  double confidence) {
        return this.getCurrentValue() - (SQRT.invoke(stepSize) * SQRT_TWO * ErrorFunction.erfi(confidence));
    }

    @Override
    double getNormalisedRandomIncrement() {
        return GENERATOR.doubleValue();
    }

    @Override
    double getStandardDeviation( double stepSize) {
        return SQRT.invoke(stepSize);
    }

    @Override
    double getUpperConfidenceQuantile( double stepSize,  double confidence) {
        return this.getCurrentValue() + (SQRT.invoke(stepSize) * SQRT_TWO * ErrorFunction.erfi(confidence));
    }

    @Override
    double getVariance( double stepSize) {
        return stepSize;
    }

}
