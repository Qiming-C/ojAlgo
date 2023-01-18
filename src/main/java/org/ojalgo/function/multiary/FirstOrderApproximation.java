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

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.structure.Access1D;

public final class FirstOrderApproximation<N extends Comparable<N>> extends ApproximateFunction<N> {

    private final AffineFunction<N> myDelegate;

    public FirstOrderApproximation( MultiaryFunction.TwiceDifferentiable<N> function,  Access1D<N> point) {

        super(function, point);

         MatrixStore<N> linear = function.getGradient(point);

        N constant = function.invoke(point);

        myDelegate = new AffineFunction<>(linear);
        myDelegate.setConstant(constant);
    }

    @Override public int arity() {
        return myDelegate.arity();
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof FirstOrderApproximation)) {
            return false;
        }
         var other = (FirstOrderApproximation<?>) obj;
        if (myDelegate == null) {
            if (other.myDelegate != null) {
                return false;
            }
        } else if (!myDelegate.equals(other.myDelegate)) {
            return false;
        }
        return true;
    }

    @Override public MatrixStore<N> getGradient( Access1D<N> point) {
        return myDelegate.getGradient(null);
    }

    @Override public MatrixStore<N> getHessian( Access1D<N> point) {
        return myDelegate.getHessian(null);
    }

    @Override
    public int hashCode() {
         int prime = 31;
        int result = 1;
        return (prime * result) + ((myDelegate == null) ? 0 : myDelegate.hashCode());
    }

    @Override public N invoke( Access1D<N> arg) {
        return myDelegate.invoke(this.shift(arg));
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    @Override
    PhysicalStore.Factory<N, ?> factory() {
        return myDelegate.factory();
    }

}
