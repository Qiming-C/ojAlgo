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
import java.util.Arrays;
import java.util.function.Predicate;
import org.ojalgo.ProgrammingError;
import org.ojalgo.function.aggregator.Aggregator;

/**
 * A (fixed size) any-dimensional data structure.
 *
 * @author apete
 */
public interface StructureAnyD extends Structure1D {

    public final class IntReference implements Comparable<IntReference> {

        public static IntReference of( int... aReference) {
            return new IntReference(aReference);
        }

        public final int[] reference;

        public IntReference( int... aReference) {

            super();

            reference = aReference;
        }

        @SuppressWarnings("unused")
        private IntReference() {
            this(-1);
        }

        @Override public int compareTo( IntReference ref) {

            @Var int retVal = reference.length - ref.reference.length;

            @Var int i = reference.length - 1;
            while (retVal == 0 && i >= 0) {
                retVal = reference[i] - ref.reference[i];
                i--;
            }

            return retVal;
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof IntReference)) {
                return false;
            }
             var other = (IntReference) obj;
            if (!Arrays.equals(reference, other.reference)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            int result = 1;
            return prime * result + Arrays.hashCode(reference);
        }

        @Override
        public String toString() {
            return Arrays.toString(reference);
        }

    }

    interface Logical<S extends StructureAnyD, B extends Logical<S, B>> extends StructureAnyD {

    }

    public final class LongReference implements Comparable<LongReference> {

        public static LongReference of( long... aReference) {
            return new LongReference(aReference);
        }

        public final long[] reference;

        public LongReference( long... aReference) {

            super();

            reference = aReference;
        }

        @SuppressWarnings("unused")
        private LongReference() {
            this(-1L);
        }

        @Override public int compareTo( LongReference ref) {

            @Var int retVal = Integer.compare(reference.length, ref.reference.length);

            @Var int i = reference.length - 1;
            while (retVal == 0 && i >= 0) {
                retVal = Long.compare(reference[i], ref.reference[i]);
                i--;
            }

            return retVal;
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof LongReference)) {
                return false;
            }
             var other = (LongReference) obj;
            if (!Arrays.equals(reference, other.reference)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
             int prime = 31;
            int result = 1;
            return prime * result + Arrays.hashCode(reference);
        }

        @Override
        public String toString() {
            return Arrays.toString(reference);
        }

    }

    public interface ReducibleTo1D<R extends Structure1D> extends StructureAnyD {

        /**
         *Returns a 1D data structure with aggregated values.
 @param dimension Which of the AnyD-dimensions should be mapped to the resulting 1D structure.
         * @param aggregator How to aggregate the values of the reduction
         * 
         */
        R reduce(int dimension, Aggregator aggregator);

    }

    public interface ReducibleTo2D<R extends Structure2D> extends StructureAnyD {

        /**
         *Returns a 2D data structure with aggregated values.
 @param rowDimension Which of the AnyD-dimensions should be mapped to the rows of the resulting 2D
         *        structure.
         * @param columnDimension Which of the AnyD-dimensions should be mapped to the columns of the
         *        resulting 2D structure.
         * @param aggregator How to aggregate the values of the reduction
         * 
         */
        R reduce(int rowDimension, int columnDimension, Aggregator aggregator);

    }

    @FunctionalInterface
    public interface ReferenceCallback {

        /**
         * @param ref Element reference (indices)
         */
        void call(long[] ref);

    }

    class ReferenceMapper implements IndexMapper<Object[]> {

        private final IndexMapper<Object>[] myMappers;
        private final long[] myStructure;

        protected ReferenceMapper( StructureAnyD structure,  IndexMapper<Object>[] mappers) {
            super();
            myMappers = mappers;
            myStructure = structure.shape();
        }

        public <T> long toIndex( int dim,  T key) {
            return myMappers[dim].toIndex(key);
        }

        @Override public long toIndex( Object[] keys) {

             long[] ref = new long[keys.length];

            for (int i = 0; i < ref.length; i++) {
                ref[i] = myMappers[i].toIndex(keys[i]);
            }

            return StructureAnyD.index(myStructure, ref);
        }

        @SuppressWarnings("unchecked")
        public <T> T toKey( int dim,  long index) {
            return (T) myMappers[dim].toKey(index);
        }

        @Override public Object[] toKey( long index) {

             long[] ref = StructureAnyD.reference(index, myStructure);

             Object[] retVal = new Object[ref.length];

            for (int i = 0; i < ref.length; i++) {
                retVal[i] = myMappers[i].toKey(ref[i]);

            }
            return retVal;
        }

        @SuppressWarnings("unchecked")
        public <T extends Comparable<? super T>> T toKey( long index,  int dim) {
             long[] ref = StructureAnyD.reference(index, myStructure);
            return (T) myMappers[dim].toKey(ref[dim]);
        }

    }

    public interface Reshapable extends StructureAnyD {

        /**
         * If necessary increase the rank to the specified number (without changing the total number of
         * components)
         */
        StructureAnyD expand(int rank);

        /**
         * Flattens this to a 1D structure. This operation is largely redundant in ojAlgo as anything AnyD is
         * also/simultaneously 1D.
         */
        Structure1D flatten();

        /**
         * The same array viewed/accessed with a different shape
         */
        StructureAnyD reshape(long... shape);

        /**
         * Squeezing removes the dimensions or axes that have a length of one. (This does not change the total
         * number of components.)
         */
        StructureAnyD squeeze();

    }

    /**
     *Returns the size of an access with that structure.
 @param structure An access structure
     * 
     */
    static int count( int[] structure) {
        @Var int retVal = 1;
         int tmpLength = structure.length;
        for (int i = 0; i < tmpLength; i++) {
            retVal *= structure[i];
        }
        return retVal;
    }

    /**
     *Returns the size of that dimension.
 @param structure An access structure
     * @param dimension A dimension index
     * 
     */
    static int count( int[] structure,  int dimension) {
        return structure.length > dimension ? structure[dimension] : 1;
    }

    /**
     *Returns the size of an access with that structure.
 @param structure An access structure
     * 
     */
    static long count( long[] structure) {
        @Var long retVal = 1;
         int tmpLength = structure.length;
        for (int i = 0; i < tmpLength; i++) {
            retVal *= structure[i];
        }
        return retVal;
    }

    /**
     *Returns the size of that dimension.
 @param structure An access structure
     * @param dimension A dimension index
     * 
     */
    static long count( long[] structure,  int dimension) {
        return structure.length > dimension ? structure[dimension] : 1;
    }

    /**
     *Returns the index of that element.
 @param structure An access structure
     * @param reference An access element reference
     * 
     */
    static int index( int[] structure,  int[] reference) {
        @Var int retVal = reference[0];
        @Var int tmpFactor = structure[0];
         int tmpLength = reference.length;
        for (int i = 1; i < tmpLength; i++) {
            retVal += tmpFactor * reference[i];
            tmpFactor *= structure[i];
        }
        return retVal;
    }

    /**
     *Returns the index of that element.
 @param structure An access structure
     * @param reference An access element reference
     * 
     */
    static long index( long[] structure,  long[] reference) {
        @Var long retVal = reference[0];
        @Var long tmpFactor = structure[0];
         int tmpLength = Math.min(structure.length, reference.length);
        for (int i = 1; i < tmpLength; i++) {
            retVal += tmpFactor * reference[i];
            tmpFactor *= structure[i];
        }
        return retVal;
    }

    static void loopMatching( StructureAnyD structureA,  StructureAnyD structureB,  IndexCallback callback) {
        if (!Arrays.equals(structureA.shape(), structureB.shape())) {
            throw new ProgrammingError("The 2 structures must have the same shape!");
        }
        Structure1D.loopMatching(structureA, structureB, callback);
    }

    static StructureAnyD.ReferenceMapper mapperOf( StructureAnyD structure,  Structure1D.IndexMapper<Object>[] mappers) {
        return new StructureAnyD.ReferenceMapper(structure, mappers);
    }

    static long[] reference( long index,  long[] structure) {

        long[] retVal = new long[structure.length];

        StructureAnyD.reference(index, structure, retVal);

        return retVal;
    }

    /**
     * Based on the input index and structure/shape the reference array will derived.
     *
     * @param index Input index
     * @param structure Relevant structure/shape
     * @param reference Will be updated to the correct reference array given the index and structure
     */
    static void reference( long index,  long[] structure,  long[] reference) {

        @Var long tmpPrev = 1L;
        @Var long tmpNext = 1L;

        for (int s = 0; s < structure.length; s++) {
            tmpNext *= structure[s];
            reference[s] = index % tmpNext / tmpPrev;
            tmpPrev = tmpNext;
        }
    }

    static long[] shape( StructureAnyD structure) {

         long tmpSize = structure.count();

        @Var long tmpTotal = structure.count(0);
        @Var int tmpRank = 1;

        while (tmpTotal < tmpSize) {
            tmpTotal *= structure.count(tmpRank);
            tmpRank++;
        }

         long[] retVal = new long[tmpRank];

        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = structure.count(i);
        }

        return retVal;
    }

    /**
     *Returns the step size (index change) in that direction.
 @param structure An access structure
     * @param dimension A dimension index indication a direction
     * 
     */
    static int step( int[] structure,  int dimension) {
        @Var int retVal = 1;
        for (int i = 0; i < dimension; i++) {
            retVal *= StructureAnyD.count(structure, i);
        }
        return retVal;
    }

    /**
     * A more complex/general version of {@linkplain #step(int[], int)}.
     *
     * @param structure An access structure
     * @param increment A vector indication a direction (and size)
     * @return The step size (index change)
     */
    static int step( int[] structure,  int[] increment) {
        @Var int retVal = 0;
        @Var int tmpFactor = 1;
         int tmpLimit = increment.length;
        for (int i = 1; i < tmpLimit; i++) {
            retVal += tmpFactor * increment[i];
            tmpFactor *= structure[i];
        }
        return retVal;
    }

    /**
     * How does the index change when stepping to the next dimensional unit (next row, next column. next
     * matrix/area, next cube...)
     *
     * @param structure An access structure
     * @param dimension Which reference index to increment
     * @return The step size (index change)
     */
    static long step( long[] structure,  int dimension) {
        @Var long retVal = 1;
        for (int i = 0; i < dimension; i++) {
            retVal *= StructureAnyD.count(structure, i);
        }
        return retVal;
    }

    /**
     * A more complex/general version of {@linkplain #step(int[], int)}.
     *
     * @param structure An access structure
     * @param increment A vector indication a direction (and size)
     * @return The step size (index change)
     */
    static long step( long[] structure,  long[] increment) {

        @Var long retVal = 0L;
        @Var long factor = 1L;

        for (int i = 1, limit = increment.length; i < limit; i++) {
            retVal += factor * increment[i];
            factor *= structure[i];
        }

        return retVal;
    }

    /**
     * count() == count(0) * count(1) * count(2) * count(3) * ...
     */
    @Override default long count() {
        return StructureAnyD.count(this.shape());
    }

    long count(int dimension);

    /**
     * Will loop through this multidimensional data structure so that one index value of one dimension is
     * fixed. (Ex: Loop through all items with row index == 5.)
     *
     * @param dimension The dimension with a fixed/supplied index. (0==row, 1==column, 2=matrix/area...)
     * @param dimensionalIndex The index value that dimension is fixed to. (Which row, column or matrix/area)
     * @param callback A callback with parameters that define a sub-loop
     */
    default void loop( int dimension,  long dimensionalIndex,  LoopCallback callback) {

         long[] structure = this.shape();

        @Var long innerCount = 1L;
        @Var long dimenCount = 1L;
        @Var long outerCount = 1L;
        for (int i = 0; i < structure.length; i++) {
            if (i < dimension) {
                innerCount *= structure[i];
            } else if (i > dimension) {
                outerCount *= structure[i];
            } else {
                dimenCount = structure[i];
            }
        }
         long totalCount = innerCount * dimenCount * outerCount;

        if (innerCount == 1L) {
            callback.call(dimensionalIndex * innerCount, totalCount, dimenCount);
        } else {
             long step = innerCount * dimenCount;
            for (long i = dimensionalIndex * innerCount; i < totalCount; i += step) {
                callback.call(i, innerCount + i, 1L);
            }
        }

    }

    default void loop( long[] initial,  int dimension,  LoopCallback callback) {

        long[] structure = this.shape();

         long remaining = StructureAnyD.count(structure, dimension) - initial[dimension];

         long first = StructureAnyD.index(structure, initial);
         long step = StructureAnyD.step(structure, dimension);
         long limit = first + step * remaining;

        callback.call(first, limit, step);
    }

    default void loop( Predicate<long[]> filter,  IndexCallback callback) {
         long[] structure = this.shape();
        for (long i = 0L, limit = this.count(); i < limit; i++) {
             long[] reference = StructureAnyD.reference(i, structure);
            if (filter.test(reference)) {
                callback.call(i);
            }
        }
    }

    default void loopAllReferences( ReferenceCallback callback) {

        long[] shape = this.shape();

        long totalCount = this.count();
        long firstCount = this.count(0);
        long repetitionsCount = totalCount / firstCount;

        for (long r = 0L; r < repetitionsCount; r++) {
            long[] reference = StructureAnyD.reference(r * firstCount, shape);
            for (long i = 0L; i < firstCount; i++) {
                callback.call(reference);
                reference[0]++;
            }
        }
    }

    /**
     *Returns the number of dimensions (the number of indices used to reference one element).
 
     */
    default int rank() {
        return this.shape().length;
    }

    long[] shape();

    default int size( int dimension) {
        return Math.toIntExact(this.count(dimension));
    }

}
