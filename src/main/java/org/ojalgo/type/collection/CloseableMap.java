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
package org.ojalgo.type.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CloseableMap<K, V extends AutoCloseable> implements Map<K, V>, AutoCloseable {

    public static <K, V extends AutoCloseable> CloseableMap<K, V> newInstance() {
        return new CloseableMap<>(new HashMap<>());
    }

    public static <K, V extends AutoCloseable> CloseableMap<K, V> newInstance( int capacity) {
        return new CloseableMap<>(new HashMap<>(capacity));
    }

    public static <K, V extends AutoCloseable> CloseableMap<K, V> wrap( Map<K, V> delegate) {
        return new CloseableMap<>(delegate);
    }

    private final Map<K, V> myDelegate;

    CloseableMap( Map<K, V> delegate) {
        super();
        myDelegate = delegate;
    }

    @Override
    public void clear() {
        myDelegate.clear();
    }

    @Override public void close() throws Exception {
        myDelegate.values().forEach(e -> {
            try {
                if (e != null) {
                    e.close();
                }
            } catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        });
    }

    @Override public V compute( K key,  BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return myDelegate.compute(key, remappingFunction);
    }

    @Override public V computeIfAbsent( K key,  Function<? super K, ? extends V> mappingFunction) {
        return myDelegate.computeIfAbsent(key, mappingFunction);
    }

    @Override public V computeIfPresent( K key,  BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return myDelegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public boolean containsKey( Object key) {
        return myDelegate.containsKey(key);
    }

    @Override
    public boolean containsValue( Object value) {
        return myDelegate.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return myDelegate.entrySet();
    }

    @Override
    public boolean equals( Object o) {
        return myDelegate.equals(o);
    }

    @Override public void forEach( BiConsumer<? super K, ? super V> action) {
        myDelegate.forEach(action);
    }

    @Override
    public V get( Object key) {
        return myDelegate.get(key);
    }

    @Override public V getOrDefault( Object key,  V defaultValue) {
        return myDelegate.getOrDefault(key, defaultValue);
    }

    @Override
    public int hashCode() {
        return myDelegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return myDelegate.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return myDelegate.keySet();
    }

    @Override public V merge( K key,  V value,  BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return myDelegate.merge(key, value, remappingFunction);
    }

    @Override
    public V put( K key,  V value) {
        return myDelegate.put(key, value);
    }

    @Override
    public void putAll( Map<? extends K, ? extends V> m) {
        myDelegate.putAll(m);
    }

    @Override public V putIfAbsent( K key,  V value) {
        return myDelegate.putIfAbsent(key, value);
    }

    @Override
    public V remove( Object key) {
        return myDelegate.remove(key);
    }

    @Override public boolean remove( Object key,  Object value) {
        return myDelegate.remove(key, value);
    }

    @Override public V replace( K key,  V value) {
        return myDelegate.replace(key, value);
    }

    @Override public boolean replace( K key,  V oldValue,  V newValue) {
        return myDelegate.replace(key, oldValue, newValue);
    }

    @Override public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function) {
        myDelegate.replaceAll(function);
    }

    @Override
    public int size() {
        return myDelegate.size();
    }

    @Override
    public Collection<V> values() {
        return myDelegate.values();
    }

}
