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
package org.ojalgo.matrix.task;

import java.util.function.Supplier;

import org.ojalgo.matrix.Provider2D;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Quadruple;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Structure2D;

public interface DeterminantTask<N extends Comparable<N>> extends MatrixTask<N> {

    public static abstract class Factory<N extends Comparable<N>> {

        public final DeterminantTask<N> make(final int dim, final boolean symmetric) {

            final Structure2D template = new Structure2D() {

                public long countColumns() {
                    return dim;
                }

                public long countRows() {
                    return dim;
                }
            };

            return this.make(template, symmetric, false);
        }

        public final DeterminantTask<N> make(final MatrixStore<N> template) {
            return this.make(template, template.isHermitian(), false);
        }

        public abstract DeterminantTask<N> make(Structure2D template, boolean symmetric, boolean positiveDefinite);

    }

    Factory<ComplexNumber> COMPLEX = new Factory<>() {

        @Override
        public DeterminantTask<ComplexNumber> make(final Structure2D template, final boolean symmetric, final boolean positiveDefinite) {
            if (symmetric && positiveDefinite) {
                return Cholesky.COMPLEX.make(template);
            }
            return LU.COMPLEX.make(template);
        }

    };

    Factory<Double> PRIMITIVE = new Factory<>() {

        @Override
        public DeterminantTask<Double> make(final Structure2D template, final boolean symmetric, final boolean positiveDefinite) {
            final long tmpDim = template.countRows();
            if (tmpDim == 1L) {
                return AbstractDeterminator.FULL_1X1;
            }
            if (symmetric) {
                if (tmpDim == 2L) {
                    return AbstractDeterminator.SYMMETRIC_2X2;
                }
                if (tmpDim == 3L) {
                    return AbstractDeterminator.SYMMETRIC_3X3;
                }
                if (tmpDim == 4L) {
                    return AbstractDeterminator.SYMMETRIC_4X4;
                } else if (tmpDim == 5L) {
                    return AbstractDeterminator.SYMMETRIC_5X5;
                } else {
                    return positiveDefinite ? Cholesky.PRIMITIVE.make(template) : LU.PRIMITIVE.make(template);
                }
            }
            if (tmpDim == 2L) {
                return AbstractDeterminator.FULL_2X2;
            }
            if (tmpDim == 3L) {
                return AbstractDeterminator.FULL_3X3;
            }
            if (tmpDim == 4L) {
                return AbstractDeterminator.FULL_4X4;
            } else if (tmpDim == 5L) {
                return AbstractDeterminator.FULL_5X5;
            } else {
                return LU.PRIMITIVE.make(template);
            }
        }

    };

    Factory<Quadruple> QUADRUPLE = new Factory<>() {

        @Override
        public DeterminantTask<Quadruple> make(final Structure2D template, final boolean symmetric, final boolean positiveDefinite) {
            if (symmetric && positiveDefinite) {
                return Cholesky.QUADRUPLE.make(template);
            }
            return LU.QUADRUPLE.make(template);
        }

    };

    Factory<Quaternion> QUATERNION = new Factory<>() {

        @Override
        public DeterminantTask<Quaternion> make(final Structure2D template, final boolean symmetric, final boolean positiveDefinite) {
            if (symmetric && positiveDefinite) {
                return Cholesky.QUATERNION.make(template);
            }
            return LU.QUATERNION.make(template);
        }

    };

    Factory<RationalNumber> RATIONAL = new Factory<>() {

        @Override
        public DeterminantTask<RationalNumber> make(final Structure2D template, final boolean symmetric, final boolean positiveDefinite) {
            if (symmetric && positiveDefinite) {
                return Cholesky.RATIONAL.make(template);
            }
            return LU.RATIONAL.make(template);
        }

    };

    N calculateDeterminant(Access2D<?> matrix);

    default Provider2D.Determinant<N> toDeterminantProvider(final ElementsSupplier<N> original, final Supplier<MatrixStore<N>> alternativeOriginalSupplier) {

        N determinant = this.calculateDeterminant(alternativeOriginalSupplier.get());

        return () -> determinant;
    }
}
