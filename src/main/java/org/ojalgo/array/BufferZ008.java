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

import org.ojalgo.function.NullaryFunction;
import org.ojalgo.type.NumberDefinition;

final class BufferZ008 extends BufferArray {

    private final ByteBuffer myBuffer;

    BufferZ008( BufferArray.Factory factory,  ByteBuffer buffer,  AutoCloseable closeable) {
        super(factory, buffer, closeable);
        myBuffer = buffer;
    }

    @Override
    protected byte byteValue( int index) {
        return myBuffer.get(index);
    }

    @Override
    protected void fillOne( int index,  NullaryFunction<?> supplier) {
        myBuffer.put(index, supplier.byteValue());
    }

    @Override
    protected float floatValue( int index) {
        return myBuffer.get(index);
    }

    @Override
    protected void set( int index,  double value) {
        myBuffer.put(index, (byte) Math.round(value));
    }

    @Override
    protected void set( int index,  long value) {
        myBuffer.put(index, (byte) value);
    }

    @Override
    protected void set( int index,  byte value) {
        myBuffer.put(index, value);
    }

    @Override
    protected void add( int index,  Comparable<?> addend) {
        this.set(index, this.byteValue(index) + NumberDefinition.byteValue(addend));
    }
}
