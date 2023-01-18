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
package org.ojalgo.array.operation;

import org.ojalgo.array.ArrayR032;
import org.ojalgo.array.ArrayR064;
import org.ojalgo.array.ArrayZ008;
import org.ojalgo.array.ArrayZ016;
import org.ojalgo.array.ArrayZ032;
import org.ojalgo.array.ArrayZ064;
import org.ojalgo.array.BasicArray;
import org.ojalgo.function.BinaryFunction.FixedFirst;
import org.ojalgo.function.BinaryFunction.FixedSecond;
import org.ojalgo.function.ParameterFunction.FixedParameter;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.structure.Access1D;

public abstract class OperationUnary implements ArrayOperation {

    public static int THRESHOLD = 256;

    public static <N extends Comparable<N>> void invoke( BasicArray<N> data,  long first,  long limit,  long step,  Access1D<N> values,
             UnaryFunction<N> function) {

        if (data.isPrimitive()) {
            if (function == PrimitiveMath.NEGATE) {
                CorePrimitiveOperation.negate(data, first, limit, step, values);
            } else {
                for (long i = first; i < limit; i += step) {
                    data.set(i, function.invoke(values.doubleValue(i)));
                }
            }
        } else {
            for (long i = first; i < limit; i += step) {
                data.set(i, function.invoke(values.get(i)));
            }
        }
    }

    public static void invoke( byte[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayZ008) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayZ008) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.byteValue(i));
            }
        }
    }

    public static void invoke( byte[] data,  int first,  int limit,  int step,  byte[] values,  UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.byteValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.byteValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

    public static void invoke( double[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayR064) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayR064) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.doubleValue(i));
            }
        }
    }

    public static void invoke( double[] data,  int first,  int limit,  int step,  double[] values,
             UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.doubleValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.doubleValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

    public static void invoke( float[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayR032) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayR032) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.floatValue(i));
            }
        }
    }

    public static void invoke( float[] data,  int first,  int limit,  int step,  float[] values,
             UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.floatValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.floatValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

    public static void invoke( int[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayZ032) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayZ032) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.intValue(i));
            }
        }
    }

    public static void invoke( int[] data,  int first,  int limit,  int step,  int[] values,  UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.intValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.intValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

    public static void invoke( long[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayZ064) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayZ064) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.longValue(i));
            }
        }
    }

    public static void invoke( long[] data,  int first,  int limit,  int step,  long[] values,  UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.longValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.longValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

    public static <N extends Comparable<N>> void invoke( N[] data,  int first,  int limit,  int step,  Access1D<N> value,
             UnaryFunction<N> function) {
        for (int i = first; i < limit; i += step) {
            data[i] = function.invoke(value.get(i));
        }
    }

    public static void invoke( short[] data,  int first,  int limit,  int step,  Access1D<Double> values,
             UnaryFunction<Double> function) {
        if (values instanceof ArrayZ016) {
            OperationUnary.invoke(data, first, limit, step, ((ArrayZ016) values).data, function);
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values.shortValue(i));
            }
        }
    }

    public static void invoke( short[] data,  int first,  int limit,  int step,  short[] values,
             UnaryFunction<Double> function) {
        if (function == PrimitiveMath.NEGATE) {
            CorePrimitiveOperation.negate(data, first, limit, step, values);
        } else if (function instanceof FixedFirst<?>) {
            var tmpFunc = (FixedFirst<Double>) function;
            OperationBinary.invoke(data, first, limit, step, tmpFunc.shortValue(), tmpFunc.getFunction(), values);
        } else if (function instanceof FixedSecond<?>) {
            var tmpFunc = (FixedSecond<Double>) function;
            OperationBinary.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.shortValue());
        } else if (function instanceof FixedParameter<?>) {
            var tmpFunc = (FixedParameter<Double>) function;
            OperationParameter.invoke(data, first, limit, step, values, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = first; i < limit; i += step) {
                data[i] = function.invoke(values[i]);
            }
        }
    }

}
