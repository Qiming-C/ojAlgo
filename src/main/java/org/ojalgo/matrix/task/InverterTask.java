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

import java.util.Optional;
import java.util.function.Supplier;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.matrix.Provider2D;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Quadruple;
import org.ojalgo.scalar.Quaternion;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Structure2D;

public interface InverterTask<N extends Comparable<N>> extends MatrixTask<N> {

    public static abstract class Factory<N extends Comparable<N>> {

        public MatrixStore<N> invert( Access2D<?> original) throws RecoverableCondition {
            return this.make(original, false, false).invert(original);
        }

        public InverterTask<N> make( int dim,  boolean spd) {

            var template = new Structure2D() {

                @Override public long countColumns() {
                    return dim;
                }

                @Override public long countRows() {
                    return dim;
                }
            };

            return this.make(template, spd, spd);
        }

        public InverterTask<N> make( MatrixStore<N> template) {
            return this.make(template, template.isHermitian(), false);
        }

        public abstract InverterTask<N> make(Structure2D template, boolean symmetric, boolean positiveDefinite);
    }

    Factory<ComplexNumber> COMPLEX = new Factory<>() {

        @Override
        public InverterTask<ComplexNumber> make( Structure2D template,  boolean symmetric,  boolean positiveDefinite) {
            if (symmetric && positiveDefinite) {
                return Cholesky.COMPLEX.make(template);
            }
            if (template.isSquare()) {
                return LU.COMPLEX.make(template);
            }
            if (template.isTall()) {
                return QR.COMPLEX.make(template);
            }
            return SingularValue.COMPLEX.make(template);
        }

    };

    Factory<Double> PRIMITIVE = new Factory<>() {

        @Override
        public InverterTask<Double> make( Structure2D template,  boolean symmetric,  boolean positiveDefinite) {

            long tmpDim = template.countRows();

            if (symmetric) {
                if (tmpDim == 1L) {
                    return AbstractInverter.FULL_1X1;
                }
                if (tmpDim == 2L) {
                    return AbstractInverter.SYMMETRIC_2X2;
                }
                if (tmpDim == 3L) {
                    return AbstractInverter.SYMMETRIC_3X3;
                }
                if (tmpDim == 4L) {
                    return AbstractInverter.SYMMETRIC_4X4;
                }
                if (tmpDim == 5L) {
                    return AbstractInverter.SYMMETRIC_5X5;
                }
                return positiveDefinite ? Cholesky.PRIMITIVE.make(template) : LU.PRIMITIVE.make(template);
            }
            if (template.isSquare()) {
                if (tmpDim == 1L) {
                    return AbstractInverter.FULL_1X1;
                }
                if (tmpDim == 2L) {
                    return AbstractInverter.FULL_2X2;
                }
                if (tmpDim == 3L) {
                    return AbstractInverter.FULL_3X3;
                }
                if (tmpDim == 4L) {
                    return AbstractInverter.FULL_4X4;
                }
                if (tmpDim == 5L) {
                    return AbstractInverter.FULL_5X5;
                }
                return LU.PRIMITIVE.make(template);
            }
            if (template.isTall()) {
                return QR.PRIMITIVE.make(template);
            }
            return SingularValue.PRIMITIVE.make(template);
        }

    };

    Factory<Quadruple> QUADRUPLE = new Factory<>() {

        @Override
        public InverterTask<Quadruple> make( Structure2D template,  boolean symmetric,  boolean positiveDefinite) {
            if (template.isSquare()) {
                if (symmetric && positiveDefinite) {
                    return Cholesky.QUADRUPLE.make(template);
                }
                return LU.QUADRUPLE.make(template);
            }
            if (template.isTall()) {
                return QR.QUADRUPLE.make(template);
            }
            return SingularValue.QUADRUPLE.make(template);
        }

    };

    Factory<Quaternion> QUATERNION = new Factory<>() {

        @Override
        public InverterTask<Quaternion> make( Structure2D template,  boolean symmetric,  boolean positiveDefinite) {
            if (template.isSquare()) {
                if (symmetric && positiveDefinite) {
                    return Cholesky.QUATERNION.make(template);
                }
                return LU.QUATERNION.make(template);
            }
            if (template.isTall()) {
                return QR.QUATERNION.make(template);
            }
            return SingularValue.QUATERNION.make(template);
        }

    };

    Factory<RationalNumber> RATIONAL = new Factory<>() {

        @Override
        public InverterTask<RationalNumber> make( Structure2D template,  boolean symmetric,  boolean positiveDefinite) {
            if (template.isSquare()) {
                if (symmetric && positiveDefinite) {
                    return Cholesky.RATIONAL.make(template);
                }
                return LU.RATIONAL.make(template);
            }
            if (template.isTall()) {
                return QR.RATIONAL.make(template);
            }
            return SingularValue.RATIONAL.make(template);
        }

    };

    /**
     * The output must be a "right inverse" and a "generalised inverse".
     */
    default MatrixStore<N> invert( Access2D<?> original) throws RecoverableCondition {
        return this.invert(original, this.preallocate(original));
    }

    /**
     * <p>
     * Exactly how (if at all) a specific implementation makes use of <code>preallocated</code> is not
     * specified by this interface. It must be documented for each implementation.
     * </p>
     * <p>
     * Should produce the same results as calling {@link #invert(Access2D)}.
     * </p>
     * <p>
     * Use {@link #preallocate(Structure2D)} to obtain a suitbale <code>preallocated</code>.
     * </p>
     *
     * @param preallocated Preallocated memory for the results, possibly some intermediate results. You must
     *        assume this is modified, but you cannot assume it will contain the full/ /correct solution.
     * @return The inverse
     * @throws RecoverableCondition TODO
     */
    MatrixStore<N> invert(Access2D<?> original, PhysicalStore<N> preallocated) throws RecoverableCondition;

    default PhysicalStore<N> preallocate( int numberOfRows,  int numberOfColumns) {
        return this.preallocate(new Structure2D() {

            @Override public long countColumns() {
                return numberOfColumns;
            }

            @Override public long countRows() {
                return numberOfRows;
            }

        });
    }

    /**
     * <p>
     * Will create a {@linkplain PhysicalStore} instance suitable for use with
     * {@link #invert(Access2D, PhysicalStore)}.
     * </p>
     * <p>
     * When inverting a matrix (mxn) the preallocated memory/matrix will typically be nxm (and of course most
     * of the time A is square).
     * </p>
     */
    PhysicalStore<N> preallocate(Structure2D template);

    default Provider2D.Inverse<Optional<MatrixStore<N>>> toInverseProvider( ElementsSupplier<N> original,
             Supplier<MatrixStore<N>> alternativeOriginalSupplier) {
        try {
            MatrixStore<N> invert = this.invert(alternativeOriginalSupplier.get());
            return () -> Optional.of(invert);
        } catch (RecoverableCondition cause) {
            return Optional::empty;
        }
    }

}
