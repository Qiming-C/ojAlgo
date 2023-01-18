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
package org.ojalgo.array;

import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.Var;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.operation.AMAX;
import org.ojalgo.array.operation.FillAll;
import org.ojalgo.array.operation.OperationBinary;
import org.ojalgo.array.operation.OperationUnary;
import org.ojalgo.array.operation.OperationVoid;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.function.aggregator.AggregatorSet;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Structure1D;
import org.ojalgo.structure.StructureAnyD;
import org.ojalgo.type.NumberDefinition;
import org.ojalgo.type.math.MathType;

/**
 * <p>
 * The odd member among the array implementations. It allows to create arrays based on memory mapped files or
 * direct buffers.
 * </p>
 *
 * @author apete
 */
public abstract class BufferArray extends PlainArray<Double> implements AutoCloseable {

    public static final class Factory extends DenseArray.Factory<Double> {

        private final BufferConstructor myConstructor;
        private final MathType myMathType;

        Factory( MathType mathType,  BufferConstructor constructor) {
            super();
            myMathType = mathType;
            myConstructor = constructor;
        }

        @Override
        public FunctionSet<Double> function() {
            return PrimitiveFunction.getSet();
        }

        public MappedFileFactory newMapped( File file) {
            return new MappedFileFactory(this, file);
        }

        @Override
        public Scalar.Factory<Double> scalar() {
            return PrimitiveScalar.FACTORY;
        }

        @Override
        AggregatorSet<Double> aggregator() {
            return PrimitiveAggregator.getSet();
        }

        @Override
        long getCapacityLimit() {
            return PlainArray.MAX_SIZE / this.getElementSize();
        }

        @Override
        BufferArray makeDenseArray( long size) {
            int capacity = Math.toIntExact(size * this.getElementSize());
            ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
            return myConstructor.newInstance(this, buffer, null);
        }

        /**
         * Signature matching {@link BufferConstructor}.
         */
        BufferArray newInstance( Factory factory,  ByteBuffer buffer,  AutoCloseable closeable) {
            return myConstructor.newInstance(factory, buffer, closeable);
        }

        @Override
        MathType getMathType() {
            return myMathType;
        }

    }

    public static final class MappedFileFactory extends DenseArray.Factory<Double> {

        private final File myFile;
        private final Factory myTypeFactory;

        MappedFileFactory( Factory typeFactory,  File file) {
            super();
            myTypeFactory = typeFactory;
            myFile = file;
        }

        @Override
        public FunctionSet<Double> function() {
            return myTypeFactory.function();
        }

        @Override public BufferArray makeFilled( Structure1D shape,  NullaryFunction<?> supplier) {
            return (BufferArray) super.makeFilled(shape, supplier);
        }

        @Override
        public BufferArray copy( Access1D<?> source) {
            return (BufferArray) super.copy(source);
        }

        @Override
        public BufferArray copy( Comparable<?>[] source) {
            return (BufferArray) super.copy(source);
        }

        @Override
        public BufferArray copy( double... source) {
            return (BufferArray) super.copy(source);
        }

        @Override
        public BufferArray copy( List<? extends Comparable<?>> source) {
            return (BufferArray) super.copy(source);
        }

        @Override
        public BufferArray make( long count) {
            return (BufferArray) super.make(count);
        }

        @Override
        SegmentedArray<Double> makeSegmented( long... structure) {
            return super.makeSegmented(structure);
        }

        @Override public BufferArray make( int count) {
            return (BufferArray) super.make(count);
        }

        @Override public BufferArray make( Structure1D shape) {
            return (BufferArray) super.make(shape);
        }

        @Override
        public BufferArray makeFilled( long count,  NullaryFunction<?> supplier) {
            return (BufferArray) super.makeFilled(count, supplier);
        }

        @Override
        public Scalar.Factory<Double> scalar() {
            return myTypeFactory.scalar();
        }

        @Override
        AggregatorSet<Double> aggregator() {
            return myTypeFactory.aggregator();
        }

