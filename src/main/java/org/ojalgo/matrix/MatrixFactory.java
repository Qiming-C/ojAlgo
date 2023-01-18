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
package org.ojalgo.matrix;

import com.google.errorprone.annotations.Var;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.ArrayR064;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.matrix.store.ElementsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.SparseStore;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Access2D;
import org.ojalgo.structure.Factory1D;
import org.ojalgo.structure.Factory2D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Structure2D;
import org.ojalgo.tensor.TensorFactory1D;
import org.ojalgo.tensor.TensorFactory2D;

/**
 * MatrixFactory creates instances of classes that implement the {@linkplain org.ojalgo.matrix.BasicMatrix}
 * interface and have a constructor that takes a MatrixStore as input.
 *
 * @author apete
 */
public abstract class MatrixFactory<N extends Comparable<N>, M extends BasicMatrix<N, M>, DR extends Mutate2D.ModifiableReceiver<N> & Supplier<M>, SR extends Mutate2D.ModifiableReceiver<N> & Supplier<M>>
        implements Factory2D.Dense<M>, Factory2D.MayBeSparse<M, DR, SR> {

    private static Constructor<? extends BasicMatrix<?, ?>> getConstructor( Class<? extends BasicMatrix<?, ?>> template) {
        try {
            Constructor<? extends BasicMatrix<?, ?>> retVal = template.getDeclaredConstructor(ElementsSupplier.class);
            retVal.setAccessible(true);
            return retVal;
        } catch (SecurityException | NoSuchMethodException cause) {
            throw new ProgrammingError(cause);
        }
    }

    private final Constructor<M> myConstructor;
    private final PhysicalStore.Factory<N, ?> myPhysicalFactory;

    MatrixFactory( Class<M> template,  PhysicalStore.Factory<N, ?> factory) {

        super();

        myPhysicalFactory = factory;
        myConstructor = (Constructor<M>) MatrixFactory.getConstructor(template);
    }

    @Override public M columns( Access1D<?>... source) {
        return this.instantiate(myPhysicalFactory.columns(source));
    }

    @Override public M columns( Comparable<?>[]... source) {
        return this.instantiate(myPhysicalFactory.columns(source));
    }

    @Override public M columns( double[]... source) {
        return this.instantiate(myPhysicalFactory.columns(source));
    }

    @Override public M columns( List<? extends Comparable<?>>... source) {
        return this.instantiate(myPhysicalFactory.columns(source));
    }

    @Override public M copy( Access2D<?> source) {
        return this.instantiate(myPhysicalFactory.copy(source));
    }

    @Override
    public FunctionSet<N> function() {
        return myPhysicalFactory.function();
    }

    @Override public M make( long rows,  long columns) {
        return this.instantiate(myPhysicalFactory.makeZero(rows, (int) columns));
    }

    public DR makeDense( int count) {
        return this.makeDense(count, 1);
    }

    @Override public DR makeDense( long rows,  long columns) {
        return this.dense(myPhysicalFactory.make(rows, columns));
    }

    public M makeDiagonal( Access1D<?> diagonal) {
        return this.instantiate(myPhysicalFactory.makeDiagonal(diagonal).get());
    }

    public M makeDiagonal( double... diagonal) {
        return this.makeDiagonal(ArrayR064.wrap(diagonal));
    }

    public M makeEye( int rows,  int columns) {

         int square = Math.min(rows, columns);

        @Var MatrixStore<N> retVal = myPhysicalFactory.makeIdentity(square);

        if (rows > square) {
            retVal = retVal.below(rows - square);
        } else if (columns > square) {
            retVal = retVal.right(columns - square);
        }

        return this.instantiate(retVal);
    }

    public M makeEye( Structure2D shape) {
        return this.makeEye(Math.toIntExact(shape.countRows()), Math.toIntExact(shape.countColumns()));
    }

    @Override public M makeFilled( long rows,  long columns,  NullaryFunction<?> supplier) {
        return this.instantiate(myPhysicalFactory.makeFilled(rows, columns, supplier));
    }

    public M makeIdentity( int dimension) {
        return this.instantiate(myPhysicalFactory.makeIdentity(dimension));
    }

    public M makeSingle( N element) {
        return this.instantiate(myPhysicalFactory.makeSingle(element));
    }

    @Override public SR makeSparse( long rows,  long columns) {
        return this.sparse(myPhysicalFactory.makeSparse(rows, columns));
    }

    @Override public SR makeSparse( Structure2D shape) {
        return this.makeSparse(shape.countRows(), Math.toIntExact(shape.countColumns()));
    }

    public M makeWrapper( Access2D<?> elements) {
        return this.instantiate(myPhysicalFactory.makeWrapper(elements));
    }

    @Override public M rows( Access1D<?>... source) {
        return this.instantiate(myPhysicalFactory.rows(source));
    }

    @Override public M rows( Comparable<?>[]... source) {
        return this.instantiate(myPhysicalFactory.rows(source));
    }

    @Override public M rows( double[]... source) {
        return this.instantiate(myPhysicalFactory.rows(source));
    }

    @Override public M rows( List<? extends Comparable<?>>... source) {
        return this.instantiate(myPhysicalFactory.rows(source));
    }

    @Override
    public Scalar.Factory<N> scalar() {
        return myPhysicalFactory.scalar();
    }

    public TensorFactory1D<N, DR> tensor1D() {
        return TensorFactory1D.of(new Factory1D<DR>() {

            @Override public FunctionSet<N> function() {
                return MatrixFactory.this.function();
            }

            @Override public DR make( long count) {
                return MatrixFactory.this.makeDense(count, 1L);
            }

            @Override public Scalar.Factory<N> scalar() {
                return MatrixFactory.this.scalar();
            }

        });
    }

    public TensorFactory2D<N, DR> tensor2D() {
        return TensorFactory2D.of(new Factory2D<DR>() {

            @Override public FunctionSet<N> function() {
                return MatrixFactory.this.function();
            }

            @Override public DR make( long rows,  long columns) {
                return MatrixFactory.this.makeDense(rows, columns);
            }

            @Override public Scalar.Factory<N> scalar() {
                return MatrixFactory.this.scalar();
            }

        });
    }

    abstract DR dense( PhysicalStore<N> store);

    final PhysicalStore.Factory<N, ?> getPhysicalFactory() {
        return myPhysicalFactory;
    }

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    M instantiate( ElementsSupplier<N> supplier) {
        try {
            return myConstructor.newInstance(supplier);
        } catch ( IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException cause) {
            throw new ProgrammingError(cause);
        }
    }

    abstract SR sparse( SparseStore<N> store);

}
