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
package org.ojalgo.netio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ojalgo.array.ArrayAnyD;
import org.ojalgo.array.DenseArray;
import org.ojalgo.type.function.OperatorWithException;
import org.ojalgo.type.keyvalue.EntryPair;
import org.ojalgo.type.keyvalue.KeyValue;

public interface DataInterpreter<T> extends DataReader.Deserializer<T>, DataWriter.Serializer<T> {

    DataInterpreter<String> STRING = new DataInterpreter<>() {

        @Override public String deserialize( DataInput input) throws IOException {
            return input.readUTF();
        }

        @Override public void serialize( String data,  DataOutput output) throws IOException {
            output.writeUTF(data);
        }

    };

    static <N extends Comparable<N>> DataInterpreter<ArrayAnyD<N>> newIDX( DenseArray.Factory<N> denseArray) {

        ArrayAnyD.Factory<N> factory = ArrayAnyD.factory(denseArray);

        return new DataInterpreter<>() {

            @Override public ArrayAnyD<N> deserialize( DataInput input) throws IOException {

                input.readByte();
                input.readByte();
                int type = input.readByte();
                int rank = input.readByte();

                long[] structure = new long[rank];
                for (int i = 0; i < rank; i++) {
                    structure[rank - 1 - i] = input.readInt();
                }

                ArrayAnyD<N> data = factory.make(structure);

                for (long i = 0, limit = data.count(); i < limit; i++) {
                    switch (type) {
                    case 0x08:
                        data.set(i, input.readUnsignedByte());
                        break;
                    case 0x09:
                        data.set(i, input.readByte());
                        break;
                    case 0x0B:
                        data.set(i, input.readShort());
                        break;
                    case 0x0C:
                        data.set(i, input.readInt());
                        break;
                    case 0x0D:
                        data.set(i, input.readFloat());
                        break;
                    case 0x0E:
                        data.set(i, input.readDouble());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown element type!");
                    }
                }

                return data;
            }

            @Override public void serialize( ArrayAnyD<N> data,  DataOutput output) throws IOException {

                int rank = data.rank();

                output.writeByte(0);
                output.writeByte(0);

                output.writeByte(0x0D); // Hard code to float for now
                output.writeByte(rank);

                for (int i = 0; i < rank; i++) {
                    output.writeInt(data.size(i));
                }

                for (long i = 0, limit = data.count(); i < limit; i++) {
                    output.writeFloat(data.floatValue(i));
                }
            }

        };
    }

    static <T> DataInterpreter<EntryPair.KeyedPrimitive<KeyValue.Dual<T>>> newScoredDual( DataInterpreter<T> keyInterpreter) {

        return new DataInterpreter<>() {

            @Override public EntryPair.KeyedPrimitive<KeyValue.Dual<T>> deserialize( DataInput input) throws IOException {
                return EntryPair.of(keyInterpreter.deserialize(input), keyInterpreter.deserialize(input), input.readFloat());
            }

            @Override public void serialize( EntryPair.KeyedPrimitive<KeyValue.Dual<T>> data,  DataOutput output) throws IOException {
                KeyValue.Dual<T> key = data.getKey();
                keyInterpreter.serialize(key.first, output);
                keyInterpreter.serialize(key.second, output);
                output.writeFloat(data.floatValue());
            }

        };
    }

    default DataReader<T> newReader( File file) {
        return DataReader.of(file, this);
    }

    default DataReader<T> newReader( File file,  OperatorWithException<InputStream> filter) {
        return DataReader.of(file, this, filter);
    }

    default DataWriter<T> newWriter( File file) {
        return DataWriter.of(file, this);
    }

    default DataWriter<T> newWriter( File file,  OperatorWithException<OutputStream> filter) {
        return DataWriter.of(file, this, filter);
    }

}
