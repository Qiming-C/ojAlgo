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
package org.ojalgo.scalar;

/**
 * price or exchange rate as in "amount = price * quatity" or "amount = rate * amount"
 *
 * @author apete
 */
public final class Price extends ExactDecimal<Price> {

    public static final Descriptor DESCRIPTOR = new Descriptor(8);

    public static final Scalar.Factory<Price> FACTORY = new ExactDecimal.Factory<>() {

        @Override public Price cast( double value) {
            return Price.valueOf(value);
        }

        @Override public Price cast( Comparable<?> number) {
            return Price.valueOf(number);
        }

        @Override public Price convert( double value) {
            return Price.valueOf(value);
        }

        @Override public Price convert( Comparable<?> number) {
            return Price.valueOf(number);
        }

        @Override public Descriptor descriptor() {
            return DESCRIPTOR;
        }

        @Override public Price one() {
            return ONE;
        }

        @Override public Price zero() {
            return ZERO;
        }

    };

    private static final double DOUBLE_DENOMINATOR = 100_000_000D;
    private static final long LONG_DENOMINATOR = 100_000_000L;

    public static final Price NEG = new Price(-LONG_DENOMINATOR);
    public static final Price ONE = new Price(LONG_DENOMINATOR);
    public static final Price TWO = new Price(LONG_DENOMINATOR + LONG_DENOMINATOR);
    public static final Price ZERO = new Price();

    public static Price valueOf( double value) {
        return new Price(Math.round(value * DOUBLE_DENOMINATOR));
    }

    public static Price valueOf( Comparable<?> number) {

        if (number == null) {
            return ZERO;
        }

        if (number instanceof Price) {
            return (Price) number;
        }

        return Price.valueOf(Scalar.doubleValue(number));
    }

    public Price() {
        super(0L);
    }

    Price( long numerator) {
        super(numerator);
    }

    public Amount multiply( Quantity quanntity) {
        return new Amount(Amount.DESCRIPTOR.multiply(this, quanntity));
    }

    @Override
    protected Descriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    protected Price wrap( long numerator) {
        return new Price(numerator);
    }

}
