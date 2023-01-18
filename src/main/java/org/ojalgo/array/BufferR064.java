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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.ojalgo.function.NullaryFunction;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;

final class BufferR064 extends BufferArray {

    private final DoubleBuffer myBuffer;

    BufferR064( BufferArray.Factory factory,  ByteBuffer buffer,  AutoCloseable closeable) {
        this(factory, buffer.asDoubleBuffer(), closeable);
    }

    BufferR064( BufferArray.Factory factory,  DoubleBuffer buffer,  AutoCloseable closeable) {
        super(factory, buffer, closeable);
        myBuffer = buffer;
    }

    @Override
    public void supplyTo( Mutate1D receiver) {
        int limit = Math.min(this.size(), receiver.size());
        for (int i = 0; i < limit; i++) {
            receiver.set(i, this.doubleValue(i));
        }
    }

    @Override
    protected byte byteValue( int index) {
        return (byte) Math.round(myBuffer.get(index));
    }

    @Override
    protected double doubleValue( int index) {
        return myBuffer.get(index);
    }

    @Override
    protected void fillOne( int index,  NullaryFunction<?> supplier) {
        myBuffer.put(index, supplier.doubleValue());
    }

    @Override
    protected float floatValue( int index) {
        return (float) myBuffer.get(index);
    }

    @Override
    protected int intValue( int index) {
        return (int) Math.round(myBuffer.get(index));
    }

    @Override
    protected long longValue( int index) {
        return Math.round(myBuffer.get(index));
    }

    @Override
    protected void set( int index,  double value) {
        myBuffer.put(index, value);
    }

    @Override
    protected void set( int index,  long value) {
        myBuffer.put(index, (double) value);
    }

    @Override
    protected void add( int index,  Comparable<?> addend) {
        this.set(index, this.doubleValue(index) + NumberDefinition.doubleValue(addend));
    }
}
