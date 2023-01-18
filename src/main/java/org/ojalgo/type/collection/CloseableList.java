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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class CloseableList<T extends AutoCloseable> implements List<T>, AutoCloseable {

    public static <T extends AutoCloseable> CloseableList<T> newInstance() {
        return new CloseableList<>(new ArrayList<>());
    }

    public static <T extends AutoCloseable> CloseableList<T> newInstance( int capacity) {
        return new CloseableList<>(new ArrayList<>(capacity));
    }

    public static <T extends AutoCloseable> CloseableList<T> wrap( List<T> delegate) {
        return new CloseableList<>(delegate);
    }

    private final List<T> myDelegate;

    CloseableList( List<T> delegate) {
        super();
        myDelegate = delegate;
    }

    @Override public void add( int index,  T element) {
        myDelegate.add(index, element);
    }

    @Override
    public boolean add( T element) {
        return myDelegate.add(element);
    }

    @Override public boolean addAll( Collection<? extends T> c) {
        return myDelegate.addAll(c);
    }

    @Override public boolean addAll( int index,  Collection<? extends T> c) {
        return myDelegate.addAll(index, c);
    }

    @Override public void clear() {
        myDelegate.clear();
    }

    @Override public void close() {
        myDelegate.forEach(e -> {
            try {
                if (e != null) {
                    e.close();
                }
            } catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        });
    }

    @Override public boolean contains( Object o) {
        return myDelegate.contains(o);
    }

    @Override public boolean containsAll( Collection<?> c) {
        return myDelegate.containsAll(c);
    }

    @Override
    public boolean equals( Object o) {
        return myDelegate.equals(o);
    }

    @Override public void forEach( Consumer<? super T> action) {
        myDelegate.forEach(action);
    }

    @Override
    public T get( int index) {
        return myDelegate.get(index);
    }

    @Override
    public int hashCode() {
        return myDelegate.hashCode();
    }

    @Override public int indexOf( Object o) {
        return myDelegate.indexOf(o);
    }

    @Override public boolean isEmpty() {
        return myDelegate.isEmpty();
    }

    @Override public Iterator<T> iterator() {
        return myDelegate.iterator();
    }

    @Override public int lastIndexOf( Object o) {
        return myDelegate.lastIndexOf(o);
    }

    @Override public ListIterator<T> listIterator() {
        return myDelegate.listIterator();
    }

    @Override public ListIterator<T> listIterator( int index) {
        return myDelegate.listIterator(index);
    }

    @Override public Stream<T> parallelStream() {
        return myDelegate.parallelStream();
    }

    @Override public T remove( int index) {
        return myDelegate.remove(index);
    }

    @Override public boolean remove( Object o) {
        return myDelegate.remove(o);
    }

    @Override public boolean removeAll( Collection<?> c) {
        return myDelegate.removeAll(c);
    }

    @Override public boolean removeIf( Predicate<? super T> filter) {
        return myDelegate.removeIf(filter);
    }

    @Override public void replaceAll( UnaryOperator<T> operator) {
        myDelegate.replaceAll(operator);
    }

    @Override public boolean retainAll( Collection<?> c) {
        return myDelegate.retainAll(c);
    }

    @Override public T set( int index,  T element) {
        return myDelegate.set(index, element);
    }

    @Override
    public int size() {
        return myDelegate.size();
    }

    @Override public void sort( Comparator<? super T> c) {
        myDelegate.sort(c);
    }

    @Override public Spliterator<T> spliterator() {
        return myDelegate.spliterator();
    }

    @Override public Stream<T> stream() {
        return myDelegate.stream();
    }

    @Override public List<T> subList( int fromIndex,  int toIndex) {
        return myDelegate.subList(fromIndex, toIndex);
    }

    @Override public Object[] toArray() {
        return myDelegate.toArray();
    }

    @Override public <E> E[] toArray( E[] a) {
        return myDelegate.toArray(a);
    }

}