        @Override
        BufferArray makeDenseArray( long size) {

            long count = myTypeFactory.getElementSize() * size;

            FileChannel fileChannel;
            MappedByteBuffer buffer;
            try {
                fileChannel = new RandomAccessFile(myFile, "rw").getChannel();
                buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0L, count);
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }

            return myTypeFactory.newInstance(myTypeFactory, buffer, fileChannel);
        }

        @Override
        MathType getMathType() {
            return myTypeFactory.getMathType();
        }

    }

    @FunctionalInterface
    interface BufferConstructor {

        BufferArray newInstance(BufferArray.Factory factory, ByteBuffer buffer, AutoCloseable closeable);

    }

    public static final Factory R032 = new Factory(MathType.R032, BufferR032::new);
    public static final Factory R064 = new Factory(MathType.R064, BufferR064::new);
    public static final Factory Z008 = new Factory(MathType.Z008, BufferZ008::new);
    public static final Factory Z016 = new Factory(MathType.Z016, BufferZ016::new);
    public static final Factory Z032 = new Factory(MathType.Z032, BufferZ032::new);
    public static final Factory Z064 = new Factory(MathType.Z064, BufferZ064::new);

    /**
     * @deprecated Use {@link #R032} instead
     */
    @Deprecated
    public static final Factory DIRECT32 = R032;
    /**
     * @deprecated Use {@link #R064} instead
     */
    @Deprecated
    public static final Factory DIRECT64 = R064;

    /**
     * @deprecated v52 Use {@link #R064} and {@link MappedFileFactory#make(long)} instead.
     */
    @Deprecated
    public static Array1D<Double> make( File file,  long count) {
        return R064.newMapped(file).make(count).wrapInArray1D();
    }

    /**
     * @deprecated v52 Use {@link #R064} and {@link MappedFileFactory#make(long)} instead.
     */
    @Deprecated
    public static ArrayAnyD<Double> make( File file,  long... structure) {
        return R064.newMapped(file).make(StructureAnyD.count(structure)).wrapInArrayAnyD(structure);
    }

    /**
     * @deprecated v52 Use {@link #R064} and {@link MappedFileFactory#make(long)} instead.
     */
    @Deprecated
    public static Array2D<Double> make( File file,  long rows,  long columns) {
        return R064.newMapped(file).make(rows * columns).wrapInArray2D(rows);
    }

    /**
     * @deprecated v52 Use {@link #R064} and {@link MappedFileFactory#make(long)} instead.
     */
    @InlineMe(replacement = "R064.make(capacity)")
