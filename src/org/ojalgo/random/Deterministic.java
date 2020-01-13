/*
 * Copyright 1997-2020 Optimatika
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

import org.ojalgo.scalar.Scalar;

/**
 * @author apete
 */
public class Deterministic extends RandomNumber {

    private final double myValue;

    public Deterministic() {

        super();

        myValue = ZERO;
    }

    public Deterministic(final Comparable<?> aValue) {

        super();

        myValue = Scalar.doubleValue(aValue);
    }

    public Deterministic(final double aValue) {

        super();

        myValue = aValue;
    }

    public int compareTo(final Deterministic o) {
        return Double.compare(myValue, o.myValue);
    }

    public double getExpected() {
        return myValue;
    }

    @Override
    public double getStandardDeviation() {
        return ZERO;
    }

    @Override
    public double getVariance() {
        return ZERO;
    }

    @Override
    protected double generate() {
        return myValue;
    }

}
