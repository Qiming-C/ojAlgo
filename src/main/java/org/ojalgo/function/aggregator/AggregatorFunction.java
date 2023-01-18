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
package org.ojalgo.function.aggregator;

import org.ojalgo.function.PredicateFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.AccessScalar;

public interface AggregatorFunction<N extends Comparable<N>> extends VoidFunction<N>, AccessScalar<N> {

    final class PredicateWrapper<N extends Comparable<N>> implements AggregatorFunction<N> {

        private final AggregatorFunction<N> myAggregator;
        private final PredicateFunction<N> myPredicate;

        PredicateWrapper( PredicateFunction<N> predicate,  AggregatorFunction<N> aggregator) {
            super();
            myPredicate = predicate;
            myAggregator = aggregator;
        }

        @Override public boolean booleanValue() {
            return myAggregator.booleanValue();
        }

        @Override public byte byteValue() {
            return myAggregator.byteValue();
        }

        @Override public double doubleValue() {
            return myAggregator.doubleValue();
        }

        @Override public float floatValue() {
            return myAggregator.floatValue();
        }

        @Override public N get() {
            return myAggregator.get();
        }

        @Override public int intValue() {
            return myAggregator.intValue();
        }

        @Override public void invoke( byte arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( double arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( float arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( int arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( long arg) {
            if (myPredicate.test((double) arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( N arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public void invoke( short arg) {
            if (myPredicate.test(arg)) {
                myAggregator.invoke(arg);
            }
        }

        @Override public long longValue() {
            return myAggregator.longValue();
        }

        @Override public AggregatorFunction<N> reset() {
            return myAggregator.reset();
        }

        @Override public short shortValue() {
            return myAggregator.shortValue();
        }

        public Scalar<N> toScalar() {
            return myAggregator.toScalar();
        }

    }

    /**
     * Only the values that pass the predicate filter will actually be part of the aggregation.
     */
    default AggregatorFunction<N> filter( PredicateFunction<N> predicate) {
        return new PredicateWrapper<>(predicate, this);
    }

    AggregatorFunction<N> reset();

    /**
     * @deprecated v53 Will be removed
     */
    @Deprecated
    Scalar<N> toScalar();

}
