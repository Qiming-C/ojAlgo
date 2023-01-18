package org.ojalgo.array;

import com.google.errorprone.annotations.Var;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.ojalgo.ProgrammingError;
import org.ojalgo.array.SparseArray.NonzeroView;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.structure.Access1D;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.type.NumberDefinition;
import org.ojalgo.type.context.NumberContext;

/**
 * A {@link SortedMap} with primitive valued long keys and {@link Comparable} values (incl. possibly primitive
 * double values). The main benefits of using this class is its use of primitive keys and values, and how it
 * integrates with other parts of ojAlgo. As a general purpose {@link Map} implementation (usage with high
 * frequency of randomly ordered put and remove operations) it is not very efficient.
 *
 * @author apete
 */
public final class LongToNumberMap<N extends Comparable<N>> implements SortedMap<Long, N>, Access1D<N>, Mutate1D.Mixable<N> {

    public static final class MapFactory<N extends Comparable<N>> extends StrategyBuildingFactory<N, LongToNumberMap<N>, MapFactory<N>> {

        MapFactory( DenseArray.Factory<N> denseFactory) {
            super(denseFactory);
        }

        @Override
        public LongToNumberMap<N> make() {
            return new LongToNumberMap<>(this.getStrategy());
        }

    }

    public static <N extends Comparable<N>> MapFactory<N> factory( DenseArray.Factory<N> denseFactory) {
        return new MapFactory<>(denseFactory);
    }

    private final SparseArray<N> myStorage;
    private final DenseCapacityStrategy<N> myStrategy;

    LongToNumberMap( DenseCapacityStrategy<N> strategy) {

        super();

        myStrategy = strategy.limit(Long.MAX_VALUE);

        myStorage = new SparseArray<>(myStrategy);
    }

    /**
     * The current capacity of the underlying data structure. The capacity is always greater than or equal to
     * the current number of entries in the map. When you add entries to the map the capacity may have to
     * grow.
     */
    public long capacity() {
        return myStorage.capacity();
    }

    @Override public void clear() {
        myStorage.reset();
    }

    @Override public Comparator<? super Long> comparator() {
        return null;
    }

    public boolean containsKey( long key) {
        return myStorage.index(key) >= 0;
    }

    @Override public boolean containsKey( Object key) {
        if (key instanceof Comparable) {
            return this.containsKey(NumberDefinition.longValue((Comparable<?>) key));
        } else {
            return false;
        }
    }

