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
package org.ojalgo.type.keyvalue;

import com.google.errorprone.annotations.Var;
import java.util.Map;
import org.ojalgo.type.PrimitiveNumber;

/**
 * A pair, like {@link Map.Entry} without {@link Map.Entry#setValue(Object)}.
 *
 * @author apete
 */
public interface KeyValue<K, V> {

    /**
     * A pair of the same type.
     *
     * @author apete
     */
    final class Dual<T> implements KeyValue<T, T> {

        public final T first;
        public final T second;

        Dual( T obj1,  T obj2) {

            super();

            first = obj1;
            second = obj2;
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Dual)) {
                return false;
            }
            var other = (Dual<?>) obj;
            if (first == null) {
                if (other.first != null) {
                    return false;
                }
            } else if (!first.equals(other.first)) {
                return false;
            }
            if (second == null) {
                if (other.second != null) {
                    return false;
                }
            } else if (!second.equals(other.second)) {
                return false;
            }
            return true;
        }

        @Override public T getKey() {
            return first;
        }

        @Override public T getValue() {
            return second;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            @Var int result = 1;
            result = prime * result + ((first == null) ? 0 : first.hashCode());
            return prime * result + ((second == null) ? 0 : second.hashCode());
        }

    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  byte value) {
        return EntryPair.of(key, value);
    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  double value) {
        return EntryPair.of(key, value);
    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  float value) {
        return EntryPair.of(key, value);
    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  int value) {
        return EntryPair.of(key, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  byte value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  double value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  float value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  int value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  long value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<KeyValue.Dual<K>, PrimitiveNumber> of( K key1,  K key2,  short value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K, V> EntryPair<KeyValue.Dual<K>, V> of( K key1,  K key2,  V value) {
        return EntryPair.of(key1, key2, value);
    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  long value) {
        return EntryPair.of(key, value);
    }

    static <K> KeyValue<K, PrimitiveNumber> of( K key,  short value) {
        return EntryPair.of(key, value);
    }

    static <K, V> KeyValue<K, V> of( K key,  V value) {
        return EntryPair.of(key, value);
    }

    K getKey();

    V getValue();

}
