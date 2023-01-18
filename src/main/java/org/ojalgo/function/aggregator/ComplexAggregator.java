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

import org.ojalgo.function.constant.ComplexMath;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;

public final class ComplexAggregator extends AggregatorSet<ComplexNumber> {

    static abstract class ComplexAggregatorFunction implements AggregatorFunction<ComplexNumber> {

        @Override public final double doubleValue() {
            return this.get().doubleValue();
        }

        @Override public final void invoke( double anArg) {
            this.invoke(ComplexNumber.valueOf(anArg));
        }

        @Override public final void invoke( float anArg) {
            this.invoke(ComplexNumber.valueOf(anArg));
        }

        public final Scalar<ComplexNumber> toScalar() {
            return this.get();
        }

    }

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> AVERAGE = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private int myCount = 0;
                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber.divide(myCount);
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myCount++;
                    myNumber = myNumber.add(anArg);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myCount = 0;
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> CARDINALITY = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private int myCount = 0;

                @Override public ComplexNumber get() {
                    return ComplexNumber.valueOf(myCount);
                }

                @Override public int intValue() {
                    return myCount;
                }

                @Override public void invoke( ComplexNumber anArg) {
                    if (!PrimitiveScalar.isSmall(PrimitiveMath.ONE, anArg.norm())) {
                        myCount++;
                    }
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myCount = 0;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> LARGEST = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = ComplexMath.MAX.invoke(myNumber, ComplexMath.ABS.invoke(anArg));
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> MAX = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = ComplexMath.MAX.invoke(myNumber, anArg);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> MIN = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.INFINITY;

                @Override public ComplexNumber get() {
                    if (ComplexNumber.isInfinite(myNumber)) {
                        return ComplexNumber.ZERO;
                    }
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = ComplexMath.MIN.invoke(myNumber, anArg);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.INFINITY;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> NORM1 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg.norm());
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> NORM2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return ComplexNumber.valueOf(PrimitiveMath.SQRT.invoke(myNumber.norm()));
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                     double tmpMod = anArg.norm();
                    myNumber = myNumber.add(tmpMod * tmpMod);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> PRODUCT = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ONE;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = myNumber.multiply(anArg);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ONE;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> PRODUCT2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ONE;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = myNumber.multiply(anArg.multiply(anArg));
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ONE;
                    return this;
                }

            };
        }
    };

    private static final ComplexAggregator SET = new ComplexAggregator();

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> SMALLEST = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.INFINITY;

                @Override public ComplexNumber get() {
                    if (ComplexNumber.isInfinite(myNumber)) {
                        return ComplexNumber.ZERO;
                    }
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    if (!ComplexNumber.isSmall(PrimitiveMath.ONE, anArg)) {
                        myNumber = ComplexMath.MIN.invoke(myNumber, ComplexMath.ABS.invoke(anArg));
                    }
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.INFINITY;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> SUM = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg);
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    private static final ThreadLocal<AggregatorFunction<ComplexNumber>> SUM2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new ComplexAggregatorFunction() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                @Override public ComplexNumber get() {
                    return myNumber;
                }

                @Override public int intValue() {
                    return this.get().intValue();
                }

                @Override public void invoke( ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg.multiply(anArg));
                }

                @Override public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

            };
        }
    };

    public static ComplexAggregator getSet() {
        return SET;
    }

    private ComplexAggregator() {
        super();
    }

    @Override
    public AggregatorFunction<ComplexNumber> average() {
        return AVERAGE.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> cardinality() {
        return CARDINALITY.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> largest() {
        return LARGEST.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> maximum() {
        return MAX.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> minimum() {
        return MIN.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> norm1() {
        return NORM1.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> norm2() {
        return NORM2.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> product() {
        return PRODUCT.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> product2() {
        return PRODUCT2.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> smallest() {
        return SMALLEST.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> sum() {
        return SUM.get().reset();
    }

    @Override
    public AggregatorFunction<ComplexNumber> sum2() {
        return SUM2.get().reset();
    }

}
