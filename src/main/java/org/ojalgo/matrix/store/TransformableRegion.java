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
package org.ojalgo.matrix.store;

import com.google.errorprone.annotations.Var;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate2D.ModifiableReceiver;
import org.ojalgo.structure.Transformation2D;

/**
 * A transformable 2D (sub)region.
 *
 * @author apete
 */
public interface TransformableRegion<N extends Comparable<N>> extends ModifiableReceiver<N> {

    @FunctionalInterface
    interface FillByMultiplying<N extends Comparable<N>> {

        void invoke(TransformableRegion<N> product, Access1D<N> left, int complexity, Access1D<N> right);

        default void invoke( TransformableRegion<N> product,  Access1D<N> left,  long complexity,  Access1D<N> right) {
            this.invoke(product, left, Math.toIntExact(complexity), right);
        }

    }

    @Override default void exchangeColumns( long colA,  long colB) {
        @Var N valA, valB;
        for (long i = 0L, limit = this.countRows(); i < limit; i++) {
            valA = this.get(i, colA);
            valB = this.get(i, colB);
            this.set(i, colB, valA);
            this.set(i, colA, valB);
        }
    }

    @Override default void exchangeRows( long rowA,  long rowB) {
        @Var N valA, valB;
        for (long j = 0L, limit = this.countColumns(); j < limit; j++) {
            valA = this.get(rowA, j);
            valB = this.get(rowB, j);
            this.set(rowB, j, valA);
            this.set(rowA, j, valB);
        }
    }

    void fillByMultiplying( Access1D<N> left,  Access1D<N> right);

    @Override default void modifyAny( Transformation2D<N> modifier) {
        modifier.transform(this);
    }

    /**
     *Returns a consumer (sub)region.
 
     */
    TransformableRegion<N> regionByColumns(int... columns);

    /**
     *Returns a consumer (sub)region.
 
     */
    TransformableRegion<N> regionByLimits(int rowLimit, int columnLimit);

    /**
     *Returns a consumer (sub)region.
 
     */
    TransformableRegion<N> regionByOffsets(int rowOffset, int columnOffset);

    /**
     *Returns a consumer (sub)region.
 
     */
    TransformableRegion<N> regionByRows(int... rows);

    /**
     *Returns a transposed consumer region.
 
     */
    TransformableRegion<N> regionByTransposing();

}