@Deprecated
    public static DenseArray<Double> make( int capacity) {
        return R064.make(capacity);
    }

    /**
     * @deprecated v52 Use {@link #R064} and {@link MappedFileFactory#make(long)} instead.
     */
    @Deprecated
    public static BufferArray wrap( ByteBuffer data) {
        return new BufferR064(BufferArray.R064, data, null);
    }

    private final Buffer myBuffer;
    private final AutoCloseable myFile;

    BufferArray( Factory factory,  Buffer buffer,  AutoCloseable file) {

        super(factory, buffer.capacity());

        myBuffer = buffer;
        myFile = file;
    }

    @Override public void close() {
        if (myFile != null) {
            try {
                myFile.close();
            } catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        }
    }

    @Override
    public void reset() {
        this.fillAll(PrimitiveMath.ZERO);
        myBuffer.clear();
    }

    @Override
    protected final void add( int index,  double addend) {
        this.set(index, this.doubleValue(index) + addend);
    }

    @Override
    protected final void add( int index,  float addend) {
        this.set(index, this.floatValue(index) + addend);
    }

    @Override
    protected final void add( int index,  long addend) {
        this.set(index, this.longValue(index) + addend);
    }

    @Override
    protected final void add( int index,  int addend) {
        this.set(index, this.intValue(index) + addend);
    }

    @Override
    protected final void add( int index,  short addend) {
        this.set(index, this.shortValue(index) + addend);
    }

    @Override
    protected final void add( int index,  byte addend) {
        this.set(index, this.byteValue(index) + addend);
    }

    @Override
    protected void exchange( int firstA,  int firstB,  int step,  int count) {

        @Var int tmpIndexA = firstA;
        @Var int tmpIndexB = firstB;

        @Var double tmpVal;

        for (int i = 0; i < count; i++) {

            tmpVal = this.doubleValue(tmpIndexA);
            this.set(tmpIndexA, this.doubleValue(tmpIndexB));
            this.set(tmpIndexB, tmpVal);

            tmpIndexA += step;
            tmpIndexB += step;
        }
    }

    @Override
    protected void fill( int first,  int limit,  int step,  Double value) {
        FillAll.fill(this, first, limit, step, value);
    }

    @Override
    protected void fill( int first,  int limit,  int step,  NullaryFunction<?> supplier) {
        FillAll.fill(this, first, limit, step, supplier);
    }

    @Override
    protected void fillOne( int index,  Access1D<?> values,  long valueIndex) {
        this.set(index, values.doubleValue(valueIndex));
    }

    @Override
    protected void fillOne( int index,  Double value) {
        this.set(index, value);
    }

    @Override
    protected Double get( int index) {
        return Double.valueOf(this.doubleValue(index));
    }

    @Override
    protected int indexOfLargest( int first,  int limit,  int step) {
        return AMAX.invoke(this, first, limit, step);
    }

    @Override
    protected boolean isAbsolute( int index) {
        return PrimitiveScalar.isAbsolute(this.doubleValue(index));
    }

    @Override
    protected boolean isSmall( int index,  double comparedTo) {
        return PrimitiveScalar.isSmall(comparedTo, this.doubleValue(index));
    }

    @Override
    protected void modify( int first,  int limit,  int step,  Access1D<Double> left,  BinaryFunction<Double> function) {
        OperationBinary.invoke(this, first, limit, step, left, function, this);
    }

    @Override
    protected void modify( int first,  int limit,  int step,  BinaryFunction<Double> function,  Access1D<Double> right) {
        OperationBinary.invoke(this, first, limit, step, this, function, right);
    }

    @Override
    protected void modify( int first,  int limit,  int step,  UnaryFunction<Double> function) {
        OperationUnary.invoke(this, first, limit, step, this, function);
    }

    @Override
    protected void modifyOne( int index,  UnaryFunction<Double> modifier) {
        this.set(index, modifier.invoke(this.doubleValue(index)));
    }

    @Override
    protected int searchAscending( Double number) {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    protected void set( int index,  Comparable<?> value) {
        this.set(index, NumberDefinition.doubleValue(value));
    }

    @Override
    protected void sortAscending() {
        ProgrammingError.throwForUnsupportedOptionalOperation();
    }

    @Override
    protected void sortDescending() {
        ProgrammingError.throwForUnsupportedOptionalOperation();
    }

    @Override
    protected void visit( int first,  int limit,  int step,  VoidFunction<Double> visitor) {
        OperationVoid.invoke(this, first, limit, step, visitor);
    }

    @Override
    protected void visitOne( int index,  VoidFunction<Double> visitor) {
        visitor.invoke(this.doubleValue(index));
    }

    @Override
    void modify( long extIndex,  int intIndex,  Access1D<Double> left,  BinaryFunction<Double> function) {
        this.set(intIndex, function.invoke(left.doubleValue(extIndex), this.doubleValue(intIndex)));
    }

    @Override
    void modify( long extIndex,  int intIndex,  BinaryFunction<Double> function,  Access1D<Double> right) {
        this.set(intIndex, function.invoke(this.doubleValue(intIndex), right.doubleValue(extIndex)));
    }

    @Override
    void modify( long extIndex,  int intIndex,  UnaryFunction<Double> function) {
        this.set(intIndex, function.invoke(this.doubleValue(intIndex)));
    }

}
