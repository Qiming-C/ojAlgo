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

import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.NativeMemory;

final class OffHeapZ008 extends OffHeapArray {

    private final long myPointer;

    OffHeapZ008( long count) {

        super(OffHeapArray.Z008, count);

        myPointer = NativeMemory.allocateByteArray(this, count);
    }

    @Override public void add( long index,  Comparable<?> addend) {
        this.add(index, Scalar.byteValue(addend));
    }

    @Override public byte byteValue( long index) {
        return NativeMemory.getByte(myPointer, index);
    }

    @Override public double doubleValue( long index) {
        return NativeMemory.getByte(myPointer, index);
    }

    @Override
    public void reset() {
        NativeMemory.initialiseByteArray(myPointer, this.count());
    }

    @Override public void set( long index,  byte value) {
        NativeMemory.setByte(myPointer, index, value);
    }

    @Override public void set( long index,  Comparable<?> value) {
        this.set(index, Scalar.byteValue(value));
    }

    @Override public void set( long index,  double value) {
        NativeMemory.setByte(myPointer, index, (byte) Math.toIntExact(Math.round(value)));
    }

    @Override public void set( long index,  float value) {
        NativeMemory.setByte(myPointer, index, (byte) Math.round(value));
    }

}
