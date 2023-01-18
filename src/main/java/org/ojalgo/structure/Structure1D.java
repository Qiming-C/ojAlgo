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
package org.ojalgo.structure;

import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;

/**
 * A (fixed size) 1-dimensional data structure.
 *
 * @author apete
 */
public interface Structure1D {

    class BasicMapper<T> implements IndexMapper<T> {

        private final List<T> myKeys = new ArrayList<>();

        BasicMapper() {
            super();
        }

        @Override public synchronized long toIndex( T key) {
            @Var long retVal = myKeys.indexOf(key);
            if (retVal < 0L) {
                retVal = this.indexForNewKey(key);
            }
            return retVal;
        }

        @Override public final T toKey( long index) {
            return myKeys.get(Math.toIntExact(index));
        }

        final long indexForNewKey( T newKey) {
             long retVal = myKeys.size();
            myKeys.add(newKey);
            return retVal;
        }

    }

    @FunctionalInterface
    public interface IndexCallback {

        /**
         * @param index Index
         */
        void call( long index);

    }

    public interface IndexMapper<T> {

        /**
         * This default implementation assumes that the index is incremented by 1 when incrementing the key to
         * the next value.
         *
         * @param key The value to increment
         * @return The next (incremented) value
         */
        default T next( T key) {
            return this.toKey(this.toIndex(key) + 1L);
        }

        /**
         * This default implementation assumes that the index is decremented by 1 when decrementing the key to
         * the previous value.
         *
         * @param key The value to decrement
         * @return The previous (decremented) value
         */
        default T previous( T key) {
            return this.toKey(this.toIndex(key) - 1L);
        }

        /**
         * For each key (any instance of that type) there is a corresponding index value – 1 or more key
         * instances will be mapped to each index value.
         */
        long toIndex(T key);

        /**
         * In most cases it should be safe to assume that the input index value is valid (matching what would
         * be created by {@link #toIndex(Object)}).
         */
        T toKey(long index);

    }

    public final class IntIndex implements Comparable<IntIndex> {

        public static IntIndex of( int index) {
            return new IntIndex(index);
        }

        public final int index;

        public IntIndex( int anIndex) {

            super();

            index = anIndex;
        }

        @SuppressWarnings("unused")
        private IntIndex() {
            this(-1);
        }

        @Override public int compareTo( IntIndex ref) {
            return Integer.compare(index, ref.index);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || !(obj instanceof IntIndex)) {
                return false;
            }
             var other = (IntIndex) obj;
            if (index != other.index) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(index);
        }

        @Override
        public String toString() {
            return Integer.toString(index);
        }

    }

    interface Logical<S extends Structure1D, B extends Logical<S, B>> extends Structure1D {

        B after(S after);

        B before(S before);

    }

    public final class LongIndex implements Comparable<LongIndex> {

        public static LongIndex of( long index) {
            return new LongIndex(index);
        }

        public final long index;

        public LongIndex( long anIndex) {

            super();

            index = anIndex;
        }

        @SuppressWarnings("unused")
        private LongIndex() {
            this(-1L);
        }

        @Override public int compareTo( LongIndex ref) {
            return Long.compare(index, ref.index);
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || !(obj instanceof LongIndex)) {
                return false;
            }
             var other = (LongIndex) obj;
            if (index != other.index) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(index);
        }

        @Override
        public String toString() {
            return Long.toString(index);
        }

    }

    @FunctionalInterface
    public interface LoopCallback {

        /**
         * for(long i = first; i < limit; i += step)
         *
         * @param first The initial value
         * @param limit The value limit
         * @param step The increment size
         */
        void call(long first, long limit, long step);

    }

    static int index( long index) {
        return Math.toIntExact(index);
    }

    static void loopMatching( Structure1D structureA,  Structure1D structureB,  IndexCallback callback) {
         long limit = Math.min(structureA.count(), structureB.count());
        Structure1D.loopRange(0L, limit, callback);
    }

    static void loopRange( long first,  long limit,  IndexCallback callback) {
        for (long i = first; i < limit; i++) {
            callback.call(i);
        }
    }

    /**
     *Returns a very simple implementation - you better come up with something else.
 
     */
    static <T> IndexMapper<T> mapper() {
        return new BasicMapper<>();
    }

    static int[] newDecreasingRange( int first,  int count) {
         int[] retVal = new int[count];
        for (int i = 0; i < count; i++) {
            retVal[i] = first - i;
        }
        return retVal;
    }

    static long[] newDecreasingRange( long first,  int count) {
         long[] retVal = new long[count];
        for (int i = 0; i < count; i++) {
            retVal[i] = first - i;
        }
        return retVal;
    }

    static int[] newIncreasingRange( int first,  int count) {
         int[] retVal = new int[count];
        for (int i = 0; i < count; i++) {
            retVal[i] = first + i;
        }
        return retVal;
    }

    static long[] newIncreasingRange( long first,  int count) {
         long[] retVal = new long[count];
        for (int i = 0; i < count; i++) {
            retVal[i] = first + i;
        }
        return retVal;
    }

    static long[] replaceNullOrEmptyWithFull( long[] suggested,  int fullSize) {
        if (suggested != null && suggested.length > 0) {
            return suggested;
        } else {
            return Structure1D.newIncreasingRange(0L, fullSize);
        }
    }

    static int[] toIntIndexes( long[] indexes) {
        int[] retVal = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            retVal[i] = Math.toIntExact(indexes[i]);
        }
        return retVal;
    }

    static long[] toLongIndexes( int[] indexes) {
        long[] retVal = new long[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            retVal[i] = indexes[i];
        }
        return retVal;
    }

    /**
     *Returns the total number of elements in this structure.
 
     */
    long count();

    default void loopAll( IndexCallback callback) {
        Structure1D.loopRange(0L, this.count(), callback);
    }

    default int size() {
        return Math.toIntExact(this.count());
    }

}