    public boolean containsValue( double value) {
        for ( NonzeroView<N> tmpView : myStorage.nonzeros()) {
            // if (tmpView.doubleValue() == value) {
            if (NumberContext.compare(tmpView.doubleValue(), value) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override public boolean containsValue( Object value) {
        for ( NonzeroView<N> tmpView : myStorage.nonzeros()) {
            if (value.equals(tmpView.get())) {
                return true;
            }
        }
        return false;
    }

    @Override public long count() {
        return myStorage.getActualLength();
    }

    @Override public double doubleValue( long key) {
         int tmpIndex = myStorage.index(key);
        if (tmpIndex >= 0) {
            return myStorage.doubleValueInternally(tmpIndex);
        } else {
            return PrimitiveMath.NaN;
        }
    }

    @Override public Set<Map.Entry<Long, N>> entrySet() {
        return new AbstractSet<Map.Entry<Long, N>>() {

            @Override
            public Iterator<Map.Entry<Long, N>> iterator() {
                return new Iterator<Map.Entry<Long, N>>() {

                    NonzeroView<N> tmpNonzeros = myStorage.nonzeros();

                    @Override public boolean hasNext() {
                        return tmpNonzeros.hasNext();
                    }

                    @Override public Map.Entry<Long, N> next() {

                        tmpNonzeros.next();

                        return new Map.Entry<Long, N>() {

                            @Override public Long getKey() {
                                return tmpNonzeros.index();
                            }

                            @Override public N getValue() {
                                return tmpNonzeros.get();
                            }

                            @Override public N setValue( N value) {
                                ProgrammingError.throwForUnsupportedOptionalOperation();
                                return null;
                            }

                        };
                    }

                };
            }

            @Override
            public int size() {
                return myStorage.getActualLength();
            }
        };
    }

    @Override public Long firstKey() {
        return myStorage.firstIndex();
    }

    @Override public N get( long key) {
         int tmpIndex = myStorage.index(key);
        if (tmpIndex >= 0) {
            return myStorage.getInternally(tmpIndex);
        } else {
            return null;
        }
    }

    @Override public N get( Object key) {
        return key instanceof Comparable ? this.get(NumberDefinition.longValue((Comparable<?>) key)) : null;
    }

    public LongToNumberMap<N> headMap( long toKey) {
        return this.subMap(myStorage.firstIndex(), toKey);
    }

    @Override public LongToNumberMap<N> headMap( Long toKey) {
        return this.headMap(toKey.longValue());
    }

    @Override public boolean isEmpty() {
        return myStorage.getActualLength() == 0;
    }

    @Override public Set<Long> keySet() {
        return new AbstractSet<Long>() {

            @Override
            public Iterator<Long> iterator() {
                return myStorage.indices().iterator();
            }

            @Override
            public int size() {
                return myStorage.getActualLength();
            }

        };
    }

    @Override public Long lastKey() {
        return myStorage.lastIndex();
    }

    @Override public double mix( long key,  BinaryFunction<N> mixer,  double addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myStorage) {
             int tmpIndex = myStorage.index(key);
             double oldValue = tmpIndex >= 0 ? myStorage.doubleValueInternally(tmpIndex) : PrimitiveMath.NaN;
             double newValue = tmpIndex >= 0 ? mixer.invoke(oldValue, addend) : addend;
            myStorage.put(key, tmpIndex, newValue);
            return newValue;
        }
    }

    @Override public N mix( long key,  BinaryFunction<N> mixer,  N addend) {
        ProgrammingError.throwIfNull(mixer);
        synchronized (myStorage) {
             int tmpIndex = myStorage.index(key);
             N oldValue = tmpIndex >= 0 ? myStorage.getInternally(tmpIndex) : null;
             N newValue = tmpIndex >= 0 ? mixer.invoke(oldValue, addend) : addend;
            myStorage.put(key, tmpIndex, newValue);
            return newValue;
        }
    }

    @Override public NonzeroView<N> nonzeros() {
        return myStorage.nonzeros();
    }

    public double put( long key,  double value) {
         int index = myStorage.index(key);
         double oldValue = index >= 0 ? myStorage.doubleValueInternally(index) : PrimitiveMath.NaN;
        myStorage.put(key, index, value);
        return oldValue;
    }

    public N put( long key,  N value) {
         int index = myStorage.index(key);
         N oldValue = index >= 0 ? myStorage.getInternally(index) : null;
        myStorage.put(key, index, value);
        return oldValue;
    }

    @Override public N put( Long key,  N value) {
        return this.put(key.longValue(), value);
    }

    public void putAll( LongToNumberMap<N> m) {
        if (myStorage.isPrimitive()) {
            for ( NonzeroView<N> tmpView : m.getStorage().nonzeros()) {
                myStorage.set(tmpView.index(), tmpView.doubleValue());
            }
        } else {
            for ( NonzeroView<N> tmpView : m.getStorage().nonzeros()) {
                myStorage.set(tmpView.index(), tmpView.get());
            }
        }
    }

    @Override public void putAll( Map<? extends Long, ? extends N> m) {
        for ( Entry<? extends Long, ? extends N> tmpEntry : m.entrySet()) {
            myStorage.set(tmpEntry.getKey(), tmpEntry.getValue());
        }
    }

    public N remove( long key) {
         int index = myStorage.index(key);
         N oldValue = index >= 0 ? myStorage.getInternally(index) : null;
        myStorage.remove(key, index);
        return oldValue;
    }

    @Override public N remove( Object key) {
        if (key instanceof Comparable) {
            return this.remove(NumberDefinition.longValue((Comparable<?>) key));
        } else {
            return null;
        }
    }

    @Override public int size() {
        return myStorage.getActualLength();
    }

    public LongToNumberMap<N> subMap( long fromKey,  long toKey) {

         LongToNumberMap<N> retVal = new LongToNumberMap<>(myStrategy);

        @Var long tmpKey;
        for ( NonzeroView<N> tmpView : myStorage.nonzeros()) {
            tmpKey = tmpView.index();
            if ((fromKey <= tmpKey) && (tmpKey < toKey)) {
                 N tmpValue = tmpView.get();
                retVal.put(tmpKey, tmpValue);
            }
        }

        return retVal;
    }

    @Override public LongToNumberMap<N> subMap( Long fromKey,  Long toKey) {
        return this.subMap(fromKey.longValue(), toKey.longValue());
    }

    public LongToNumberMap<N> tailMap( long fromKey) {
        return this.subMap(fromKey, myStorage.lastIndex() + 1L);
    }

    @Override public LongToNumberMap<N> tailMap( Long fromKey) {
        return this.tailMap(fromKey.longValue());
    }

    @Override
    public String toString() {

         NonzeroView<N> nz = myStorage.nonzeros();

        if (!nz.hasNext()) {
            return "{}";
        }

         var builder = new StringBuilder();
        builder.append('{');
        for (;;) {
             NonzeroView<N> entry = nz.next();
             long key = entry.index();
             N value = entry.get();
            builder.append(key);
            builder.append('=');
            builder.append(value);
            if (!nz.hasNext()) {
                return builder.append('}').toString();
            }
            builder.append(',').append(' ');
        }
    }

    @Override public NumberList<N> values() {
        return new NumberList<>(myStorage.getValues(), myStrategy, myStorage.getActualLength());
    }

    /**
     * Should return the same elements/values as first calling {@link #subMap(Long, Long)} and then
     * {@link #values()} but this method does not create any copies. Any change in the underlying data
     * structure (this map) will corrupt this method's output.
     */
    public Access1D<N> values( long fromKey,  long toKey) {
        return myStorage.getValues(fromKey, toKey);
    }

    SparseArray<N> getStorage() {
        return myStorage;
    }

}
