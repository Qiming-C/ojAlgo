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
package org.ojalgo.series;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.ojalgo.netio.ASCII;
import org.ojalgo.type.ColourData;
import org.ojalgo.type.TypeUtils;

abstract class TreeSeries<K extends Comparable<? super K>, V extends Comparable<V>, I extends TreeSeries<K, V, I>>
        implements NavigableMap<K, V>, BasicSeries<K, V> {

    private ColourData myColour = null;
    private final NavigableMap<K, V> myDelegate;
    private String myName = null;

    protected TreeSeries( NavigableMap<K, V> delegate) {
        super();
        myDelegate = delegate;
    }

    @Override public Entry<K, V> ceilingEntry( K key) {
        return myDelegate.ceilingEntry(key);
    }

    @Override public K ceilingKey( K key) {
        return myDelegate.ceilingKey(key);
    }

    @Override public void clear() {
        myDelegate.clear();
    }

    @Override public final I colour( ColourData colour) {
        return (I) BasicSeries.super.colour(colour);
    }

    @Override public Comparator<? super K> comparator() {
        return myDelegate.comparator();
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

    @Override public boolean containsKey( Object key) {
        return myDelegate.containsKey(key);
    }

    @Override public boolean containsValue( Object value) {
        return myDelegate.containsValue(value);
    }

    @Override public NavigableSet<K> descendingKeySet() {
        return myDelegate.descendingKeySet();
    }

    @Override public NavigableMap<K, V> descendingMap() {
        return myDelegate.descendingMap();
    }

    @Override public Set<Entry<K, V>> entrySet() {
        return myDelegate.entrySet();
    }

    @Override
    public boolean equals( Object o) {
        return myDelegate.equals(o);
    }

    @Override public Entry<K, V> firstEntry() {
        return myDelegate.firstEntry();
    }

    @Override public K firstKey() {
        return myDelegate.firstKey();
    }

    @Override public Entry<K, V> floorEntry( K key) {
        return myDelegate.floorEntry(key);
    }

    @Override public K floorKey( K key) {
        return myDelegate.floorKey(key);
    }

    @Override public void forEach( BiConsumer<? super K, ? super V> action) {
        myDelegate.forEach(action);
    }

    @Override public V get( K key) {
        return myDelegate.get(key);
    }

    @Override public V get( Object key) {
        return myDelegate.get(key);
    }

    @Override public ColourData getColour() {
        return myColour;
    }

    @Override public String getName() {
        return myName;
    }

    @Override public V getOrDefault( Object key,  V defaultValue) {
        return myDelegate.getOrDefault(key, defaultValue);
    }

    @Override
    public int hashCode() {
        return myDelegate.hashCode();
    }

    @Override public SortedMap<K, V> headMap( K toKey) {
        return myDelegate.headMap(toKey);
    }

    @Override public NavigableMap<K, V> headMap( K toKey,  boolean inclusive) {
        return myDelegate.headMap(toKey, inclusive);
    }

    @Override public Entry<K, V> higherEntry( K key) {
        return myDelegate.higherEntry(key);
    }

    @Override public K higherKey( K key) {
        return myDelegate.higherKey(key);
    }

    @Override public boolean isEmpty() {
        return myDelegate.isEmpty();
    }

    @Override public Set<K> keySet() {
        return myDelegate.keySet();
    }

    @Override public Entry<K, V> lastEntry() {
        return myDelegate.lastEntry();
    }

    @Override public K lastKey() {
        return myDelegate.lastKey();
    }

    @Override public Entry<K, V> lowerEntry( K key) {
        return myDelegate.lowerEntry(key);
    }

    @Override public K lowerKey( K key) {
        return myDelegate.lowerKey(key);
    }

    @Override public V merge( K key,  V value,  BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return myDelegate.merge(key, value, remappingFunction);
    }

    @Override public final I name( String name) {
        return (I) BasicSeries.super.name(name);
    }

    @Override public NavigableSet<K> navigableKeySet() {
        return myDelegate.navigableKeySet();
    }

    @Override public Entry<K, V> pollFirstEntry() {
        return myDelegate.pollFirstEntry();
    }

    @Override public Entry<K, V> pollLastEntry() {
        return myDelegate.pollLastEntry();
    }

    @Override public V put( K key,  V value) {
        return myDelegate.put(key, value);
    }

    @Override public void putAll( Map<? extends K, ? extends V> m) {
        myDelegate.putAll(m);
    }

    @Override public V putIfAbsent( K key,  V value) {
        return myDelegate.putIfAbsent(key, value);
    }

    @Override public V remove( Object key) {
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

    @Override public void setColour( ColourData colour) {
        myColour = colour;
    }

    @Override public void setName( String name) {
        myName = name;
    }

    @Override public int size() {
        return myDelegate.size();
    }

    @Override public NavigableMap<K, V> subMap( K fromKey,  boolean fromInclusive,  K toKey,  boolean toInclusive) {
        return myDelegate.subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    @Override public SortedMap<K, V> subMap( K fromKey,  K toKey) {
        return myDelegate.subMap(fromKey, toKey);
    }

    @Override public SortedMap<K, V> tailMap( K fromKey) {
        return myDelegate.tailMap(fromKey);
    }

    @Override public NavigableMap<K, V> tailMap( K fromKey,  boolean inclusive) {
        return myDelegate.tailMap(fromKey, inclusive);
    }

    @Override
    public String toString() {

        StringBuilder retVal = this.toStringFirstPart();

        this.appendLastPartToString(retVal);

        return retVal.toString();
    }

    @Override public Collection<V> values() {
        return myDelegate.values();
    }

    final void appendLastPartToString( StringBuilder builder) {

        if (myColour != null) {
            builder.append(TypeUtils.toHexString(myColour.getRGB()));
            builder.append(ASCII.NBSP);
        }

        if (this.size() <= 30) {
            builder.append(myDelegate.toString());
        } else {
            builder.append("First:");
            builder.append(this.firstKey());
            builder.append("=");
            builder.append(this.firstValue());
            builder.append(ASCII.NBSP);
            builder.append("Last:");
            builder.append(this.lastKey());
            builder.append("=");
            builder.append(this.lastValue());
            builder.append(ASCII.NBSP);
            builder.append("Size:");
            builder.append(this.size());
        }
    }

    final StringBuilder toStringFirstPart() {

         var retVal = new StringBuilder();

        if (myName != null) {
            retVal.append(myName);
            retVal.append(ASCII.NBSP);
        }

        return retVal;
    }

}
