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
package org.ojalgo.function;

import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.type.NumberDefinition;

public interface UnaryFunction<N extends Comparable<N>> extends BasicFunction, UnaryOperator<N>, DoubleUnaryOperator {

    static <N extends Comparable<N>> boolean isZeroModified( UnaryFunction<N> function) {
        return !PrimitiveScalar.isSmall(PrimitiveMath.ONE, function.invoke(PrimitiveMath.ZERO));
    }

    default UnaryFunction<N> andThen( UnaryFunction<N> after) {
        ProgrammingError.throwIfNull(after);
        return new UnaryFunction<N>() {

            @Override public double invoke( double arg) {
                return after.invoke(UnaryFunction.this.invoke(arg));
            }

            @Override public float invoke( float arg) {
                return after.invoke(UnaryFunction.this.invoke(arg));
            }

            @Override public N invoke( N arg) {
                return after.invoke(UnaryFunction.this.invoke(arg));
            }

        };
    }

    @Override default N apply( N arg) {
        return this.invoke(arg);
    }

    @Override default double applyAsDouble( double arg) {
        return this.invoke(arg);
    }

    default UnaryFunction<N> compose( UnaryFunction<N> before) {
        ProgrammingError.throwIfNull(before);
        return new UnaryFunction<N>() {

            @Override public double invoke( double arg) {
                return UnaryFunction.this.invoke(before.invoke(arg));
            }

            @Override public float invoke( float arg) {
                return UnaryFunction.this.invoke(before.invoke(arg));
            }

            @Override public N invoke( N arg) {
                return UnaryFunction.this.invoke(before.invoke(arg));
            }

        };
    }

    default byte invoke( byte arg) {
        return (byte) this.invoke((double) arg);
    }

    double invoke(double arg);

    default float invoke( float arg) {
        return (float) this.invoke((double) arg);
    }

    default int invoke( int arg) {
        return NumberDefinition.toInt(this.invoke((double) arg));
    }

    default long invoke( long arg) {
        return NumberDefinition.toLong(this.invoke((double) arg));
    }

    N invoke(N arg);

    default short invoke( short arg) {
        return (short) this.invoke((double) arg);
    }

}
