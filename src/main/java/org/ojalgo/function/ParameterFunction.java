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

import java.util.function.BiFunction;

import org.ojalgo.ProgrammingError;
import org.ojalgo.type.NumberDefinition;

public interface ParameterFunction<N extends Comparable<N>> extends BasicFunction, BiFunction<N, Integer, N> {

    /**
     * A {@linkplain ParameterFunction} with a set/fixed parameter.
     *
     * @author apete
     */
    public static final class FixedParameter<N extends Comparable<N>> implements UnaryFunction<N> {

        private final ParameterFunction<N> myFunction;
        private final int myParameter;

        @SuppressWarnings("unused")
        private FixedParameter() {
            this(null, 0);
        }

        FixedParameter( ParameterFunction<N> function,  int param) {

            super();

            myFunction = function;
            myParameter = param;
        }

        public ParameterFunction<N> getFunction() {
            return myFunction;
        }

        public int getParameter() {
            return myParameter;
        }

        @Override public double invoke( double arg) {
            return myFunction.invoke(arg, myParameter);
        }

        @Override public float invoke( float arg) {
            return myFunction.invoke(arg, myParameter);
        }

        @Override public N invoke( N arg) {
            return myFunction.invoke(arg, myParameter);
        }

    }

    default ParameterFunction<N> andThen( UnaryFunction<N> after) {
        ProgrammingError.throwIfNull(after);
        return new ParameterFunction<N>() {

            @Override public double invoke( double arg,  int param) {
                return after.invoke(ParameterFunction.this.invoke(arg, param));
            }

            @Override public float invoke( float arg,  int param) {
                return after.invoke(ParameterFunction.this.invoke(arg, param));
            }

            @Override public N invoke( N arg,  int param) {
                return after.invoke(ParameterFunction.this.invoke(arg, param));
            }

        };
    }

    @Override default N apply( N arg,  Integer param) {
        return this.invoke(arg, param.intValue());
    }

    default ParameterFunction<N> compose( UnaryFunction<N> before) {
        ProgrammingError.throwIfNull(before);
        return new ParameterFunction<N>() {

            @Override public double invoke( double arg,  int param) {
                return ParameterFunction.this.invoke(before.invoke(arg), param);
            }

            @Override public float invoke( float arg,  int param) {
                return ParameterFunction.this.invoke(before.invoke(arg), param);
            }

            @Override public N invoke( N arg,  int param) {
                return ParameterFunction.this.invoke(before.invoke(arg), param);
            }

        };
    }

    default byte invoke( byte arg,  int param) {
        return (byte) this.invoke((double) arg, param);
    }

    double invoke(double arg, int param);

    default float invoke( float arg,  int param) {
        return (float) this.invoke((double) arg, param);
    }

    default int invoke( int arg,  int param) {
        return NumberDefinition.toInt(this.invoke((double) arg, param));
    }

    default long invoke( long arg,  int param) {
        return NumberDefinition.toLong(this.invoke((double) arg, param));
    }

    N invoke(N arg, int param);

    default short invoke( short arg,  int param) {
        return (short) this.invoke((double) arg, param);
    }

    /**
     * Turns this parameter function into a unary function with the parameter fixed/locked to the specified
     * value.
     *
     * @param param The parameter of the parameter function.
     * @return The resulting unary function.
     */
    default UnaryFunction<N> parameter( int param) {
        return new FixedParameter<>(this, param);
    }

}
