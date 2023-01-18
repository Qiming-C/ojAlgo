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
package org.ojalgo.function.multiary;

import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.Var;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.Primitive64Store;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;

/**
 * [l]<sup>T</sup>[x] + c
 *
 * @author apete
 */
public final class AffineFunction<N extends Comparable<N>> implements MultiaryFunction.TwiceDifferentiable<N>, MultiaryFunction.Affine<N> {

    public static final class Factory<N extends Comparable<N>> {

        private Access1D<?> myCoefficients = null;
        private final PhysicalStore.Factory<N, ?> myFactory;

        Factory( PhysicalStore.Factory<N, ?> factory) {
            super();
            myFactory = factory;
        }

        public Factory<N> coefficients( Access1D<?> coefficients) {
            myCoefficients = coefficients;
            return this;
        }

        public AffineFunction<N> make( int arity) {
            if (myCoefficients != null) {
                return new AffineFunction<>(myFactory.rows(myCoefficients));
            } else {
                return new AffineFunction<>(myFactory.make(1, arity));
            }
        }

    }

    public static <N extends Comparable<N>> Factory<N> factory( PhysicalStore.Factory<N, ?> factory) {
        return new Factory<>(factory);
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @Deprecated
    public static AffineFunction<ComplexNumber> makeComplex( Access1D<?> coefficients) {
        // return new AffineFunction<>(GenericStore.C128.rows(coefficients));
        return AffineFunction.factory(GenericStore.C128).coefficients(coefficients).make(coefficients.size());
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @InlineMe(replacement = "AffineFunction.factory(GenericStore.C128).make(arity)", imports = {"org.ojalgo.function.multiary.AffineFunction", "org.ojalgo.matrix.store.GenericStore"})
@Deprecated
    public static AffineFunction<ComplexNumber> makeComplex( int arity) {
        // return new AffineFunction<>(GenericStore.C128.make(1, arity));
        return AffineFunction.factory(GenericStore.C128).make(arity);
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @Deprecated
    public static AffineFunction<Double> makePrimitive( Access1D<?> coefficients) {
        // return new AffineFunction<>(Primitive64Store.FACTORY.rows(coefficients));
        return AffineFunction.factory(Primitive64Store.FACTORY).coefficients(coefficients).make(coefficients.size());
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @InlineMe(replacement = "AffineFunction.factory(Primitive64Store.FACTORY).make(arity)", imports = {"org.ojalgo.function.multiary.AffineFunction", "org.ojalgo.matrix.store.Primitive64Store"})
@Deprecated
    public static AffineFunction<Double> makePrimitive( int arity) {
        // return new AffineFunction<>(Primitive64Store.FACTORY.make(1, arity));
        return AffineFunction.factory(Primitive64Store.FACTORY).make(arity);
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @Deprecated
    public static AffineFunction<RationalNumber> makeRational( Access1D<?> coefficients) {
        // return new AffineFunction<>(GenericStore.Q128.rows(coefficients));
        return AffineFunction.factory(GenericStore.Q128).coefficients(coefficients).make(coefficients.size());
    }

    /**
     * @deprecated v53 Use {@link #factory(PhysicalStore.Factory)} instead.
     */
    @InlineMe(replacement = "AffineFunction.factory(GenericStore.Q128).make(arity)", imports = {"org.ojalgo.function.multiary.AffineFunction", "org.ojalgo.matrix.store.GenericStore"})
@Deprecated
    public static AffineFunction<RationalNumber> makeRational( int arity) {
        // return new AffineFunction<>(GenericStore.Q128.make(1, arity));
        return AffineFunction.factory(GenericStore.Q128).make(arity);
    }

    public static <N extends Comparable<N>> AffineFunction<N> wrap( PhysicalStore<N> coefficients) {
        return new AffineFunction<>(coefficients);
    }

    private final MatrixStore<N> myCoefficients;
    private final ConstantFunction<N> myConstant;

    AffineFunction( MatrixStore<N> coefficients) {

        super();

        if (!coefficients.isVector()) {
            throw new IllegalArgumentException("Must be a  vector!");
        }

        myCoefficients = coefficients;
        myConstant = new ConstantFunction<>(coefficients.count(), coefficients.physical());
    }

    @Override public int arity() {
        return Math.toIntExact(myCoefficients.count());
    }

    @Override public N getConstant() {
        return myConstant.getConstant();
    }

    @Override
    public MatrixStore<N> getGradient( Access1D<N> point) {
        return this.getLinearFactors(false);
    }

    @Override
    public MatrixStore<N> getHessian( Access1D<N> point) {
        return myCoefficients.physical().makeZero(this.arity(), this.arity());
    }

    @Override public MatrixStore<N> getLinearFactors( boolean negated) {

        @Var MatrixStore<N> retVal = myCoefficients;

        if (myCoefficients.countRows() == 1L) {
            retVal = retVal.transpose();
        }

        if (negated) {
            retVal = retVal.negate();
        }

        return retVal;
    }

    @Override
    public N invoke( Access1D<N> arg) {
        return this.getScalarValue(arg).get();
    }

    @Override public PhysicalStore<N> linear() {
        return (PhysicalStore<N>) myCoefficients;
    }

    @Override public void setConstant( Comparable<?> constant) {
        myConstant.setConstant(constant);
    }

    PhysicalStore.Factory<N, ?> factory() {
        return myCoefficients.physical();
    }

    Scalar<N> getScalarValue( Access1D<N> arg) {

        PhysicalStore<N> preallocated = myCoefficients.physical().make(1L, 1L);

        Scalar<N> retVal = myConstant.getScalarConstant();

        myCoefficients.multiply(arg, preallocated);

        return retVal.add(preallocated.get(0, 0));
    }

}
